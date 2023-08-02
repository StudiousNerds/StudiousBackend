package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.SortType;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Validated
@Slf4j
public class StudycafeController {
    private final StudycafeService studycafeService;

    @GetMapping("/search")
    public List<SearchResponse> search(
            @RequestParam Integer page,
            @RequestParam(required = false) SortType sortType,       // 정렬 기준
            @RequestParam @Length(min = 2) String keyword,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @FutureOrPresent LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam(required = false) @Positive Integer headCount,
            @RequestParam(required = false) @Range(min = 0, max = 5) Integer minGrade,          // 최소 평점
            @RequestParam(required = false) Boolean eventInProgress,   // 이벤트 여부
            @RequestParam(required = false) List<HashtagName> hashtags,     // 해시태그
            @RequestParam(required = false) List<ConvenienceName> conveniences // 편의 시설
    ) {
        SearchRequest searchRequest = SearchRequest.builder()
                .sortType(sortType)
                .keyword(keyword)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .headCount(headCount)
                .minGrade(minGrade)
                .eventInProgress(eventInProgress)
                .hashtags(hashtags)
                .conveniences(conveniences)
                .build();

        log.info("searchRequest = {}", searchRequest);
        return studycafeService.inquire(searchRequest, PageRequestConverter.of(page));
    }

    @GetMapping("/studycafes/{cafeId}")
    public FindStudycafeResponse findStudycafeInfo(@PathVariable("cafeId") Long cafeId, @RequestBody FindStudycafeRequest findStudycafeRequest) {
        return studycafeService.findByDate(cafeId, findStudycafeRequest);
    }

    @GetMapping("/main")
    public MainPageResponse mainpage() {
        return studycafeService.getMainPage();
    }
}
