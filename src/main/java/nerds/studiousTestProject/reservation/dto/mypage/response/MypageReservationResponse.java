package nerds.studiousTestProject.reservation.dto.mypage.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Builder
public class MypageReservationResponse {

    private List<ReservationRecordWithStatusResponse> reservations;
    private Integer pageNumber;
    private Integer totalPage;

    public static MypageReservationResponse of(List<ReservationRecordWithStatusResponse> reservationRecordWithStatusResponseList, Page<ReservationRecord> reservationRecordPage) {
        if(reservationRecordPage.isEmpty()) return null;
        return MypageReservationResponse.builder()
                .reservations(reservationRecordWithStatusResponseList)
                .pageNumber(reservationRecordPage.getNumber())
                .totalPage(reservationRecordPage.getTotalPages())
                .build();
    }
}
