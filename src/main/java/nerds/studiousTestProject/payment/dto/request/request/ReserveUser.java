package nerds.studiousTestProject.payment.dto.request.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ReserveUser {

    @NotNull(message = "예약자명은 필수 입니다.")
    private String name;

    @NotNull(message = "예약자 전화번호는 필수 입니다.")
    private String phoneNumber;

    @Nullable
    private String request;

}
