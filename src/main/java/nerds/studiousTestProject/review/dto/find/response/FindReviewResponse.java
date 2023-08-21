package nerds.studiousTestProject.review.dto.find.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class FindReviewResponse {
    private Integer totalPage;
    private Integer currentPage;
    private Double grade;
    private String nickname;
    private String detail;
    private LocalDate date;
    private String roomName;
    private String[] photos;
}
