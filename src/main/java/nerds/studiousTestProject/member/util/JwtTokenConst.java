package nerds.studiousTestProject.member.util;

public class JwtTokenConst {

    // Token
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24;        // ACCESS 토큰 만료 시간 (24시간)
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 6;   // REFRESH 토큰 만료 시간 (6시간)
    public static final Long REISSUE_EXPIRE_TIME = 1000L * 60 * 60 * 3;         // Reissue 만료 시간 (3시간)

    // header
    public static final String ALG_KEY = "alg";
    public static final String TYPE_KEY = "typ";
    public static final String TYPE_VALUE = "JWT";
    public static final String TOKEN_PREFIX = "Bearer";
    public static final String REFRESH_TOKEN_COOKIE_PREFIX = "refresh_token";
    public static final String AUTHORITIES_KEY = "auth";
}
