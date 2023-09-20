package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Builder
@Data
public class SearchResponse {
    private Long id;
    private String name;
    private String photo;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private Double grade;

    public SearchResponse(Long id, String name, String photo, Integer accumRevCnt, Integer walkingTime, String nearestStation, Double grade) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.accumRevCnt = accumRevCnt;
        this.walkingTime = walkingTime;
        this.nearestStation = nearestStation;
        this.grade = grade;
    }

    public static SearchResponse from(Studycafe studycafe) {
        return SearchResponse.builder()
                .id(studycafe.getId())
                .name(studycafe.getName())
                .photo(studycafe.getPhoto())
                .grade(studycafe.getTotalGrade())
                .accumRevCnt(studycafe.getAccumReserveCount())
                .walkingTime(studycafe.getWalkingTime())
                .nearestStation(studycafe.getNearestStation())
                .build();
    }
}
