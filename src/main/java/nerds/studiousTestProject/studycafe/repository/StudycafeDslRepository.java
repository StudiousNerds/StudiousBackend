package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.convenience.entity.QConvenienceList;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.reservation.entity.ReservationStatus;
import nerds.studiousTestProject.studycafe.dto.search.QSearchResponse;
import nerds.studiousTestProject.studycafe.dto.search.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.search.SortType;
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
                        inOperation(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        dateAndTimeNotReserved(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        headCountBetween(searchRequest.getHeadCount()),
                        keywordContains(searchRequest.getKeyword()),
                        totalGradeGoe(searchRequest.getMinGrade()),
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
                        inOperation(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        headCountBetween(searchRequest.getHeadCount()),
                        dateAndTimeNotReserved(searchRequest.getDate(), searchRequest.getStartTime(), searchRequest.getEndTime()),
                        keywordContains(searchRequest.getKeyword()),
                        totalGradeGoe(searchRequest.getMinGrade()),
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

    private BooleanExpression inOperation(LocalDate date, LocalTime startTime, LocalTime endTime) {
        BooleanExpression startTimeLoe = cafeStartTimeLoe(date, startTime);
        BooleanExpression endTimeGoe = cafeEndTimeGoe(date, endTime);
        return startTimeLoe != null ? startTimeLoe.and(endTimeGoe) : endTimeGoe;
    }

    private BooleanExpression cafeStartTimeLoe(LocalDate date, LocalTime startTime) {
        if (startTime == null) {
            return null;
        }

        int value = date.getDayOfWeek().getValue();
        return studycafe.operationInfos.get(value - 1).startTime.loe(startTime);
    }

    private BooleanExpression cafeEndTimeGoe(LocalDate date, LocalTime endTime) {
        if (endTime == null) {
            return null;
        }

        int value = date.getDayOfWeek().getValue();
        return studycafe.operationInfos.get(value - 1).endTime.goe(endTime);
    }

    private BooleanExpression dateAndTimeNotReserved(LocalDate date, LocalTime startTime, LocalTime endTime) {
        BooleanExpression dateEq = dateEq(date);
        BooleanExpression startTimeLoe = startTimeLoe(startTime);
        BooleanExpression endTimeGoe = endTimeGoe(endTime);

        // 날짜가 선택 안 된 경우는 가능 시간 조회 불가능
        // 예약이 없는 경우를 대비하여 reservationRecord.isNull() 조건 추가
        return dateEq != null ? reservationRecord.isNull()
                .or(
                        (reservationRecord.status.eq(ReservationStatus.CONFIRMED).and(
                                reservationRecord.date.eq(date)
                                .and(startTimeLoe)
                                .and(endTimeGoe)
                        )).not()
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
        return hasText(keyword) ? studycafe.name.contains(keyword).or(studycafe.address.contains(keyword)) : null;
    }

    private BooleanExpression totalGradeGoe(Integer minGrade) {
        return minGrade != null ? studycafe.totalGrade.goe(minGrade) : null;
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
            case GRADE_DESC -> orderSpecifiers.add(new OrderSpecifier(Order.DESC, studycafe.totalGrade));
            case CREATED_DESC -> orderSpecifiers.add(new OrderSpecifier(Order.ASC, studycafe.createdAt));
        }

        orderSpecifiers.add(new OrderSpecifier(Order.ASC, studycafe.createdAt));
        return orderSpecifiers.toArray(OrderSpecifier[]::new);
    }
}
