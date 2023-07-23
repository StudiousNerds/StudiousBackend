package nerds.studiousTestProject.review.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FindReviewResponse {
    private Integer grade;
    private String email;
    private String detail;
    private LocalDate date;
    private String[] photos;
}
