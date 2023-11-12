package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.dto.PaidConvenienceResponse;

import java.util.List;

@Getter
@Builder
public class PaidConvenienceForChangeResponse {

    private List<PaidConvenienceResponse> paidList;
    private List<PaidConvenienceResponse> notPaidList;

    public static PaidConvenienceForChangeResponse of(List<PaidConvenienceResponse> paidConvenienceList, List<PaidConvenienceResponse> notPaidConvenienceList) {
        return PaidConvenienceForChangeResponse.builder()
                .paidList(paidConvenienceList)
                .notPaidList(notPaidConvenienceList)
                .build();
    }
}
