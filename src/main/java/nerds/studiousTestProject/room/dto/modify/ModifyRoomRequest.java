package nerds.studiousTestProject.room.dto.modify;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.dto.modify.ModifyConvenienceRequest;

import java.util.List;

@Builder
@Data
public class ModifyRoomRequest {
    private String name;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private Integer price;
    private String type;
    private Integer minUsingTime;
    private List<ModifyConvenienceRequest> conveniences;
}
