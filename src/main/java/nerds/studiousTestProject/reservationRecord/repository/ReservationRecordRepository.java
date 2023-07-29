package nerds.studiousTestProject.reservationRecord.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import nerds.studiousTestProject.reservationRecord.entity.ReservationRecord;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class ReservationRecordRepository {

    @PersistenceContext
    private EntityManager em;

    public ReservationRecord saveReservationRecord(ReservationRecord reservationRecord) {
        em.persist(reservationRecord);
        return reservationRecord;
    }

    public Optional<ReservationRecord> findByOrderId(String orderId){
        ReservationRecord reservationRecord = em.createQuery("select r from ReservationRecord r where r.orderId =: orderId", ReservationRecord.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
        return Optional.ofNullable(reservationRecord);
    }

    public Optional<ReservationRecord> findById(Long reservationRecordId){
        ReservationRecord reservationRecord = em.find(ReservationRecord.class, reservationRecordId);
        return Optional.ofNullable(reservationRecord);
    }

    public void remove(ReservationRecord reservationRecord) {
        em.remove(reservationRecord);
    }
}
