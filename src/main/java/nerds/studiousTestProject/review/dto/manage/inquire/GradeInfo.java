package nerds.studiousTestProject.review.dto.manage.inquire;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.review.entity.Grade;

@Builder
@Data
public class GradeInfo {
    private Integer cleanliness;
    private Integer deafening;
    private Integer fixturesStatus;

    public static GradeInfo from(Grade grade) {
        return GradeInfo.builder()
                .cleanliness(grade.getCleanliness())
                .deafening(grade.getDeafening())
                .fixturesStatus(grade.getFixturesStatus())
                .build();
    }
}
