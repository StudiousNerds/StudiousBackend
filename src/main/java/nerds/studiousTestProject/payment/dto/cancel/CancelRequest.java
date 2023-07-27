package nerds.studiousTestProject.payment.dto.cancel;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelRequest {

    private String cancelReason;

    @Nullable
    private Integer canselAmount;

}
