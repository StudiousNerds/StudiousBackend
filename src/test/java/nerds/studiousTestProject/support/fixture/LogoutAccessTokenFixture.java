package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.token.LogoutAccessToken;

public enum LogoutAccessTokenFixture {
    FIRST_LOGOUT_ACCESS_TOKEN("logoutAccessToken", 10000L);

    private final String token;
    private final Long expiration;

    LogoutAccessTokenFixture(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public LogoutAccessToken 생성() {
        return 기본_정보_빌더_생성().build();
    }

    public LogoutAccessToken.LogoutAccessTokenBuilder 기본_정보_빌더_생성() {
        return LogoutAccessToken.builder()
                .token(this.token)
                .expiration(this.expiration);
    }
}
