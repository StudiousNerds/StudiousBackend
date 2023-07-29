package nerds.studiousTestProject.member.dto.oauth.userinfo;

import nerds.studiousTestProject.member.exception.message.ExceptionMessage;
import nerds.studiousTestProject.member.exception.model.OAuth2Exception;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getInstance(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> new GoogleUserInfo(attributes);
            case "naver" -> new NaverUserInfo(attributes);
            case "kakao" -> new KakaoUserInfo(attributes);
            default -> throw new OAuth2Exception(ExceptionMessage.NOT_FOUND_SOCIAL_INFO);
        };
    }
}
