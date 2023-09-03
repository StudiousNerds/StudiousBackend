package nerds.studiousTestProject.member.dto.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.member.util.JwtTokenConst;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokenResponse {
    private String grantType;
    private String accessToken;

    public static JwtTokenResponse from(String accessToken) {
        return JwtTokenResponse.builder()
                .grantType(JwtTokenConst.TOKEN_PREFIX)
                .accessToken(accessToken)
                .build();
    }
}
