package nerds.studiousTestProject.studycafe.dto.register;

import jakarta.validation.Valid;
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
    @NotNull(message = "사업자 정보는 필수입니다.")
    @Valid
    private BusinessInfo businessInfo;

    @NotNull(message = "스터디카페 정보는 필수입니다.")
    @Valid
    private CafeInfo cafeInfo;

    @NotNull(message = "스터디룸 정보는 필수입니다.")
    @Valid
    private List<RoomInfo> roomInfos;

    @Data
    public static class CafeInfo {
        @NotBlank(message = "스터디카페 이름은 필수입니다.")
        private String name;    // 스터디카페 이름

        @NotNull(message = "스터디카페 주소는 필수입니다.")
        @Valid
        private AddressInfo addressInfo;    // 주소 정보

        @Size(min = 20, max = 100, message = "공간 소개 글은 최소 20자에서 최대 100자 사이여야 합니다.")
        private String introduction;    // 공간 소개

        @Size(min = 8, max = 8, message = "운영 시간은 필수입니다.")
        @Valid
        private List<OperationInfo> operationInfos;   // 운영 시간

        @Size(min = 1, message = "공통 편의시설을 1개 이상 선택해주세요.")
        @Valid
        private List<ConvenienceName> conveniences; // 카페 편의 시설

        @Size(min = 1, message = "스터디카페 사진을 1개 이상 등록해주세요.")
        private List<String> photos;    // 카페 사진

        @Size(min = 1, message = "유의 사항을 1개 이상 등록해주세요.")
        private List<String> notices;   // 유의 사항

        @Size(min = 9, max = 9, message = "환불 정책을 입력해주세요.")
        @Valid
        private List<RefundPolicy> refundPolicies;  // 환불 정책

        @Data
        public static class AddressInfo {
            @NotBlank(message = "위도는 필수입니다.")
            private String latitude;    // 위도

            @NotBlank(message = "경도는 필수입니다.")
            private String longitude;   // 경도

            @NotBlank(message = "우편 번호는 필수입니다.")
            private String zipcode; // 우편 번호

            @NotBlank(message = "기본 주소는 필수입니다.")
            private String basic;   // 기본 주소

            @NotBlank(message = "상세 주소는 필수입니다.")
            private String detail;  // 상세 주소
        }

        @Data
        public static class OperationInfo {
            @NotNull(message = "요일 입력은 필수입니다.")
            @Valid
            private Week week;    // 요일 (추후, 열거체로 리펙토링)

            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime startTime;    // 시작 시간

            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
            private LocalTime endTime;  // 마감 시간

            @NotNull(message = "24시간 여부를 체크해주세요.")
            private Boolean allDay; // 24시간 여부

            @NotNull(message = "휴일 여부를 체크해주세요.")
            private Boolean closed; // 휴일 여부
        }

        @Data
        public static class RefundPolicy {
            @Range(min = 0, max = 8, message = "날짜는 최소 0에서 최대 8 사이 수여야 합니다.")
            private Integer day;    // 이용까지 남은 날짜

            @Range(min = 0, max = 100, message = "환불 비율는 최소 0에서 최대 100 사이 수여야 합니다.")
            private Integer rate;   // 환불 비율
        }
    }

    @Data
    public static class RoomInfo {
        @NotBlank(message = "스터디룸 이름은 필수입니다.")
        private String name;    // 스터디룸 이름

        @Positive(message = "기준 인원 수는 필수입니다.")
        private Integer standardHeadCount;  // 기준 인원 수

        @Positive(message = "최소 인원 수는 필수입니다.")
        private Integer minHeadCount;  // 최소 인원 수

        @Positive(message = "최대 인원 수는 필수입니다.")
        private Integer maxHeadCount;  // 최대 인원 수

        @Positive(message = "최소 이용 가능 시간은 필수입니다.")
        private Integer minUsingTime;   // 최소 이용 가능 시간

        @Positive(message = "가격은 필수입니다.")
        private Integer price;  // 가격

        @NotNull(message = "가격 기준은 필수입니다.")
        @Valid
        private PriceType type; // 가격 기준

        @Size(min = 1, message = "룸 편의시설을 1개 이상 선택해주세요.")
        @Valid
        private List<ConvenienceName> conveniences; // 룸 편의시설

        @Size(min = 1, message = "스터디룸 사진을 1개 이상 선택해주세요.")
        private List<String> photos;    // 룸 사진
    }
}