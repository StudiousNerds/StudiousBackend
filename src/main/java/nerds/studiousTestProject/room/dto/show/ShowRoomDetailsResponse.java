package nerds.studiousTestProject.room.dto.show;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.reservation.dto.show.response.PaidConvenienceResponse;
import nerds.studiousTestProject.room.entity.Room;

import java.util.List;

@Builder
@Data
public class ShowRoomDetailsResponse {
    private String name;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private Integer price;
    private String priceType;
    private Integer minUsingTime;
    private List<String> photos;
    private List<String> conveniences;
    private List<PaidConvenienceResponse> paidConvenienceResponses;

    public static ShowRoomDetailsResponse from(Room room) {
        return ShowRoomDetailsResponse.builder()
                .name(room.getName())
                .minHeadCount(room.getMinHeadCount())
                .maxHeadCount(room.getMaxHeadCount())
                .price(room.getPrice())
                .priceType(room.getPriceType().name())
                .minUsingTime(room.getMinUsingTime())
                .photos(room.getSubPhotos().stream().map(SubPhoto::getPath).toList())
                .conveniences(room.getConveniences().stream().filter(Convenience::isFree).map(c -> c.getName().name()).toList())
                .paidConvenienceResponses(room.getConveniences().stream().filter(c -> !c.isFree()).map(c -> new PaidConvenienceResponse(c.getName().name(), c.getPrice())).toList())
                .build();
    }
}
