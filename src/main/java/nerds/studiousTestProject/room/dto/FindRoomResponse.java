package nerds.studiousTestProject.room.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class FindRoomResponse {
    private Long id;
    private String name;
    private Integer standCount;
    private Integer minCount;
    private Integer maxCount;
    private Integer price;
    private String type;
    private Integer minUsingTime;
    private String[] photos;
    private Map<String, Integer[]> canReserveDatetime;
    private String[] conveniences;
}
