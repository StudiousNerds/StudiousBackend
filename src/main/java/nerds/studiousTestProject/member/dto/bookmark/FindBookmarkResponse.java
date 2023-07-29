package nerds.studiousTestProject.member.dto.bookmark;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindBookmarkResponse {
    private Integer pageNumber;
    private Integer totalRecord;
    private Long cafeId;
    private String cafeName;
    private String photo;
    private Integer accumRevCnt;
    private String nearestStation;
    private Integer distance;
    private Double grade;
    private String[] hashtags;
}
