package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.convenience.ConvenienceName;
import nerds.studiousTestProject.convenience.QConvenienceList;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.studycafe.dto.QSearchResponse;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.SortType;
import nerds.studiousTestProject.studycafe.entity.hashtag.HashtagName;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static nerds.studiousTestProject.reservation.entity.QReservationRecord.reservationRecord;
import static nerds.studiousTestProject.room.entity.QRoom.room;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;
import static nerds.studiousTestProject.studycafe.entity.hashtag.QHashtagRecord.hashtagRecord;
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
                        gradeBetween(searchRequest.getMinGrade(), searchRequest.getMaxGrade()),
                        hashtagContains(searchRequest.getHashtags()),
                        convenienceContains(searchRequest.getConveniences())
                )
                .groupBy(studycafe.id);

        // 조회 결과가 없으면 빈 Page 리턴
        if (count.fetchOne() == null) {
            return Page.empty();
        }

        JPAQuery<SearchResponse> contentQuery = queryFactory
                .select(
                        new QSearchResponse(
                                studycafe.id,
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
                        gradeBetween(searchRequest.getMinGrade(), searchRequest.getMaxGrade()),
                        hashtagContains(searchRequest.getHashtags()),
                        convenienceContains(searchRequest.getConveniences())
                )
                .groupBy(studycafe.id)
                .orderBy(createOrderSpecifier(searchRequest.getSortType()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    public JPAQuery<SearchResponse> getJoinedContentQuery(JPAQuery<SearchResponse> query, SearchRequest searchRequest) {
        if (searchRequest.getHeadCount() != null || searchRequest.getDate() != null || searchRequest.getConveniences() != null) {
            query = query.leftJoin(studycafe.rooms, room);

            if (searchRequest.getDate() != null) {
                query = query
                        .leftJoin(room.reservationRecords, reservationRecord);
            }

            if (searchRequest.getConveniences() != null && !searchRequest.getConveniences().isEmpty()) {
                QConvenienceList rConvenienceList = new QConvenienceList("rConvenienceList");
                QConvenienceList cConvenienceList = new QConvenienceList("cConvenienceList");

                query = query
                        .leftJoin(studycafe.convenienceLists, cConvenienceList)
                        .leftJoin(room.convenienceLists, rConvenienceList);
            }
        }

        if (searchRequest.getHashtags() != null) {
            query = query
                    .leftJoin(studycafe.hashtagRecords, hashtagRecord);
        }

        return query;
    }

    public JPAQuery<Long> getJoinedCountQuery(JPAQuery<Long> query, SearchRequest searchRequest) {
        if (searchRequest.getHeadCount() != null || searchRequest.getDate() != null || searchRequest.getConveniences() != null) {
            query = query.leftJoin(studycafe.rooms, room);

            if (searchRequest.getDate() != null) {
                query = query
                        .leftJoin(room.reservationRecords, reservationRecord);
            }

            if (searchRequest.getConveniences() != null && !searchRequest.getConveniences().isEmpty()) {
                QConvenienceList rConvenienceList = new QConvenienceList("rConvenienceList");
                QConvenienceList cConvenienceList = new QConvenienceList("cConvenienceList");

                query = query
                        .leftJoin(studycafe.convenienceLists, cConvenienceList)
                        .leftJoin(room.convenienceLists, rConvenienceList);
            }
        }

        if (searchRequest.getHashtags() != null) {
            query = query
                    .leftJoin(studycafe.hashtagRecords, hashtagRecord);
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
        BooleanExpression startTimeGoe = startTimeGoe(startTime);
        BooleanExpression endTimeLoe = endTimeLoe(endTime);

        // 날짜가 선택 안 된 경우는 가능 시간 조회 불가능
        // 예약이 없는 경우를 대비하여 reservationRecord.isNull() 조건 추가
        return dateEq != null ? reservationRecord.isNull()
                .or(
                        (reservationRecord.status.eq(ReservationStatus.CONFIRMED).and(reservationRecord.date.eq(date)
                                .and(startTimeGoe)
                                .and(endTimeLoe)
                        )).not()
                ) : null;
    }

    private BooleanExpression dateEq(Date date) {
        return date != null ? reservationRecord.date.eq(date) : null;
    }

    private BooleanExpression startTimeGoe(Time startTime) {
        if (startTime == null) {
            startTime = Time.valueOf("00:00:00");   // 시간 설정이 안되있는 경우 00:00:00 으로 설정
        }

        return reservationRecord.startTime.goe(startTime);
    }

    private BooleanExpression endTimeLoe(Time endTime) {
        if (endTime == null) {
            endTime = Time.valueOf("23:59:59");     // 시간 설정이 안되있는 경우 23:59:59 으로 설정
        }

        return reservationRecord.endTime.loe(endTime);
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

    private BooleanExpression hashtagContains(List<HashtagName> hashtags) {
        return hashtags != null && !hashtags.isEmpty() ? hashtagRecord.name.in(hashtags) : null;
    }

    private BooleanExpression convenienceContains(List<ConvenienceName> conveniences) {
        if (conveniences == null || conveniences.isEmpty()) {
            return null;
        }

        // Room과 Studycafe의 Convenience 두 개를 Join 해야 하므로 별도의 Q클래스 객체를 만들어 조인을 해야 한다.
        QConvenienceList cConvenienceList = new QConvenienceList("cConvenienceList");
        QConvenienceList rConvenienceList = new QConvenienceList("rConvenienceList");

        return cConvenienceList.name.in(conveniences).and(rConvenienceList.name.in(conveniences));
    }

    private OrderSpecifier[] createOrderSpecifier(SortType sortType) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        switch (sortType != null ? sortType : SortType.GRADE_DESC) {
            case RESERVATION_DESC -> orderSpecifiers.add(new OrderSpecifier(Order.DESC, studycafe.accumReserveCount));
            case RESERVATION_ASC -> orderSpecifiers.add(new OrderSpecifier(Order.ASC, studycafe.accumReserveCount));
            case GRADE_DESC -> orderSpecifiers.add(new OrderSpecifier(Order.DESC, studycafe.totalGrade));
            case GRADE_ASC -> orderSpecifiers.add(new OrderSpecifier(Order.ASC, studycafe.totalGrade));
        }

        orderSpecifiers.add(new OrderSpecifier(Order.ASC, studycafe.createdAt));
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }

}
