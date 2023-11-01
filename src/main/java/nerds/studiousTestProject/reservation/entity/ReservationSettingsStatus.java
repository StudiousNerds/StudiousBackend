package nerds.studiousTestProject.reservation.entity;

public enum ReservationSettingsStatus {

    ALL("전체"),
    USING("이용 중"),
    BEFORE_USING("이용 전"),
    AFTER_USING("이용 완료"),
    CANCELED("취소");

    private final String statusMessage;

    ReservationSettingsStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public static class NAME {
        public static final String ALL = "ALL";
    }
}
