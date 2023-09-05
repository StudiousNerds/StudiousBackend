package nerds.studiousTestProject.review.dto.manage.inquire;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.review.entity.Review;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ReviewInfo {
    private List<String> photos;
    private String detail;
    private LocalDate writeDate;

    public static ReviewInfo from(Review review, List<SubPhoto> subPhotos) {
        List<String> photos = new ArrayList<>();
        photos.add(review.getPhoto());
        photos.addAll(subPhotos.stream().map(SubPhoto::getPath).toList());

        return ReviewInfo.builder()
                .detail(review.getDetail())
                .writeDate(review.getCreatedDate())
                .photos(photos)
                .build();
    }
}
