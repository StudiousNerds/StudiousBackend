package nerds.studiousTestProject.reservation.dto.mypage.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import java.util.List;

@Getter
@Builder
public class MypageReservationResponse {
    private List<ReservationRecordInfoWithStatus> reservationRecordInfoWithStatusList;
    private Integer pageNumber;
    private Integer totalPage;

    public static MypageReservationResponse of(List<ReservationRecordInfoWithStatus> reservationRecordInfoWithStatusList, Page<ReservationRecord> reservationRecordPage) {
        if(reservationRecordPage.isEmpty()) return null;
        return MypageReservationResponse.builder()
                .reservationRecordInfoWithStatusList(reservationRecordInfoWithStatusList)
                .pageNumber(reservationRecordPage.getNumber() + 1)
                .totalPage(reservationRecordPage.getTotalPages())
                .build();
    }
}
