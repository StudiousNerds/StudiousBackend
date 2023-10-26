package nerds.studiousTestProject.member.dto.oauth.authenticate.userinfo;

import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getInstance(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new NotFoundException(ErrorCode.NOT_FOUND_SOCIAL_INFO);
        };
    }
}
