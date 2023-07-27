package nerds.studiousTestProject.payment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {

    @NotEmpty
    private int amount;

    @NotEmpty
    @Size(min = 6, max = 64)
    private String orderId;

    @NotEmpty
    private String orderName;

    @NotEmpty
    private String successUrl;

    @NotEmpty
    private String failUrl;

}



