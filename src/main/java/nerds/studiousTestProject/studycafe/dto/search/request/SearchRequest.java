package nerds.studiousTestProject.studycafe.dto.search.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.hashtag.entity.HashtagName;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Builder
@Data
public class SearchRequest {
    @Length(min = 2, message = "키워드를 2글자 이상 입력해주세요.")
    @NotNull(message = "키워드를 2글자 이상 입력해주세요.")
    private String keyword;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @FutureOrPresent(message = "예약일은 오늘 이후 날짜로 설정해야 합니다.")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Positive(message = "인원 수는 최소 1명 이상이여야 합니다.")
    private Integer headCount;

    @Range(min = 0, max = 5, message = "최소 평점은 0이상 5이하여야 합니다.")
    private Integer minGrade;

    private Boolean eventInProgress;
    private List<HashtagName> hashtags;
    private List<ConvenienceName> conveniences;
    private SortType sortType;
}
