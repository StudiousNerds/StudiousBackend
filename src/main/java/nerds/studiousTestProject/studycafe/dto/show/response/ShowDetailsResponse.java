package nerds.studiousTestProject.studycafe.dto.show.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.room.dto.show.ShowRoomResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.AnnouncementInResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@Builder
@Data
public class ShowDetailsResponse {
    private Long studycafeId;
    private String cafeName;
    private List<String> photos;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private List<String> hashtags;
    private String introduction;
    private List<String> conveniences;
    private List<AnnouncementInResponse> announcements;
    private List<ShowRoomResponse> rooms;

    public static ShowDetailsResponse of(final Studycafe studycafe,
                                         final List<String> photos,
                                         final List<ShowRoomResponse> rooms) {
        return ShowDetailsResponse.builder()
                .studycafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(photos)
                .accumRevCnt(studycafe.getAccumReserveCount())
                .walkingTime(studycafe.getWalkingTime())
                .nearestStation(studycafe.getNearestStation())
                .hashtags(studycafe.getAccumHashtagHistories().stream().map(h -> h.getName().name()).toList())
                .introduction(studycafe.getIntroduction())
                .conveniences(studycafe.getConveniences().stream().map(c -> c.getName().name()).toList())
                .announcements(studycafe.getAnnouncements().stream().map(AnnouncementInResponse::from).toList())
                .rooms(rooms)
                .build();
    }
}
