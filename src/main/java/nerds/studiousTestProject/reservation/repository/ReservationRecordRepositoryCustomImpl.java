package nerds.studiousTestProject.reservation.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static nerds.studiousTestProject.member.entity.member.QMember.member;
import static nerds.studiousTestProject.payment.entity.QPayment.payment;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.AFTER_USING;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.ALL;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.BEFORE_USING;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.CANCELED;
import static nerds.studiousTestProject.reservation.entity.ReservationSettingsStatus.USING;
import static nerds.studiousTestProject.reservation.entity.QReservationRecord.reservationRecord;
import static nerds.studiousTestProject.room.entity.QRoom.room;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;


@RequiredArgsConstructor
public class ReservationRecordRepositoryCustomImpl implements ReservationRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ReservationRecord> getReservationRecordsConditions(ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, Long memberId, Pageable pageable) {

        JPAQuery<ReservationRecord> contentQuery = jpaQueryFactory.selectFrom(reservationRecord)
                .join(reservationRecord.member, member).fetchJoin()
                .join(reservationRecord.room, room).fetchJoin()
                .innerJoin(room.studycafe, studycafe).fetchJoin()
                .join(reservationRecord.payment, payment).fetchJoin();

        JPAQuery<Long> countQuery = jpaQueryFactory.select(reservationRecord.count())
                .from(reservationRecord)
                .join(reservationRecord.member, member)
                .join(reservationRecord.room.studycafe, studycafe);

        List<ReservationRecord> content = getReservationSettings(contentQuery, tab, studycafeName, startDate, endDate, memberId)
                .orderBy(reservationRecord.date.desc(), reservationRecord.startTime.desc(), reservationRecord.endTime.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = getReservationSettings(countQuery, tab, studycafeName, startDate, endDate, memberId)
                .fetchOne();

        return count == null ? Page.empty() : new PageImpl<>(content, pageable, count);
    }

    private <T> JPAQuery<T> getReservationSettings(JPAQuery<T> query, ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, Long memberId) {
        return query
                .where(
                        member.id.eq(memberId),
                        searchByStudycafeName(studycafeName),
                        handleTab(tab),
                        afterReservationStartDate(startDate),
                        beforeReservationEndDate(endDate)
                );
    }

    private BooleanExpression searchByStudycafeName(String studycafeName) {
        return studycafeName == null ? null : studycafe.name.contains(studycafeName);
    }

    private BooleanExpression afterReservationStartDate(LocalDate startDate) {
        return startDate == null ? null : reservationRecord.date.goe(startDate);
    }

    private BooleanExpression beforeReservationEndDate(LocalDate endDate) {
        return endDate == null ? null : reservationRecord.date.loe(endDate);
    }

    private BooleanExpression handleTab(ReservationSettingsStatus tab) {
        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if (tab == ALL) return reservationRecord.status.eq(ReservationStatus.CONFIRMED).or(reservationRecord.status.eq(ReservationStatus.CANCELED));
        if (tab == CANCELED)
            return reservationRecord.status.eq(ReservationStatus.CANCELED);
        if (tab == BEFORE_USING)
            return conditionOfBeforeUsing(nowDate, nowTime);
        if (tab == AFTER_USING)
            return conditionOfAfterUsing(nowDate, nowTime);
        if (tab == USING)
            return conditionOfUsing(nowDate, nowTime);
        return null;
    }

    private BooleanExpression conditionOfUsing(LocalDate nowDate, LocalTime nowTime) {
        return reservationRecord.status.eq(ReservationStatus.CONFIRMED)
                .and(reservationRecord.date.eq(nowDate))
                .and(reservationRecord.startTime.loe(nowTime))
                .and(reservationRecord.endTime.goe(nowTime));
    }

    private BooleanExpression conditionOfAfterUsing(LocalDate nowDate, LocalTime nowTime) {
        return reservationRecord.status.eq(ReservationStatus.CONFIRMED)
                .and(
                        reservationRecord.date.before(nowDate)
                                .or(
                                        reservationRecord.date.eq(nowDate)
                                                .and(reservationRecord.endTime.before(nowTime))
                                )
                );
    }

    private BooleanExpression conditionOfBeforeUsing(LocalDate nowDate, LocalTime nowTime) {
        return reservationRecord.status.eq(ReservationStatus.CONFIRMED)
                .and(
                        reservationRecord.date.after(nowDate)
                                .or(
                                        reservationRecord.date.eq(nowDate)
                                                .and(reservationRecord.startTime.after(nowTime))
                                )
                );
    }

}
