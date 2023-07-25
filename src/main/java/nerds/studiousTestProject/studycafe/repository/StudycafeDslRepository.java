package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.studycafe.dto.QSearchResponse;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

import static nerds.studiousTestProject.room.entity.QRoom.room;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;

@Repository
@RequiredArgsConstructor
public class StudycafeDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<SearchResponse> searchAll(SearchRequest searchRequest) {
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

        return getJoinedContentQuery(contentQuery, searchRequest)
                .where(
                        headCountBetween(searchRequest.getHeadCount())
                )
                .groupBy(studycafe.id)
                .fetch();
    }

    public JPAQuery<SearchResponse> getJoinedContentQuery(JPAQuery<SearchResponse> query, SearchRequest searchRequest) {
        if (searchRequest.getHeadCount() != null) {
            query = query.leftJoin(studycafe.rooms, room);
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
}
