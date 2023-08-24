package nerds.studiousTestProject.studycafe.dto.enquiry.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.room.dto.FindRoomResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.AnnouncementInResponse;

import java.util.List;

@Builder
@Data
public class FindStudycafeResponse {
    private Long cafeId;
    private String cafeName;
    private List<String> photos;
    private Integer accumResCnt;
    private Integer duration;
    private String nearestStation;
    private List<String> hashtags;
    private String introduction;
    private List<String> conveniences;
    private List<AnnouncementInResponse> announcement;
    private List<FindRoomResponse> rooms;
}
