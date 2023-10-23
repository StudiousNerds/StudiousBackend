package nerds.studiousTestProject.member.dto.login.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.member.dto.enquiry.response.MenuBarProfileResponse;
import nerds.studiousTestProject.member.dto.token.JwtTokenResponse;
import nerds.studiousTestProject.member.entity.member.Member;

@Builder
@Data
public class LoginResponse {
    private JwtTokenResponse tokenInfo;
    private MenuBarProfileResponse profile;

    public static LoginResponse from(JwtTokenResponse tokenInfo, Member member) {
        return LoginResponse.builder()
                .tokenInfo(tokenInfo)
                .profile(MenuBarProfileResponse.from(member))
                .build();
    }
}
