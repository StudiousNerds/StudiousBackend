package nerds.studiousTestProject.review.dto.enquiry.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.review.entity.Review;

import java.util.List;

@Builder
@Data
public class ReviewInfoResponse {
    private ReviewInfo reviewInfo;
    private GradeInfo gradeInfo;

    public static ReviewInfoResponse from(Review review, List<SubPhoto> subPhotos) {
        return ReviewInfoResponse.builder()
                .reviewInfo(ReviewInfo.from(review, subPhotos))
                .gradeInfo(GradeInfo.from(review.getGrade()))
                .build();
    }
}
