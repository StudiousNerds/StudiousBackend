package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RegisterRequest {
    @NotNull(message = "사업자 정보는 필수입니다.")
    @Valid
    private BusinessInfo businessInfo;

    @NotNull(message = "스터디카페 정보는 필수입니다.")
    @Valid
    private CafeInfo cafeInfo;

    @NotNull(message = "스터디룸 정보는 필수입니다.")
    @Valid
    private List<RoomInfo> roomInfos;
}