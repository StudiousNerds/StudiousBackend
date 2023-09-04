package nerds.studiousTestProject.room.dto.find.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaidConvenience;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class FindRoomResponse {
    private Long id;
    private String name;
    private Integer minCount;
    private Integer maxCount;
    private Integer price;
    private String type;
    private Integer minUsingTime;
    private List<String> photos;
    private Map<String, Integer[]> canReserveDatetime;
    private List<String> conveniences;
    private List<PaidConvenience> paidConveniences;
}
