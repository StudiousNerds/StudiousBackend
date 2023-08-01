package nerds.studiousTestProject.studycafe.dto.register;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.studycafe.entity.Week;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.List;

@Builder
@Data
public class RegisterRequest {
    @NotNull
    private BusinessInfo businessInfo;

    @NotNull
    private CafeInfo cafeInfo;

    @NotNull
    private List<RoomInfo> roomInfos;

    @Data
    public static class CafeInfo {
        @NotBlank
        private String name;    // 스터디카페 이름

        @NotNull
        private AddressInfo addressInfo;    // 주소 정보

        @Size(min = 20, max = 100)
        private String introduction;    // 공간 소개

        @NotNull
        @Min(1)
        private List<OperationInfo> operationInfos;   // 운영 시간

        @NotNull
        @Min(1)
        private List<ConvenienceName> conveniences; // 카페 편의 시설

        @NotNull
        @Min(1)
        private List<String> photos;    // 카페 사진

        @NotNull
        @Min(1)
        private List<String> notices;   // 유의 사항

        @NotNull
        @Min(1)
        private List<RefundPolicy> refundPolicies;  // 환불 정책

        @Data
        public static class AddressInfo {
            @NotBlank
            private String basic;   // 기본 주소

            @NotBlank
            private String detail;  // 상세 주소
        }

        @Data
        public static class OperationInfo {
            @NotBlank
            private Week week;    // 요일 (추후, 열거체로 리펙토링)

            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime startTime;    // 시작 시간

            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime endTime;  // 마감 시간

            @NotNull
            private Boolean allDay; // 24시간 여부

            @NotNull
            private Boolean closed; // 휴일 여부
        }

        @Data
        public static class RefundPolicy {
            @Range(min = 0, max = 8)
            private Integer day;    // 이용까지 남은 날짜

            @Range(min = 0, max = 100)
            private Integer rate;   // 환불 비율
        }
    }

    @Data
    public static class RoomInfo {
        @NotBlank
        private String name;    // 스터디룸 이름

        @Positive
        private Integer standardHeadCount;  // 기준 인원 수

        @Positive
        private Integer minHeadCount;  // 최소 인원 수

        @Positive
        private Integer maxHeadCount;  // 최대 인원 수

        @Positive
        private Integer minUsingTime;   // 최소 이용 가능 시간

        @Positive
        private Integer price;  // 가격

        @NotNull
        private PriceType type; // 가격 기준

        @NotNull
        @Min(1)
        private List<ConvenienceName> conveniences; // 룸 편의시설

        @NotNull
        @Min(1)
        private List<String> photos;    // 룸 사진
    }
}