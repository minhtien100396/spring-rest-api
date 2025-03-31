package vn.hoidanit.jobhunter.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import vn.hoidanit.jobhunter.util.SecurityUtil;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Value("${hoidanit.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    // tiem component customAuthenticationEntryPoint vao de su dung
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
        http
                .csrf(c -> c.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        authz -> authz
                                // khong can xac thuc van truy cap duoc
                                .requestMatchers("/", "/login").permitAll()
                                // phai xac thuc moi duoc truy cap
                                .anyRequest().authenticated())
                // dung de xac thuc nguoi dung thong qua ham jwtDecoder()
                // Tại phía Server của Spring (sau khi đã cấu hình oauth2-resource-server), sẽ
                // kích hoạt filter BearerTokenAuthenticationFilter
                // Filter này sẽ “tự động tách” Bear Token (bạn không cần phải làm thủ công, thư
                // viện đã làm sẵn rồi)
                .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults())
                        // them xu ly authenticationEntryPoint custom
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                // xu ly exception
                // .exceptionHandling(
                // exceptions -> exceptions
                // .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint()) // 401
                // .accessDeniedHandler(new BearerTokenAccessDeniedHandler())) // 403
                .formLogin(f -> f.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    // 1. Khi một người dùng gửi request với JWT hợp lệ, JwtAuthenticationConverter
    // sẽ trích xuất thông tin từ token.
    // 2. Spring Security sẽ phan quyen (authorities) cho người dùng, dựa trên
    // claims như roles, authorities.
    // 3. Nếu quyền của người dùng khớp với yêu cầu của route, họ sẽ được phép truy
    // cập.
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("hoidanit");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    // giai ma token
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
                getSecretKey()).macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    // JwtEncoder giúp mã hóa JWT trước khi gửi về client.
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey())); // getSecretKey() : tao Key dang mang byte
    }

    // Phương thức getSecretKey() giúp giải mã secret key từ Base64 thành mảng byte,
    // sau đó chuyển nó thành một SecretKey, dùng để mã hóa JWT.
    // Tao key
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode(); // Giai ma key tu Base64 sang byte
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

}
