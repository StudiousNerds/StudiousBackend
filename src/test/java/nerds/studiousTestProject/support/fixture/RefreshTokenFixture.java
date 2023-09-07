package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.token.RefreshToken;

public enum RefreshTokenFixture {
    FIRST_REFRESH_TOKEN("refreshToken", 10000L);

    private final String token;
    private final Long expiration;

    RefreshTokenFixture(String token, Long expiration) {
        this.token = token;
        this.expiration = expiration;
    }

    public RefreshToken 생성() {
        return 기본_정보_빌더_생성().build();
    }

    public RefreshToken 멤버_ID_생성(Long memberId) {
        return 기본_정보_빌더_생성().memberId(memberId).build();
    }

    public RefreshToken.RefreshTokenBuilder 기본_정보_빌더_생성() {
        return RefreshToken.builder()
                .token(this.token)
                .expiration(this.expiration);
    }
}
