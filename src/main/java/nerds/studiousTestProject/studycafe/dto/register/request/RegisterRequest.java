package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class RegisterRequest {
    @NotNull(message = "사업자 정보는 필수입니다.")
    @Valid
    private BusinessInfoRequest businessInfo;

    @NotNull(message = "스터디카페 정보는 필수입니다.")
    @Valid
    private CafeInfoRequest cafeInfo;

    @NotNull(message = "스터디룸 정보는 필수입니다.")
    @Size(min = 1, message = "스터디룸 정보는 필수입니다.")
    @Valid
    private List<RoomInfoRequest> roomInfos;
}