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
public class ShowManagedDetailsResponse {
    private String name;
    private ModifyAddressResponse addressInfo;
    private String introduction;
    private List<ModifyOperationInfoResponse> operationInfos;
    private List<ModifyConvenienceResponse> convenienceInfos;
    private List<String> photos;
    private List<String> notices;
    private List<ModifyRefundPolicyResponse> refundPolicies;

    public static ShowManagedDetailsResponse from(final Studycafe studycafe,
                                                  final ModifyAddressResponse modifyAddressResponse,
                                                  final List<ModifyConvenienceResponse> modifyConvenienceResponses,
                                                  final List<ModifyOperationInfoResponse> modifyOperationInfoResponses,
                                                  final List<ModifyRefundPolicyResponse> modifyRefundPolicyResponses,
                                                  final List<String> photos,
                                                  final List<String> notices) {
        return ShowManagedDetailsResponse.builder()
                .name(studycafe.getName())
                .addressInfo(modifyAddressResponse)
                .introduction(studycafe.getIntroduction())
                .operationInfos(modifyOperationInfoResponses)
                .convenienceInfos(modifyConvenienceResponses)
                .photos(photos)
                .notices(notices)
                .refundPolicies(modifyRefundPolicyResponses)
                .build();
    }
}
