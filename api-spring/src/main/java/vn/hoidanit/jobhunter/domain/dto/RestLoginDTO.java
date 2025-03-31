package vn.hoidanit.jobhunter.domain.dto;

public class RestLoginDTO {
    private String accessToken;

    public RestLoginDTO(String accessToken) {
        this.accessToken = accessToken;
    }

    public RestLoginDTO() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
