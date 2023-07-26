package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.ConvenienceName;
import nerds.studiousTestProject.studycafe.dto.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.SortType;
import nerds.studiousTestProject.studycafe.entity.hashtag.HashtagName;
import nerds.studiousTestProject.studycafe.service.StudyCafeService;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Validated
@Slf4j
public class StudycafeController {
    private final StudyCafeService studyCafeService;

    @GetMapping("/search")
    public List<SearchResponse> search(
                                        @RequestParam Integer page,
                                        @RequestParam(required = false) SortType sortType,       // 정렬 기준
                                        @RequestParam(required = false) @Length(min = 2) String keyword,
                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") @FutureOrPresent Date date,
                                        @RequestParam(required = false) Time startTime,
                                        @RequestParam(required = false) Time endTime,
                                        @RequestParam(required = false) @Positive Integer headCount,
                                        @RequestParam(required = false) @Range(min = 0, max = 5) Integer minGrade,          // 최소 평점
                                        @RequestParam(required = false) @Range(min = 0, max = 5) Integer maxGrade,          // 최대 평점
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
                .maxGrade(maxGrade)
                .eventInProgress(eventInProgress)
                .hashtags(hashtags)
                .conveniences(conveniences)
                .build();

        log.info("searchRequest = {}", searchRequest);
        return studyCafeService.inquire(searchRequest, PageRequestConverter.of(page));
    }
}
