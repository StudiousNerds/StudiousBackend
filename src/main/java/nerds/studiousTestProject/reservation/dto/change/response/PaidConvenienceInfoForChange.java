package nerds.studiousTestProject.reservation.dto.change.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.dto.PaidConvenienceInfo;

import java.util.List;

@Getter
@Builder
public class PaidConvenienceInfoForChange {

    private List<PaidConvenienceInfo> paidList;
    private List<PaidConvenienceInfo> notPaidList;

    public static PaidConvenienceInfoForChange of(List<PaidConvenienceInfo> paidConvenienceList, List<PaidConvenienceInfo> notPaidConvenienceList) {
        return PaidConvenienceInfoForChange.builder()
                .paidList(paidConvenienceList)
                .notPaidList(notPaidConvenienceList)
                .build();
    }
}
