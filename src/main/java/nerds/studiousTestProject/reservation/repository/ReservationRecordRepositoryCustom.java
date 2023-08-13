package nerds.studiousTestProject.reservation.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.dto.mypage.response.ReservationSettingsStatus;
import nerds.studiousTestProject.reservation.entity.ReservationRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface ReservationRecordRepositoryCustom {

    Page<ReservationRecord> getReservationRecordsConditions(ReservationSettingsStatus tab, String studycafeName, LocalDate startDate, LocalDate endDate, Member member, Pageable pageable);
}
