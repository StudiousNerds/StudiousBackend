package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.room.entity.Room;

import java.util.List;

@Data
public class RoomInfoRequest {
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

    @NotNull(message = "룸 편의시설을 1개 이상 선택해주세요.")
    @Size(min = 1, message = "룸 편의시설을 1개 이상 선택해주세요.")
    @Valid
    private List<ConvenienceInfoRequest> convenienceInfos; // 룸 편의시설

    @NotNull(message = "스터디룸 사진을 1개 이상 선택해주세요.")
    @Size(min = 1, message = "스터디룸 사진을 1개 이상 선택해주세요.")
    private List<String> photos;    // 룸 사진

    public Room toEntity(String roomMainPhoto) {
        return Room.builder()
                .name(name)
                .photo(roomMainPhoto)
                .standardHeadCount(standardHeadCount)
                .minHeadCount(minHeadCount)
                .maxHeadCount(maxHeadCount)
                .minUsingTime(minUsingTime)
                .price(price)
                .type(type)
                .build();
    }
}
