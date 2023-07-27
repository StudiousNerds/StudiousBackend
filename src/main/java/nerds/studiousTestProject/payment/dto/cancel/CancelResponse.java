package nerds.studiousTestProject.payment.dto.cancel;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelResponse {

    private int cancelAmount;
    private String canceledAt;

}
