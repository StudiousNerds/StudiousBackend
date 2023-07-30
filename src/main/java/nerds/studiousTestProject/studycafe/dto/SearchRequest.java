package nerds.studiousTestProject.studycafe.dto;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.ConvenienceName;
import nerds.studiousTestProject.studycafe.entity.hashtag.HashtagName;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Data
public class SearchRequest {
    private String keyword;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer headCount;
    private Integer minGrade;
    private Boolean eventInProgress;
    private List<HashtagName> hashtags;
    private List<ConvenienceName> conveniences;
    private SortType sortType;
}
