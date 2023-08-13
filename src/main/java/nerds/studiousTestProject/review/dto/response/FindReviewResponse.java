package nerds.studiousTestProject.review.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class FindReviewResponse {
    private Double grade;
    private String nickname;
    private String detail;
    private LocalDate date;
    private String[] photos;
}
