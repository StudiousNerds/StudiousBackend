package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class CafeDetailsResponse {
    private String name;
    private AddressInfoResponse addressInfo;
    private String introduction;
    private List<OperationInfoResponse> operationInfos;
    private List<ConvenienceInfoResponse> convenienceInfos;
    private List<String> photos;
    private List<String> notices;
    private List<RefundPolicyResponse> refundPolicies;
}
