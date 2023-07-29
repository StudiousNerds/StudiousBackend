package nerds.studiousTestProject.member.dto.oauth.signup;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.dto.general.token.JwtTokenResponse;

@Data
@Builder
public class OAuth2AuthenticateResponse {
    private boolean exist;
    private JwtTokenResponse jwtTokenResponse;
    private UserInfo userInfo;

    @Getter
    @Setter
    @Builder
    public static class UserInfo {
        Long providerId;
        String email;
        MemberType type;
    }
}