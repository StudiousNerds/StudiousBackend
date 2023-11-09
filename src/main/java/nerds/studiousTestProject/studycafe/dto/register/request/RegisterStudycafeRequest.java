package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import nerds.studiousTestProject.convenience.dto.register.RegisterConvenienceRequest;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.refundpolicy.dto.register.RegisterRefundPolicyRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.NearestStationResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@Data
public class RegisterStudycafeRequest {
    @NotBlank(message = "스터디카페 이름은 필수입니다.")
    private String name;    // 스터디카페 이름

    @NotNull(message = "스터디카페 주소는 필수입니다.")
    @Valid
    private RegisterAddressRequest addressInfo;    // 주소 정보

    @Size(min = 20, max = 100, message = "공간 소개 글은 최소 20자에서 최대 100자 사이여야 합니다.")
    private String introduction;    // 공간 소개

    @NotBlank(message = "전화번호는 필수입니다.")
    private String tel;

    @Size(min = 8, max = 8, message = "운영 시간은 필수입니다.")
    @Valid
    private List<RegisterOperationInfoRequest> operationInfos;   // 운영 시간

    @Size(min = 1, message = "공통 편의시설을 1개 이상 선택해주세요.")
    @Valid
    private List<RegisterConvenienceRequest> convenienceInfos; // 카페 편의 시설

    @Size(min = 1, message = "유의 사항을 1개 이상 등록해주세요.")
    private List<String> notices;   // 유의 사항

    @Size(min = 9, max = 9, message = "환불 정책을 입력해주세요.")
    @Valid
    private List<RegisterRefundPolicyRequest> refundPolicies;  // 환불 정책

    public Studycafe toEntity(Member member, NearestStationResponse nearestStationResponse) {
        return Studycafe.builder()
                .name(name)
                .member(member)
                .address(addressInfo.of())
                .tel(tel)
                .walkingTime(nearestStationResponse.getWalkingTime())
                .nearestStation(nearestStationResponse.getNearestStation())
                .introduction(introduction)
                .build();
    }
}
