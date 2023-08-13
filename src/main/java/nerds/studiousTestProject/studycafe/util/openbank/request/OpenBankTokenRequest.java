package nerds.studiousTestProject.studycafe.util.openbank.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OpenBankTokenRequest {
    private String client_id;
    private String client_secret;
    private String scope;
    private String grant_type;
}
