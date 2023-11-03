package nerds.studiousTestProject.room.dto.show;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.room.entity.Room;

@Builder
@Data
public class ShowRoomBasicResponse {
    private Long id;
    private String name;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private Integer price;
    private String priceType;
    private Integer minUsingTime;
    private String photo;
//    private Map<String, Integer[]> canReserveDatetime;

    public static ShowRoomBasicResponse of(Room room) {
        return ShowRoomBasicResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .minHeadCount(room.getMinHeadCount())
                .maxHeadCount(room.getMaxHeadCount())
                .price(room.getPrice())
                .priceType(room.getPriceType().name())
                .minUsingTime(room.getMinUsingTime())
                .photo(room.getSubPhotos().stream().map(SubPhoto::getPath).findFirst().get())
                .build();
    }
}
