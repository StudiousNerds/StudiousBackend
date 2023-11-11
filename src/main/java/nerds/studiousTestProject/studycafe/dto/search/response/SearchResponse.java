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
    private List<HashtagName> hashtags;
    private Double grade;
    private Boolean bookmarked;

    public static SearchResponse of(SearchInResponse searchInResponse, List<HashtagName> hashtags, Double grade) {
        return SearchResponse.builder()
                .id(searchInResponse.getId())
                .id(searchInResponse.getId())
                .name(searchInResponse.getName())
                .photo(searchInResponse.getPhoto())
                .accumRevCnt(searchInResponse.getAccumRevCnt())
                .walkingTime(searchInResponse.getWalkingTime())
                .nearestStation(searchInResponse.getNearestStation())
                .hashtags(hashtags)
                .grade(grade)
                .bookmarked(searchInResponse.getBookmarked())
                .build();
    }
}
