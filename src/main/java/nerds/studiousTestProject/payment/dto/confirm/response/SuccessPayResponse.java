package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.Getter;

@Getter
public class SuccessPayResponse {

    private Long reservationRecordId;

    public SuccessPayResponse(Long reservationRecordId) {
        this.reservationRecordId = reservationRecordId;
    }
}
