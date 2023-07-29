package nerds.studiousTestProject.exception;

public class ReservationRecordNotFoundException extends NotFoundException{
    public ReservationRecordNotFoundException() {
        super(ErrorCode.RESERVATION_RECORD_NOT_FOUND, "예약내역을 찾을 수 없습니다.");
    }
}
