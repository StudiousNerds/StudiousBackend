package nerds.studiousTestProject.room.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class FindRoomResponse {
    private Long id;
    private String name;
    private String[] photos;
    private Map<LocalDate, Integer[]> canReserveDatetime;
    private String[] conveniences;
}
