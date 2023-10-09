package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;

@Data
public class SearchResponseInfo {
    private Long id;
    private String name;
    private String photo;
    private Integer reflectedAccumResCnt;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private String accumHashtagHistoryNames;
    private String hashtagRecordNames;
    private Double reflectedGradeSum;
    private Integer reflectedGradeCount;
    private Double gradeSum;
    private Integer gradeCount;

    @Builder
    public SearchResponseInfo(Long id, String name, String photo, Integer reflectedAccumResCnt, Integer accumRevCnt, Integer walkingTime, String nearestStation, String accumHashtagHistoryNames, String hashtagRecordNames, Double reflectedGradeSum, Integer reflectedGradeCount, Double gradeSum, Integer gradeCount) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.reflectedAccumResCnt = reflectedAccumResCnt;
        this.accumRevCnt = accumRevCnt;
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
        this.accumHashtagHistoryNames = accumHashtagHistoryNames;
        this.hashtagRecordNames = hashtagRecordNames;
        this.reflectedGradeSum = reflectedGradeSum;
        this.reflectedGradeCount = reflectedGradeCount;
        this.gradeSum = gradeSum;
        this.gradeCount = gradeCount;
    }
}
