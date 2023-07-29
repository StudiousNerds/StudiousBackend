package nerds.studiousTestProject.payment.dto.cancel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.payment.dto.confirm.request.Cancels;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
