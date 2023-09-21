package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class SearchResponse {
    private Long id;
    private String name;
    private String photo;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private List<String> hashtagNames;
    private Double grade;

    public static SearchResponse from(Studycafe studycafe) {
        SearchResponse searchResponse = SearchResponse.builder()
                .id(studycafe.getId())
                .name(studycafe.getName())
                .photo(studycafe.getPhoto())
                .grade(studycafe.getTotalGrade())
                .accumRevCnt(studycafe.getAccumReserveCount())
                .walkingTime(studycafe.getWalkingTime())
                .nearestStation(studycafe.getNearestStation())
                .build();

        List<String> hashtagNames = studycafe.getAccumHashtagHistories().stream().map(e -> e.getName().name()).toList();
        searchResponse.setHashtagNames(hashtagNames);
        System.out.println(hashtagNames);

        return searchResponse;
    }
}
