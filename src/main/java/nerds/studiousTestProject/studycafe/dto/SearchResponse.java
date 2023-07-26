package nerds.studiousTestProject.studycafe.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchResponse {
    private Long id;
    private String name;
    private String photo;
    private Integer accumRevCnt;
    private Integer duration;
    private Double grade;

    @QueryProjection
    public SearchResponse(Long id, String name, String photo, Integer accumRevCnt, Integer duration, Double grade) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.accumRevCnt = accumRevCnt;
        this.duration = duration;
        this.grade = grade;
    }
}
