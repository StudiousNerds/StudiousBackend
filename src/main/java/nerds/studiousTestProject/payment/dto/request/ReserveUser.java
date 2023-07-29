package nerds.studiousTestProject.payment.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveUser {

    @NotNull(message = "예약자명은 필수 입니다.")
    private String name;

    @NotNull(message = "예약자 전화번호는 필수 입니다.")
    private String phoneNumber;

    @Nullable
    private String request;

}
