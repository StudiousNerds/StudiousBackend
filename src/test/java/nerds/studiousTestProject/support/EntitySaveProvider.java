package nerds.studiousTestProject.support;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.hashtag.entity.HashtagRecord;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.Role;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.review.entity.Grade;
import nerds.studiousTestProject.review.entity.Review;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntitySaveProvider {

    @Autowired
    private EntityManager em;

    private static EntityManager staticEm;

    @PostConstruct
    public void initStatic() {
        staticEm = this.em;
    }

    public static Role 권한_저장(Role role) {
        staticEm.persist(role);
        flushAndClear();
        return role;
    }

    public static Studycafe 스터디카페_저장(Studycafe studycafe) {
        staticEm.persist(studycafe);
        flushAndClear();
        return studycafe;
    }

    public static Member 회원_저장(Member member) {
        staticEm.persist(member);
        flushAndClear();
        return member;
    }

    public static Room 룸_저장(Room room) {
        staticEm.persist(room);
        flushAndClear();
        return room;
    }

    public static OperationInfo 운영_정보_저장(OperationInfo operationInfo) {
        staticEm.persist(operationInfo);
        flushAndClear();
        return operationInfo;
    }

    public static ReservationRecord 예약_정보_저장(ReservationRecord reservationRecord) {
        staticEm.persist(reservationRecord);
        flushAndClear();
        return reservationRecord;
    }

    public static Review 리뷰_저장(Review review) {
        staticEm.persist(review);
        flushAndClear();
        return review;
    }

    public static Grade 평점_저장(Grade grade) {
        staticEm.persist(grade);
        flushAndClear();
        return grade;
    }

    public static HashtagRecord 해시테그_저장(HashtagRecord hashtagRecord) {
        staticEm.persist(hashtagRecord);
        flushAndClear();
        return hashtagRecord;
    }

    public static Convenience 편의시설_저장(Convenience convenience) {
        staticEm.persist(convenience);
        flushAndClear();
        return convenience;
    }

    private static void flushAndClear() {
        staticEm.flush();
        staticEm.clear();
    }
}
