package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class FindReviewInfo {
    private String nickname;
    private String roomName;
    private Integer minHeadCount;
    private Integer maxHeadCount;
    private String detail;
    private LocalDate date;
    private List<String> photos;
}
