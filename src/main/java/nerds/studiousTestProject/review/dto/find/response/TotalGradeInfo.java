package nerds.studiousTestProject.review.dto.find.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalGradeInfo {
    private Integer recommendationRate;
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixturesStatus;
    private Double total;
}
