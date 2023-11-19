package nerds.studiousTestProject.studycafe.controller;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowStudycafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchSortType;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studycafes")
@Slf4j
@Validated
public class StudycafeController {
    private final StudycafeService studycafeService;

    private static final String STUDYCAFE_SEARCH_SIZE = "8";
    private static final String TIME_DEFAULT_VALUE = "#{T(java.time.LocalDateTime).now()}";

    @GetMapping
    public List<SearchResponse> findStudycafes(@RequestParam(required = false) final Integer page,
                                               @RequestParam(defaultValue = STUDYCAFE_SEARCH_SIZE) final Integer size,
                                               @RequestParam(required = false) final String keyword,
                                               @RequestParam(required = false) @FutureOrPresent(message = "예약일은 오늘 이후 날짜로 설정해야 합니다.") final LocalDate date,
                                               @RequestParam(required = false) final LocalTime startTime,
                                               @RequestParam(required = false) final LocalTime endTime,
                                               @RequestParam(required = false) @Positive(message = "인원 수는 최소 1명 이상이여야 합니다.") final Integer headCount,
                                               @RequestParam(required = false) @Range(min = 0, max = 5, message = "최소 평점은 0이상 5이하여야 합니다.") final Integer minGrade,
                                               @RequestParam(required = false) final List<HashtagName> hashtags,
                                               @RequestParam(required = false) final List<ConvenienceName> conveniences,
                                               @RequestParam(defaultValue = SearchSortType.Names.GRADE_DESC) final SearchSortType sortType,
                                               @LoggedInMember @Nullable Long memberId) {
        return studycafeService.enquire(keyword, date, startTime, endTime, headCount,
                minGrade, hashtags, conveniences, sortType, PageRequestConverter.of(page, size), memberId);
    }

    @GetMapping("/{studycafeId}")
    public ShowStudycafeDetailsResponse findStudycafe(
            @LoggedInMember @Nullable Long memberId,
            @PathVariable("studycafeId") final Long studycafeId,
            @RequestParam(defaultValue = TIME_DEFAULT_VALUE) @FutureOrPresent(message = "예약일은 오늘 이후 날짜로 설정해야 합니다.") final LocalDate date) {
        return studycafeService.findStudycafeByDate(memberId, studycafeId, date);
    }

    @GetMapping("/{studycafeId}/refundPolicies")
    public List<RefundPolicyResponse> findStudycafeRefundPolicy(@PathVariable("studycafeId") final Long studycafeId) {
        return studycafeService.findRefundPolicies(studycafeId);
    }

    @GetMapping("/{studycafeId}/notices")
    public List<String> findStudycafeNotice(@PathVariable("studycafeId") final Long studycafeId) {
        return studycafeService.findNotices(studycafeId);
    }
}