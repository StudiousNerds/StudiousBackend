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

    public ReservationRecord findByOrderId(String orderId){
        return em.createQuery("select r from ReservationRecord r where r.orderId =: orderId", ReservationRecord.class)
                .setParameter(orderId, orderId)
                .getSingleResult();
    }

    public void deleteByOrderId(String orderId) {
        em.createQuery("delete from ReservationRecord r where r.orderId =: orderId")
                .setParameter(orderId, orderId)
                .executeUpdate();
        em.clear();
    }
}
