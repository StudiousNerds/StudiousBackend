package nerds.studiousTestProject.payment.dto.confirm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveUserInfo {

    private String name;

    private String phoneNumber;

    private String request;

}
