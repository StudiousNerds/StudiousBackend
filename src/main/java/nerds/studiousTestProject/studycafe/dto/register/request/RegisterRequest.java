package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.room.dto.register.RegisterRoomRequest;

import java.util.List;

@Builder
@Data
public class RegisterRequest {
    @NotNull(message = "사업자 정보는 필수입니다.")
    @Valid
    private RegisterBusinessmanRequest businessman;

    @NotNull(message = "스터디카페 정보는 필수입니다.")
    @Valid
    private RegisterStudycafeRequest studycafe;

    @NotNull(message = "스터디룸 정보는 필수입니다.")
    @Size(min = 1, message = "스터디룸 정보는 필수입니다.")
    @Valid
    private List<RegisterRoomRequest> rooms;
}