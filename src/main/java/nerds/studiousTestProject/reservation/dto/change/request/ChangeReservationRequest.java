package nerds.studiousTestProject.reservation.dto.change.request;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.reservation.dto.change.response.PaidConvenienceInfo;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangeReservationRequest {

    @NotNull(message = "추가 결제 총 가격은 필수입니다.")
    private Integer price;
    private Integer headCount;
    private List<PaidConvenienceInfo> conveniences;

    public boolean isBothNull() {
        return headCount == null && conveniences == null;
    }

}
