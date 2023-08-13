package nerds.studiousTestProject.payment.dto.cancel.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.util.fromtoss.Cancels;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CancelResponse {

    private int cancelAmount;
    private String canceledAt;

    public static CancelResponse of(Cancels cancel) {
        return CancelResponse.builder()
                .canceledAt(cancel.getCanceledAt())
                .cancelAmount(cancel.getCancelAmount())
                .build();
    }
}
