package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.convenience.entity.QConvenience;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.request.SortType;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.hashtag.entity.QHashtagRecord.hashtagRecord;
import static nerds.studiousTestProject.reservation.entity.QReservationRecord.reservationRecord;
import static nerds.studiousTestProject.review.entity.QGrade.grade;
import static nerds.studiousTestProject.review.entity.QReview.review;
import static nerds.studiousTestProject.room.entity.QRoom.room;
import static nerds.studiousTestProject.studycafe.entity.QOperationInfo.operationInfo;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;
import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class StudycafeRepositoryCustomImpl implements StudycafeRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private static final String CAFE_CONVENIENCE_NAME = "cConvenienceList";
    private static final String ROOM_CONVENIENCE_NAME = "rConvenienceList";
    private static final String GROUP_CONCAT_TEMPLATE = "group_concat(distinct {0})";

    @Override
    public Page<SearchResponseInfo> getSearchResult(SearchRequest searchRequest, Pageable pageable) {
        JPAQuery<Long> countQuery = queryFactory
                .select(studycafe.count())
                .from(studycafe);

        // 페이징 처리를 위해선 개수를 직접 쿼리를 날려 확인해야 한다! (QueryDSL의 count는 믿을게 못됨)
        JPAQuery<Long> count = getJoinedQuery(countQuery, searchRequest)
                .where(
                        dateAndTimeCanReserve(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        headCountBetween(searchRequest.getHeadCount()),
                        keywordContains(searchRequest.getKeyword())
                )
                .having(
                        hashtagContains(searchRequest.getHashtags()),
                        totalGradeGoe(searchRequest.getMinGrade()),
                        convenienceContains(searchRequest.getConveniences()))
                .groupBy(studycafe.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 조회 결과가 없으면(조회 결과 개수가 0인 경우) 굳이 쿼리를 날리지 않고 빈 Page 리턴
        if (count.fetchFirst() == null) {
            return Page.empty();
        }

        JPAQuery<SearchResponseInfo> contentQuery = queryFactory
                .select(
                        Projections.constructor(SearchResponseInfo.class,
                                studycafe.id,
                                studycafe.name,
                                studycafe.photo,
                                studycafe.accumReserveCount,
                                reservationRecord.count().intValue(),
                                studycafe.walkingTime,
                                studycafe.nearestStation,
                                Expressions.stringTemplate(GROUP_CONCAT_TEMPLATE, accumHashtagHistory.name),
                                Expressions.stringTemplate(GROUP_CONCAT_TEMPLATE, hashtagRecord.name),
                                studycafe.totalGrade,
                                grade.total.avg()
                        )
                )
                .from(studycafe);

        List<SearchResponseInfo> content = getJoinedQuery(contentQuery, searchRequest)
                .where(
                        dateAndTimeCanReserve(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        headCountBetween(searchRequest.getHeadCount()),
                        keywordContains(searchRequest.getKeyword())
                )
                .having(
                        hashtagContains(searchRequest.getHashtags()),
                        totalGradeGoe(searchRequest.getMinGrade()),
                        convenienceContains(searchRequest.getConveniences()))
                .groupBy(studycafe.id)
                .orderBy(createOrderSpecifier(searchRequest.getSortType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    /**
     * 동적 Join 문을 만들어주는 메소드, 편의시설 조건이 있는 경우 추가 Join 발생
     * @param query Join 문이 작성되기 전 JpaQuery 객체
     * @param searchRequest 검색 요청
     * @return Join 문이 작성된 JpaQuery 객체
     * @param <T> Count Query 인 경우 Long, 나머지는 응답 객체 또는 Entity
     */
    private <T> JPAQuery<T> getJoinedQuery(JPAQuery<T> query, SearchRequest searchRequest) {
        if (searchRequest.getDate() != null && searchRequest.getWeek() != null) {
            query = query
                    .leftJoin(studycafe.operationInfos, operationInfo).on(operationInfo.week.eq(searchRequest.getWeek()));
        }

        if (searchRequest.getConveniences() != null && !searchRequest.getConveniences().isEmpty()) {
            QConvenience cConveniences = new QConvenience(CAFE_CONVENIENCE_NAME);
            QConvenience rConveniences = new QConvenience(ROOM_CONVENIENCE_NAME);

            query = query
                    .leftJoin(studycafe.conveniences, cConveniences)
                    .leftJoin(room.conveniences, rConveniences);
        }

        return query.leftJoin(studycafe.accumHashtagHistories, accumHashtagHistory)
                .leftJoin(studycafe.rooms, room)
                .leftJoin(room.reservationRecords, reservationRecord)
                .leftJoin(reservationRecord.review, review)
                .leftJoin(review.grade, grade)
                .leftJoin(review.hashtagRecords, hashtagRecord);
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

    private BooleanExpression dateAndTimeCanReserve(LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (date == null) {
            return null;
        }

        BooleanExpression inOperation = inOperation(startTime, endTime);
        return inOperation != null ? inOperation.and(dateAndTimeNotReserved(date, startTime, endTime)) : dateAndTimeNotReserved(date, startTime, endTime);
    }

    private BooleanExpression inOperation(LocalTime startTime, LocalTime endTime) {
        return isAllDay().or(NotClosedAndBetweenOperationTime(startTime, endTime));
    }

    private BooleanExpression isAllDay() {
        return operationInfo.isAllDay;
    }

    private BooleanExpression NotClosedAndBetweenOperationTime(LocalTime startTime, LocalTime endTime) {
        BooleanExpression startTimeLoe = cafeStartTimeLoe(startTime);
        BooleanExpression endTimeGoe = cafeEndTimeGoe(endTime);

        return operationInfo.closed.isFalse().and(startTimeLoe != null ? startTimeLoe.and(endTimeGoe) : endTimeGoe);
    }

    private BooleanExpression cafeStartTimeLoe(LocalTime startTime) {
        if (startTime == null) {
            return null;
        }

        return operationInfo.startTime.loe(startTime);
    }

    private BooleanExpression cafeEndTimeGoe(LocalTime endTime) {
        if (endTime == null) {
            return null;
        }

        return operationInfo.endTime.goe(endTime);
    }

    private BooleanExpression dateAndTimeNotReserved(LocalDate date, LocalTime startTime, LocalTime endTime) {
        BooleanExpression dateEq = dateEq(date);
        BooleanExpression startTimeLoe = startTimeLoe(startTime);
        BooleanExpression endTimeGoe = endTimeGoe(endTime);

        // 날짜가 선택 안 된 경우는 가능 시간 조회 불가능
        // 예약이 없는 경우를 대비하여 reservationRecord.isNull() 조건 추가
        return dateEq != null ? reservationRecord.isNull()
                .or(
                        (reservationRecord.status.eq(ReservationStatus.CONFIRMED)
                                .and(dateEq)
                                .and(startTimeLoe)
                                .and(endTimeGoe)
                        ).not()
                ) : null;
    }

    private BooleanExpression dateEq(LocalDate date) {
        return date != null ? reservationRecord.date.eq(date) : null;
    }

    private BooleanExpression startTimeLoe(LocalTime startTime) {
        if (startTime == null) {
            startTime = LocalTime.MIN;   // 시간 설정이 안되있는 경우 00:00:00 으로 설정
        }

        return reservationRecord.startTime.loe(startTime);
    }

    private BooleanExpression endTimeGoe(LocalTime endTime) {
        if (endTime == null) {
            endTime = LocalTime.MAX;     // 시간 설정이 안되있는 경우 23:59:59 으로 설정
        }

        return reservationRecord.endTime.goe(endTime);
    }

    private BooleanExpression keywordContains(String keyword) {
        return hasText(keyword) ? studycafe.name.contains(keyword).or(studycafe.address.addressBasic.contains(keyword)).or(studycafe.address.addressDetail.contains(keyword)) : null;
    }

    private BooleanExpression totalGradeGoe(Integer minGrade) {
        return minGrade != null ? grade.total.goe(minGrade) : null;
    }

    private BooleanExpression hashtagContains(List<HashtagName> hashtags) {
        return hashtags != null && !hashtags.isEmpty() ? hashtagRecord.name.in(hashtags) : null;
    }

    private BooleanExpression convenienceContains(List<ConvenienceName> conveniences) {
        if (conveniences == null || conveniences.isEmpty()) {
            return null;
        }

        // Room과 Studycafe의 Convenience 두 개를 Join 해야 하므로 별도의 Q클래스 객체를 만들어 조인을 해야 한다.
        QConvenience cConveniences = new QConvenience("cConvenienceList");
        QConvenience rConveniences = new QConvenience("rConvenienceList");

        return cConveniences.name.in(conveniences).and(rConveniences.name.in(conveniences));
    }

    private OrderSpecifier[] createOrderSpecifier(SortType sortType) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        switch (sortType != null ? sortType : SortType.GRADE_DESC) {
            case RESERVATION_DESC -> orderSpecifiers.add(reservationRecord.count().desc());
            case GRADE_DESC -> orderSpecifiers.add(grade.total.avg().desc());
            case CREATED_DESC -> orderSpecifiers.add(studycafe.createdDate.desc());
            case REVIEW_DESC -> orderSpecifiers.add(review.count().desc());
            case REVIEW_ASC -> orderSpecifiers.add(review.count().asc());
        }

        orderSpecifiers.add(studycafe.name.asc());  // 두 번째 정렬조건 추가
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
