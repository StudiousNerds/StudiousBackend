package nerds.studiousTestProject.room.dto.register;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.convenience.dto.register.RegisterConvenienceRequest;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.room.entity.Room;

import java.util.List;

@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class RegisterRoomRequest {
    @NotBlank(message = "스터디룸 이름은 필수입니다.")
    private String name;    // 스터디룸 이름

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
    private List<RegisterConvenienceRequest> convenienceInfos; // 룸 편의시설

    public Room toEntity() {
        return Room.builder()
                .name(name)
                .minHeadCount(minHeadCount)
                .maxHeadCount(maxHeadCount)
                .minUsingTime(minUsingTime)
                .price(price)
                .type(type)
                .build();
    }
}
