package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyInfo;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Slf4j
@Validated
public class StudycafeController {
    private final StudycafeService studycafeService;

    private static final int STUDYCAFE_SEARCH_SIZE = 8;

    @GetMapping("/search")
    public List<SearchResponse> search(@RequestParam(required = false) Integer page, @ModelAttribute @Valid SearchRequest searchRequest) {
        return studycafeService.inquire(searchRequest, PageRequestConverter.of(page, STUDYCAFE_SEARCH_SIZE));
    }

    @GetMapping("/studycafes/{studycafeId}")
    public FindStudycafeResponse findStudycafeInfo(
            @PathVariable("studycafeId") Long studycafeId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDateTime).now()}") @FutureOrPresent LocalDate date) {
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