package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;

@Data
public class SearchInResponse {
    private Long id;
    private String name;
    private String photo;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private String accumHashtags;
    private Double gradeSum;
    private Integer gradeCount;
    private Boolean bookmarked;

    @Builder
    public SearchInResponse(Long id, String name, String photo, Integer accumRevCnt, Integer walkingTime, String nearestStation, String accumHashtags, Double gradeSum, Integer gradeCount, Boolean bookmarked) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.accumRevCnt = accumRevCnt;
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
        this.accumHashtags = accumHashtags;
        this.gradeSum = gradeSum;
        this.gradeCount = gradeCount;
        this.bookmarked = bookmarked;
    }
}
