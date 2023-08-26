package nerds.studiousTestProject.bookmark.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FindBookmarkResponse {
    private PageInfo pageInfo;
    private Long cafeId;
    private String cafeName;
    private String photo;
    private Integer accumRevCnt;
    private String nearestStation;
    private Integer distance;
    private Double grade;
    private List<String> hashtags;
}
