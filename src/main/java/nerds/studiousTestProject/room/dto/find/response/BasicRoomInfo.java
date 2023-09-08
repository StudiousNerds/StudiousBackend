package nerds.studiousTestProject.room.dto.find.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.room.entity.Room;

@Builder
@Data
public class BasicRoomInfo {
    private String roomName;
    private Integer minHeadCount;
    private Integer maxHeadCount;

    public static BasicRoomInfo of(Room room) {
        return BasicRoomInfo.builder()
                .roomName(room.getName())
                .minHeadCount(room.getMinHeadCount())
                .minHeadCount(room.getMaxHeadCount())
                .build();
    }
}
