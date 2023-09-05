package nerds.studiousTestProject.room.dto.modify.request;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaidConvenience;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@Builder
@Data
public class ModifyRoomRequest {
    private String roomName;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private Integer price;
    private String type;
    private Integer minUsingTime;
    private List<String> conveniences;
    private List<PaidConvenience> paidConveniences;

    public static Room toEntity(Studycafe studycafe, Long roomId, ModifyRoomRequest modifyRoomRequest) {
        return Room.builder()
                .id(roomId)
                .name(modifyRoomRequest.getRoomName())
                .minHeadCount(modifyRoomRequest.getMinHeadCount())
                .maxHeadCount(modifyRoomRequest.getMaxHeadCount())
                .price(modifyRoomRequest.getPrice())
                .type(PriceType.valueOf(modifyRoomRequest.getType()))
                .minUsingTime(modifyRoomRequest.getMinUsingTime())
                .studycafe(studycafe)
                .build();
    }
}
