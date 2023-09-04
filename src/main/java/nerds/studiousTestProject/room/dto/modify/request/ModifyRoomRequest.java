package nerds.studiousTestProject.room.dto.modify.request;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaidConvenience;

import java.util.List;

@Builder
@Data
public class ModifyRoomRequest {
    private Long roomId;
    private String roomName;
    private Integer minCount;
    private Integer maxCount;
    private Integer price;
    private String type;
    private Integer minUsingTime;
    private List<String> photos;
    private List<String> conveniences;
    private List<PaidConvenience> paidConveniences;
}
