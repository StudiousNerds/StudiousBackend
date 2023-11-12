package nerds.studiousTestProject.reservation.dto.reserve.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ReserveUserRequest {

    @NotBlank(message = "예약자명은 필수 입니다.")
    private String name;

    @NotBlank(message = "예약자 전화번호는 필수 입니다.")
    private String phoneNumber;

    @Nullable
    private String request;

}
