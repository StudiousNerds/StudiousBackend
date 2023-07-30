package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmFailResponse {
    private String message;
    private int statusCode;
}
