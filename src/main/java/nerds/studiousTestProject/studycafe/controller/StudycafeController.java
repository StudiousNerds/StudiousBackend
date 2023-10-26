package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyInfo;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
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
@RequestMapping("/api/v1/")
@Slf4j
@Validated
public class StudycafeController {
    private final StudycafeService studycafeService;

    private static final int STUDYCAFE_SEARCH_SIZE = 8;
    private static final String TIME_DEFAULT_VALUE = "#{T(java.time.LocalDateTime).now()}";

    @GetMapping("/search")
    public List<SearchResponse> search(@RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) @FutureOrPresent(message = "예약일은 오늘 이후 날짜로 설정해야 합니다.") LocalDate date,
                                       @RequestParam(required = false) LocalTime startTime,
                                       @RequestParam(required = false) LocalTime endTime,
                                       @RequestParam(required = false) @Positive(message = "인원 수는 최소 1명 이상이여야 합니다.") Integer headCount,
                                       @RequestParam(required = false) @Range(min = 0, max = 5, message = "최소 평점은 0이상 5이하여야 합니다.") Integer minGrade,
                                       @RequestParam(required = false) List<HashtagName> hashtags,
                                       @RequestParam(required = false) List<ConvenienceName> conveniences,
                                       @RequestParam(defaultValue = SearchSortType.Names.GRADE_DESC) SearchSortType sortType) {
        return studycafeService.enquiry(keyword, date, startTime, endTime, headCount, minGrade, hashtags, conveniences, sortType, PageRequestConverter.of(page, STUDYCAFE_SEARCH_SIZE));
    }

    @GetMapping("/studycafes/{studycafeId}")
    public FindStudycafeResponse findStudycafeInfo(
            @PathVariable("studycafeId") Long studycafeId,
            @RequestParam(defaultValue = TIME_DEFAULT_VALUE) @FutureOrPresent(message = "예약일은 오늘 이후 날짜로 설정해야 합니다.") LocalDate date) {
        return studycafeService.findByDate(studycafeId, date);
    }

    @GetMapping("/studycafes/{studycafeId}/refundPolicy")
    public List<RefundPolicyInfo> findStudycafeRefundPolicy(@PathVariable("studycafeId") Long studycafeId) {
        return studycafeService.findRefundPolicy(studycafeId);
    }

    @GetMapping("/studycafes/{studycafeId}/notice")
    public List<String> findStudycafeNotice(@PathVariable("studycafeId") Long studycafeId) {
        return studycafeService.findNotice(studycafeId);
    }

    @GetMapping("/main")
    public MainPageResponse mainpage() {
        return studycafeService.getMainPage();
    }
}