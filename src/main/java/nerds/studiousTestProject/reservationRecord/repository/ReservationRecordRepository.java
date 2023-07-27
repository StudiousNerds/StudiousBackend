package nerds.studiousTestProject.reservationRecord.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import org.springframework.stereotype.Repository;

@Repository
public class ReservationRecordRepository {

    @PersistenceContext
    private EntityManager em;

    public ReservationRecord saveReservationRecord(ReservationRecord reservationRecord) {
        em.persist(reservationRecord);
        return reservationRecord;
    }
}
