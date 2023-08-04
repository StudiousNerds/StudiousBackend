//package nerds.studiousTestProject;
//
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import nerds.studiousTestProject.convenience.entity.Convenience;
//import nerds.studiousTestProject.convenience.entity.ConvenienceName;
//import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
//import nerds.studiousTestProject.hashtag.entity.Hashtag;
//import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
//import nerds.studiousTestProject.hashtag.repository.HashtagRepository;
//import nerds.studiousTestProject.payment.entity.Payment;
//import nerds.studiousTestProject.payment.repository.PaymentRepository;
//import nerds.studiousTestProject.photo.entity.SubPhoto;
//import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
//import nerds.studiousTestProject.reservation.entity.ReservationRecord;
//import nerds.studiousTestProject.reservation.entity.ReservationStatus;
//import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
//import nerds.studiousTestProject.review.entity.Grade;
//import nerds.studiousTestProject.review.entity.Review;
//import nerds.studiousTestProject.review.repository.GradeRepository;
//import nerds.studiousTestProject.review.repository.ReviewRepository;
//import nerds.studiousTestProject.room.entity.Room;
//import nerds.studiousTestProject.room.repository.RoomRepository;
//import nerds.studiousTestProject.studycafe.entity.Studycafe;
//import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
//import nerds.studiousTestProject.user.entity.member.Member;
//import nerds.studiousTestProject.user.entity.member.MemberType;
//import nerds.studiousTestProject.user.repository.member.MemberRepository;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class Initializer {
//
//    private final RoomRepository roomRepository;
//    private final ReviewRepository reviewRepository;
//    private final ReservationRecordRepository reservationRecordRepository;
//    private final StudycafeRepository studycafeRepository;
//    private final MemberRepository memberRepository;
//    private final SubPhotoRepository subPhotoRepository;
//    private final GradeRepository gradeRepository;
//    private final HashtagRepository hashtagRepository;
//    private final PaymentRepository paymentRepository;
//    private final ConvenienceRepository convenienceRepository;
//
//    @PostConstruct
//    public void init(){
//        List<String> notice = getNotices();
//        List<Integer> refundPolicy = getRefundPolicy();
//        List<String> role = getRole();
//        List<Hashtag> hashtags = getHashtags();
//
//        // 멤버(사장) 등록
//        Member member = memberRepository.save(Member.builder().id(1L).email("choi@naver.com").password("test").name("사장")
//                .nickname("와꾸").type(MemberType.DEFAULT).phoneNumber("01023457123").roles(role).build());
//
//        // 스터디카페(NerdsStudycafe) 등록
//        Studycafe nerdsStudycafe = studycafeRepository.save(Studycafe.builder().id(1L).member(member).name("NerdsStudycafe").photo("www.spring.com")
//                .endTime(LocalTime.of(23, 0)).phoneNumber("0212341234").startTime(LocalTime.of(9, 0)).duration(25)
//                .nearestStation("노원역").accumReserveCount(20).introduction("좋아요").createdDate(LocalDateTime.now()).totalGrade(4.5).notificationInfo("정숙요망")
//                .notice(notice).refundPolicyInfo(refundPolicy).build());
//
//        // 룸 등록
//        Room room1 = roomRepository.save(Room.builder().id(1L).studycafe(nerdsStudycafe).maxCount(6).minCount(4).standCount(4).minUsingTime(60)
//                .price(6000).type("시간당").name("testroom1").studycafe(nerdsStudycafe).build());
//
//
//
//        //--------------------------이곳부터는 nerdystduycafe -----------------------------------------//
//
//        Studycafe nerdyStudycafe = studycafeRepository.save(Studycafe.builder().id(2L).member(member).name("NerdyStudycafe").photo("www.springdfdf.com")
//                .endTime(LocalTime.of(23, 0)).phoneNumber("0212341235").startTime(LocalTime.of(9, 0))
//                .duration(25).nearestStation("노원역").accumReserveCount(20).introduction("좋아d요").createdDate(LocalDateTime.now())
//                .totalGrade(5.0).notificationInfo("정숙요망").notice(notice).refundPolicyInfo(refundPolicy).build());
//
//        // 첫 번째 룸 등록
//        Room room2 = roomRepository.save(Room.builder().id(2L).studycafe(nerdyStudycafe).maxCount(6).minCount(4).standCount(4).minUsingTime(60)
//                .price(6000).type("시간당").name("testroom1").studycafe(nerdsStudycafe).build());
//        // 두 번째 룸 등록
//        Room room3 = roomRepository.save(Room.builder().id(3L).studycafe(nerdyStudycafe).maxCount(6).minCount(4).standCount(4).minUsingTime(60)
//                .price(6000).type("시간당").name("testroom1").studycafe(nerdsStudycafe).build());
//
//
//        // 카페 사진 등록
//        subPhotoRepository.save(SubPhoto.builder().id(1L).studycafe(nerdyStudycafe).url("www.nerdy2.com").build());
//        // 첫 번째 룸 사진 등록
//        subPhotoRepository.save(SubPhoto.builder().id(2L).studycafe(nerdyStudycafe).room(room2).url("www.nerdy1.com").build());
//        // 두 번째 룸 사진 등록
//        subPhotoRepository.save(SubPhoto.builder().id(3L).studycafe(nerdyStudycafe).room(room2).url("www.nerdy.com").build());
//        // 해시태그 등록
//        hashtagRepository.save(HashtagRecord.builder().id(1L).studycafe(nerdyStudycafe).hashtags(hashtags).build()) ;
//        // 결제 내역
//        Payment payment = paymentRepository.save(Payment.builder().id(1L).build());
//        // 예약 내역
//        ReservationRecord record = reservationRecordRepository.save(ReservationRecord.builder().id(1L).member(member).room(room2).payment(payment)
//                .name("최보현").date(LocalDate.of(2023,7,26)).startTime(LocalTime.of(13,0))
//                .endTime(LocalTime.of(15,0)).reservationStatus(ReservationStatus.RESERVED).completePayment(true).build());
//        // 등급 등록
//        Grade grade = gradeRepository.save(Grade.builder().id(1L).cleanliness(5).deafening(5).fixturesStatus(5).total(5.0).isRecommended(true).build());
//        // 리뷰 등록
//        reviewRepository.save(Review.builder().id(2L).reservationRecord(record).grade(grade).createdDate(LocalDate.now())
//                .detail("최고여요").build());
//        // 카페 편의시설 등록
//        convenienceRepository.save(Convenience.builder().id(1L).studycafe(nerdyStudycafe).name(ConvenienceName.주차).build());
//        // 룸 편의시설 등록
//        convenienceRepository.save(Convenience.builder().id(2L).studycafe(nerdyStudycafe).room(room1).name(ConvenienceName.HDMI).build());
//    }
//
//    private List<Hashtag> getHashtags() {
//        List<Hashtag> hashtags = new ArrayList<>();
//        hashtags.add(Hashtag.면접용);
//        hashtags.add(Hashtag.갓성비);
//        return hashtags;
//    }
//
//    private List<String> getRole() {
//        List<String> role = new ArrayList<>();
//        role.add("USER");
//        return role;
//    }
//
//    private List<Integer> getRefundPolicy() {
//        List<Integer> refundPolicy = new ArrayList<>();
//        refundPolicy.add(50);
//        refundPolicy.add(30);
//        return refundPolicy;
//    }
//
//    private List<String> getNotices() {
//        List<String> notice = new ArrayList<>();
//        notice.add("이벤트 합니다.");
//        notice.add("친구추천");
//        return notice;
//    }
//
//}
