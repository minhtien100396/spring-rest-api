package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.LoginDTO;
import vn.hoidanit.jobhunter.domain.dto.ResLoginDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/auth/login")
    @ApiMessage("Login success")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());
        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set thong tin nguoi dung vao context SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Tao doi tuong gui ve client
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());

            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());

        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefrectToken(loginDTO.getUsername(), res);

        // update user
        this.userService.updateUserToken(loginDTO.getUsername(), refresh_token);

        // set cookies
        ResponseCookie resCookie = ResponseCookie
                // Tên cookie là "refresh_token", giá trị là token
                .from("refresh_token", refresh_token)
                // Nếu website bị XSS (Cross-site Scripting), hacker có thể chèn JS và đánh cắp
                // cookie
                // httpOnly: lưu refresh token ở nơi JavaScript không thể truy cập
                .httpOnly(true)
                // Chỉ gửi cookie qua HTTPS
                .secure(true)
                // Cookie hợp lệ với mọi route
                .path("/")
                // Thời gian sống (tính bằng giây)
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                // Set cookie vào header
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    // Su dung khi F5 trang van luu tru nguoi dung
    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        // Lấy email (username) từ token JWT
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(email);
            userLogin.setName(currentUserDB.getName());
            userGetAccount.setUser(userLogin);
        }
        return ResponseEntity.ok().body(userGetAccount);
    }

    // Su dung khi access token het han
    @GetMapping("/auth/refresh")
    @ApiMessage("get User By refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
            throws IdInvalidException {

        if (refresh_token.equals("abc")) {
            throw new IdInvalidException("Ban khong co refresh token o cookie");
        }
        // check token
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token khong hop le");
        }

        // Tao doi tuong gui ve client
        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());

            res.setUser(userLogin);
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(email, res.getUser());

        res.setAccessToken(access_token);

        String new_refresh_token = this.securityUtil.createRefrectToken(email, res);

        // update user
        this.userService.updateUserToken(email, new_refresh_token);

        // set cookies
        ResponseCookie resCookie = ResponseCookie
                // Tên cookie là "refresh_token", giá trị là token
                .from("refresh_token", new_refresh_token)
                // Nếu website bị XSS (Cross-site Scripting), hacker có thể chèn JS và đánh cắp
                // cookie
                // httpOnly: lưu refresh token ở nơi JavaScript không thể truy cập
                .httpOnly(true)
                // Chỉ gửi cookie qua HTTPS
                .secure(true)
                // Cookie hợp lệ với mọi route
                .path("/")
                // Thời gian sống (tính bằng giây)
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                // Set cookie vào header
                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout Success")
    public ResponseEntity<Void> logout(@CookieValue("refresh_token") String refresh_token) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token khong hop le");
        }

        this.userService.updateUserToken(email, null);

        ResponseCookie deleteSpringCookie = ResponseCookie
                // Tên cookie là "refresh_token", giá trị là token
                .from("refresh_token", null)
                // Nếu website bị XSS (Cross-site Scripting), hacker có thể chèn JS và đánh cắp
                // cookie
                // httpOnly: lưu refresh token ở nơi JavaScript không thể truy cập
                .httpOnly(true)
                // Chỉ gửi cookie qua HTTPS
                .secure(true)
                // Cookie hợp lệ với mọi route
                .path("/")
                // Thời gian sống (tính bằng giây)
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .build();
    }
}
