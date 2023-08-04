package nerds.studiousTestProject.studycafe.util.openbank.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenBankTokenResponse {
    private String access_token;
    private String token_type;
    private String expires_in;
    private String oop;
    private String client_use_code;
}
