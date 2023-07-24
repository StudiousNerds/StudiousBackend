package nerds.studiousTestProject.studycafe.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.studycafe.dto.QSearchResponse;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.Date;
import java.util.List;

import static io.jsonwebtoken.lang.Strings.hasText;
import static nerds.studiousTestProject.studycafe.entity.QStudycafe.studycafe;

@Repository
@RequiredArgsConstructor
public class StudycafeDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<SearchResponse> searchAll(SearchRequest searchRequest) {
        return queryFactory
                .select(
                        new QSearchResponse(
                                studycafe.name,
                                studycafe.photo,    // 사진 (추후 수정 예정)
                                studycafe.accumReserveCount,
                                studycafe.duration,    // 역까지 걸리는 시간
                                studycafe.totalGrade
                        )
                )
                .from(studycafe)
                .where(
                        headCountBetween(searchRequest.getHeadCount())
                )
                .fetch();
    }

    private BooleanExpression headCountBetween(Integer headCount) {
        BooleanExpression minHeadCountGoe = minHeadCountGoe(headCount);
        return minHeadCountGoe == null ? null : minHeadCountGoe.and(maxHeadCountLoe(headCount));
    }

    private BooleanExpression minHeadCountGoe(Integer headCount) {
        return headCount != null ? studycafe.rooms.any().minHeadCount.goe(headCount) : null;
    }

    private BooleanExpression maxHeadCountLoe(Integer headCount) {
        return headCount != null ? studycafe.rooms.any().maxHeadCount.loe(headCount) : null;
    }
}
