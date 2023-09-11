package nerds.studiousTestProject.room.dto.modify.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class ModifyRoomResponse {
    private Long roomId;
    private LocalDate modifiedAt;
}
