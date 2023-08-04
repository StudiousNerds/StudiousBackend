package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Builder
@Getter
public class ReserveResponse {

    private String cafeName;
    private String studycafePhoto;
    private String roomName;
    private List<String> conveniences;
    private List<PaidConvenience> paidConveniences;
    private String username;
    private String userPhoneNumber;
    private List<RefundPolicyInResponse> refundPolicy;

}
