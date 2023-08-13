package nerds.studiousTestProject.payment.dto.request.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class PaymentResponse {

    private int amount;

    private String orderId;

    private String orderName;

    private String successUrl;

    private String failUrl;

}



