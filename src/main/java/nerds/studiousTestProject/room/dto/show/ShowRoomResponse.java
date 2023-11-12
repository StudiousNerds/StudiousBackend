package nerds.studiousTestProject.room.dto.show;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.room.entity.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class ShowRoomResponse {
    private Long id;
    private String name;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private Integer price;
    private String priceType;
    private Integer minUsingTime;
    private Map<LocalDate, List<Integer>> canReserveDateTime;
    private String photo;

    public static ShowRoomResponse of(Room room, Map<LocalDate, List<Integer>> canReserveDateTime) {
        return ShowRoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .minHeadCount(room.getMinHeadCount())
                .maxHeadCount(room.getMaxHeadCount())
                .price(room.getPrice())
                .priceType(room.getPriceType().name())
                .photo(room.getSubPhotos().stream().map(SubPhoto::getPath).findFirst().get())
                .canReserveDateTime(canReserveDateTime)
                .build();
    }
}
