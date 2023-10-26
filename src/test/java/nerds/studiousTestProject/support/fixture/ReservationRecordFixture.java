package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationRecord.ReservationRecordBuilder;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import java.time.LocalDate;
import java.time.LocalTime;

public enum ReservationRecordFixture {
    CONFIRM_RESERVATION("길가은","01012341234", ReservationStatus.CONFIRMED,3),
    IN_PROGRESS_RESERVATION("김민우","01043214321", ReservationStatus.INPROGRESS,4),
    CANCELED_RESERVATION("최보현","01087968797", ReservationStatus.CANCELED,3);

    private final String reserveUserName;
    private final String phoneNumber;
    private final ReservationStatus reservationStatus;
    private final int headCount;

    ReservationRecordFixture(String reserveUserName, String phoneNumber, ReservationStatus reservationStatus, int headCount) {
        this.reserveUserName = reserveUserName;
        this.phoneNumber = phoneNumber;
        this.reservationStatus = reservationStatus;
        this.headCount = headCount;
    }

    public ReservationRecord 생성(){
        return 생성(null);
    }

    public ReservationRecord 생성(Long id){
        return 기본_정보_빌더_생성(id).build();
    }

    public ReservationRecordBuilder 기본_정보_빌더_생성(Long id){
        return ReservationRecord.builder()
                .userName(this.reserveUserName)
                .userPhoneNumber(this.phoneNumber)
                .status(this.reservationStatus)
                .headCount(this.headCount);
    }

    public ReservationRecord 예약_내역_생성(LocalDate reserveDate, LocalTime startTime, LocalTime endTime, Member member, Room room){
        return 예약_내역_생성(null, reserveDate, startTime, endTime, member, room);
    }

    public ReservationRecord 예약_내역_생성(Long id, LocalDate reserveDate, LocalTime startTime, LocalTime endTime, Member member, Room room){
        return 기본_정보_빌더_생성(id)
                .date(reserveDate)
                .startTime(startTime)
                .endTime(endTime)
                .usingTime(endTime.getHour() - startTime.getHour())
                .member(member)
                .room(room)
                .build();
    }

    public ReservationRecord 리뷰_추가_생성(LocalDate reserveDate, LocalTime startTime, LocalTime endTime, Member member, Room room, Review review){
        return 리뷰_추가_생성(null, reserveDate, startTime, endTime, member, room, review);
    }

    public ReservationRecord 리뷰_추가_생성(Long id, LocalDate reserveDate, LocalTime startTime, LocalTime endTime, Member member, Room room, Review review){
        return 기본_정보_빌더_생성(id)
                .date(reserveDate)
                .startTime(startTime)
                .endTime(endTime)
                .usingTime(endTime.getHour() - startTime.getHour())
                .member(member)
                .room(room)
                .review(review)
                .build();
    }

    public ReservationRecord 룸_생성(Room room, Long id) {
        return 기본_정보_빌더_생성(id)
                .room(room)
                .build();
    }

    public ReservationRecord 멤버_생성(Member member, Long id) {
        return 기본_정보_빌더_생성(id)
                .member(member)
                .build();
    }
}
