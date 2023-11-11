package nerds.studiousTestProject.studycafe.dto.show.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyAddressResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyConvenienceResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyOperationInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyRefundPolicyResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.List;

@Builder
@Data
public class ShowManagedStudycafeDetailsResponse {
    private String name;
    private ModifyAddressResponse address;
    private String introduction;
    private List<ModifyOperationInfoResponse> operationInfos;
    private List<ModifyConvenienceResponse> conveniences;
    private List<String> photos;
    private List<String> notices;
    private List<ModifyRefundPolicyResponse> refundPolicies;

    public static ShowManagedStudycafeDetailsResponse of(final Studycafe studycafe,
                                                         final ModifyAddressResponse modifyAddressResponse,
                                                         final List<ModifyConvenienceResponse> modifyConvenienceResponses,
                                                         final List<ModifyOperationInfoResponse> modifyOperationInfoResponses,
                                                         final List<ModifyRefundPolicyResponse> modifyRefundPolicyResponses,
                                                         final List<String> photos,
                                                         final List<String> notices) {
        return ShowManagedStudycafeDetailsResponse.builder()
                .name(studycafe.getName())
                .address(modifyAddressResponse)
                .introduction(studycafe.getIntroduction())
                .operationInfos(modifyOperationInfoResponses)
                .conveniences(modifyConvenienceResponses)
                .photos(photos)
                .notices(notices)
                .refundPolicies(modifyRefundPolicyResponses)
                .build();
    }
}
