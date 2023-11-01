package nerds.studiousTestProject.review.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.review.dto.enquiry.request.AdminReviewType;
import nerds.studiousTestProject.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static nerds.studiousTestProject.reservation.entity.QReservationRecord.reservationRecord;
import static nerds.studiousTestProject.review.entity.QReview.review;
import static nerds.studiousTestProject.room.entity.QRoom.room;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> getPagedReviewsByStudycafeId(Long studycafeId, AdminReviewType reviewType, Pageable pageable) {
        JPAQuery<Long> countQuery = queryFactory.select(review.count()).from(review);
        JPAQuery<Long> count = getResultQuery(studycafeId, reviewType, pageable, countQuery);

        if (count.fetchFirst() == null) {
            return Page.empty();
        }

        JPAQuery<Review> contentQuery = queryFactory.select(review).from(review);
        List<Review> content = getResultQuery(studycafeId, reviewType, pageable, contentQuery)
                .orderBy(createOrderSpecifier(pageable))
                .fetch();

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    private <T> JPAQuery<T> getResultQuery(Long studycafeId, AdminReviewType reviewType, Pageable pageable, JPAQuery<T> query) {
        return query
                .leftJoin(reservationRecord).on(reservationRecord.review.id.eq(review.id))
                .leftJoin(room).on(room.id.eq(reservationRecord.room.id))
                .where(
                        room.studycafe.id.eq(studycafeId),
                        typeFilter(reviewType)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    private BooleanExpression typeFilter(AdminReviewType reviewType) {
        return reviewType != null && reviewType.equals(AdminReviewType.NO_ANSWER) ? review.comment.isNull() : null;
    }

    private OrderSpecifier<?> createOrderSpecifier(Pageable pageable) {
        for (Sort.Order order : pageable.getSort()) {
            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
            return new OrderSpecifier<>(direction, review.createdDate);
        }

        return new OrderSpecifier<>(Order.DESC, review.createdDate);
    }
}
