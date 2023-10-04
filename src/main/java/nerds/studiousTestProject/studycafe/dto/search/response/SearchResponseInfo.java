package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;

@Data
public class SearchResponseInfo {
    private Long id;
    private String name;
    private String photo;
    private Integer reflectedAccumResCnt;
    private Integer accumResCnt;
    private Integer walkingTime;
    private String nearestStation;
    private String accumHashtagHistoryNames;
    private String hashtagRecordNames;
    private Double reflectedTotalGrade;
    private Double totalGrade;

    @Builder
    public SearchResponseInfo(Long id, String name, String photo, Integer reflectedAccumResCnt, Integer accumResCnt, Integer walkingTime, String nearestStation, String accumHashtagHistoryNames, String hashtagRecordNames, Double reflectedTotalGrade, Double totalGrade) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.reflectedAccumResCnt = reflectedAccumResCnt;
        this.accumResCnt = accumResCnt;
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
        this.accumHashtagHistoryNames = accumHashtagHistoryNames;
        this.hashtagRecordNames = hashtagRecordNames;
        this.reflectedTotalGrade = reflectedTotalGrade;
        this.totalGrade = totalGrade;
    }
}
