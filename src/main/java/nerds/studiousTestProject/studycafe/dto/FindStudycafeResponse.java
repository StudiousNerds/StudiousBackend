package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.dto.FindReviewResponse;
import nerds.studiousTestProject.room.dto.FindRoomResponse;

import java.util.List;

@Data
@Builder
public class FindStudycafeResponse {
    private String cafeName;
    private String[] photos;
    private Integer accumResCnt;
    private Integer duration;
    private String[] hashtags;
    private String introduction;
    private String[] conveniences;
    private String notification;
    private List<Integer> refundPolicy;
    private String[] notice;
    private List<FindRoomResponse> rooms;
    private Double recommendationRate;
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixturesStatus;
    private Double total;
    private List<FindReviewResponse> reviewInfo;
}
