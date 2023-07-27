package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.studycafe.dto.QSearchResponse;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import static nerds.studiousTestProject.reservation.entity.QReservationRecord.reservationRecord;
import static nerds.studiousTestProject.room.entity.QRoom.room;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class StudycafeDslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<SearchResponse> searchAll(SearchRequest searchRequest, Pageable pageable) {
        JPAQuery<Long> countQuery = queryFactory
                .select(studycafe.count())
                .from(studycafe);

        JPAQuery<Long> count = getJoinedCountQuery(countQuery, searchRequest)
                .where(
                        openTime(searchRequest.getStartTime(), searchRequest.getEndTime()),
                        dateAndTimeNotReserved(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        headCountBetween(searchRequest.getHeadCount()),
                        keywordContains(searchRequest.getKeyword()),
                        gradeBetween(searchRequest.getMinGrade(), searchRequest.getMaxGrade())
                )
                .groupBy(studycafe.id);

        // 조회 결과가 없으면 빈 Page 리턴
        if (count.fetchOne() == null) {
            return Page.empty();
        }

        JPAQuery<SearchResponse> contentQuery = queryFactory
                .select(
                        new QSearchResponse(
                                studycafe.name,
                                studycafe.photo,    // 사진 (추후 수정 예정)
                                studycafe.accumReserveCount,
                                studycafe.duration,
                                studycafe.totalGrade
                        )
                )
                .from(studycafe);

        List<SearchResponse> content = getJoinedContentQuery(contentQuery, searchRequest)
                .where(
                        headCountBetween(searchRequest.getHeadCount()),
                        dateAndTimeNotReserved(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        openTime(searchRequest.getStartTime(), searchRequest.getEndTime()),
                        keywordContains(searchRequest.getKeyword()),
                        gradeBetween(searchRequest.getMinGrade(), searchRequest.getMaxGrade())
                )
                .groupBy(studycafe.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    public JPAQuery<SearchResponse> getJoinedContentQuery(JPAQuery<SearchResponse> query, SearchRequest searchRequest) {
        if (searchRequest.getHeadCount() != null || searchRequest.getDate() != null) {
            query = query.leftJoin(studycafe.rooms, room);

            if (searchRequest.getDate() != null) {
                query = query
                        .leftJoin(room.reservationRecords, reservationRecord);
            }
        }

        return query;
    }

    public JPAQuery<Long> getJoinedCountQuery(JPAQuery<Long> query, SearchRequest searchRequest) {
        if (searchRequest.getHeadCount() != null || searchRequest.getDate() != null) {
            query = query.leftJoin(studycafe.rooms, room);

            if (searchRequest.getDate() != null) {
                query = query
                        .leftJoin(room.reservationRecords, reservationRecord);
            }
        }

        return query;
    }

    private BooleanExpression headCountBetween(Integer headCount) {
        BooleanExpression minHeadCountLoe = minHeadCountLoe(headCount);
        return minHeadCountLoe == null ? null : minHeadCountLoe.and(maxHeadCountGoe(headCount));
    }

    private BooleanExpression minHeadCountLoe(Integer headCount) {
        return headCount != null ? room.minHeadCount.loe(headCount) : null;
    }

    private BooleanExpression maxHeadCountGoe(Integer headCount) {
        return headCount != null ? room.maxHeadCount.goe(headCount) : null;
    }

    private BooleanExpression openTime(Time startTime, Time endTime) {
        BooleanExpression startTimeLoe = cafeStartTimeLoe(startTime);
        BooleanExpression endTimeGoe = cafeEndTimeGoe(endTime);
        return startTimeLoe != null ? startTimeLoe.and(endTimeGoe) : endTimeGoe;
    }

    private BooleanExpression cafeStartTimeLoe(Time startTime) {
        return startTime != null ? studycafe.startTime.loe(startTime) : null;
    }

    private BooleanExpression cafeEndTimeGoe(Time endTime) {
        return endTime != null ? studycafe.endTime.goe(endTime) : null;
    }

    private BooleanExpression dateAndTimeNotReserved(Date date, Time startTime, Time endTime) {
        BooleanExpression dateEq = dateEq(date);
        BooleanExpression startTimeLoe = startTimeLoe(startTime);
        BooleanExpression endTimeGoe = endTimeGoe(endTime);

        // 날짜가 선택 안 된 경우는 가능 시간 조회 불가능
        // 예약이 없는 경우를 대비하여 reservationRecord.isNull() 조건 추가
        return dateEq != null ? reservationRecord.isNull()
                .or(
                        reservationRecord.status.eq(ReservationStatus.CONFIRMED).and(reservationRecord.date.eq(date)
                                .and(startTimeLoe)
                                .and(endTimeGoe)
                                .not()
                        )
                ) : null;
    }

    private BooleanExpression dateEq(Date date) {
        return date != null ? reservationRecord.date.eq(date) : null;
    }

    private BooleanExpression startTimeLoe(Time startTime) {
        if (startTime == null) {
            startTime = Time.valueOf("00:00:00");   // 시간 설정이 안되있는 경우 00:00:00 으로 설정
        }

        return reservationRecord.startTime.loe(startTime);
    }

    private BooleanExpression endTimeGoe(Time endTime) {
        if (endTime == null) {
            endTime = Time.valueOf("23:59:59");     // 시간 설정이 안되있는 경우 23:59:59 으로 설정
        }

        return reservationRecord.endTime.goe(endTime);
    }

    private BooleanExpression keywordContains(String keyword) {
        return hasText(keyword) ? studycafe.name.contains(keyword).or(studycafe.address.contains(keyword)) : null;
    }

    private BooleanExpression minGradeGoe(Integer minGrade) {
        return minGrade != null ? studycafe.totalGrade.goe(minGrade) : null;
    }

    private BooleanExpression maxGradeLoe(Integer maxGrade) {
        return maxGrade != null ? studycafe.totalGrade.loe(maxGrade) : null;
    }

    private BooleanExpression gradeBetween(Integer minGrade, Integer maxGrade) {
        BooleanExpression minGradeGoe = minGradeGoe(minGrade);
        BooleanExpression maxGradeLoe = maxGradeLoe(maxGrade);

        return minGradeGoe != null ? minGradeGoe.and(maxGradeLoe) : maxGradeLoe;
    }
}
