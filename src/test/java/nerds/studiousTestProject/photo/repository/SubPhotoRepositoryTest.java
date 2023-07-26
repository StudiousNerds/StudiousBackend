package nerds.studiousTestProject.photo.repository;

import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.review.repository.ReviewRepository;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SubPhotoRepositoryTest {
    @Autowired
    private SubPhotoRepository subPhotoRepository;
    @Autowired
    private StudycafeRepository studycafeRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    void findAllByReviewId() {
        // given
        Review review = reviewRepository.save(Review.builder().id(1L).build());
        SubPhoto subPhoto = subPhotoRepository.save(SubPhoto.builder().review(review).url("www.spring.com").build());
        SubPhoto subPhoto1 = subPhotoRepository.save(SubPhoto.builder().review(review).url("www.sub.com").build());
        // when
        List<SubPhoto> reviewPhotoList = subPhotoRepository.findAllByReviewId(review.getId());
        // then
        Assertions.assertThat(reviewPhotoList).contains(subPhoto, subPhoto1);
    }

    @Test
    void findAllByStudycafeId() {
        // given
        Studycafe studycafe = studycafeRepository.save(Studycafe.builder().id(1L).build());
        SubPhoto subPhoto = subPhotoRepository.save(SubPhoto.builder().studycafe(studycafe).url("www.spring.com").build());
        SubPhoto subPhoto1 = subPhotoRepository.save(SubPhoto.builder().studycafe(studycafe).url("www.sub.com").build());
        // when
        List<SubPhoto> studycafePhotoList = subPhotoRepository.findAllByStudycafeId(1L);
        // then
        Assertions.assertThat(studycafePhotoList).contains(subPhoto, subPhoto1);
    }
}