package nerds.studiousTestProject.studycafe.dto.search.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.hashtag.entity.HashtagName;

import java.util.List;

@Builder
@Data
public class SearchResponse {
    private Long id;
    private String name;
    private String photo;
    private Integer accumRevCnt;
    private Integer walkingTime;
    private String nearestStation;
    private List<HashtagName> hashtagNames;
    private Double grade;

    public static SearchResponse from(SearchResponseInfo searchResponseInfo, List<HashtagName> hashtagNames, Integer accumRevCnt, Double grade) {
        return SearchResponse.builder()
                .id(searchResponseInfo.getId())
                .id(searchResponseInfo.getId())
                .name(searchResponseInfo.getName())
                .photo(searchResponseInfo.getPhoto())
                .accumRevCnt(accumRevCnt)
                .walkingTime(searchResponseInfo.getWalkingTime())
                .nearestStation(searchResponseInfo.getNearestStation())
                .hashtagNames(hashtagNames)
                .grade(grade)
                .build();
    }
}
