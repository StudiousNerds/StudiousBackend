package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;

@Data
public class SearchResponseInfo {
    private Long id;
    private String name;
    private String photo;
    private Integer reflectedAccumRevCnt;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private String accumHashtags;
    private String hashtags;
    private Double reflectedGradeSum;
    private Integer reflectedGradeCount;
    private Double gradeSum;
    private Integer gradeCount;

    @Builder
    public SearchResponseInfo(Long id, String name, String photo, Integer reflectedAccumRevCnt, Integer accumRevCnt, Integer walkingTime, String nearestStation, String accumHashtags, String hashtags, Double reflectedGradeSum, Integer reflectedGradeCount, Double gradeSum, Integer gradeCount) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.reflectedAccumRevCnt = reflectedAccumRevCnt;
        this.accumRevCnt = accumRevCnt;
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
        this.accumHashtags = accumHashtags;
        this.hashtags = hashtags;
        this.reflectedGradeSum = reflectedGradeSum;
        this.reflectedGradeCount = reflectedGradeCount;
        this.gradeSum = gradeSum;
        this.gradeCount = gradeCount;
    }
}
