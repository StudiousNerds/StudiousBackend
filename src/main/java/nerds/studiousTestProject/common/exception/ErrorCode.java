package nerds.studiousTestProject.common.exception;

public enum ErrorCode {

    NOT_FOUND_ROOM("스터디룸을 찾을 수 없습니다."),
    NOT_FOUND_RESERVATION_RECORD("예약내역을 찾을 수 없습니다."),
    NOT_FOUND_PAYMENT("결제 내역을 찾을 수 없습니다."),
    INVALID_REQUEST_BODY_TYPE_TO_TOSS("토스에게 요청하는 값은 null 값이 있어선 안됩니다."),
    NOT_EXIST_PAGE("잘못된 페이지입니다."),
    MISMATCH_EMAIL("이메일이 일치하지 않습니다."),
    MISMATCH_PASSWORD("비밀번호가 일치하지 않습니다."),
    MISMATCH_PHONE_NUMBER("전화번호가 일치하지 않습니다."),
    NOT_EXIST_PASSWORD("비밀번호가 존재하지 않습니다. 일반회원인 경우 비밀번호는 필수입니다."),
    ALREADY_EXIST_USER("이미 존재하는 회원입니다."),
    ALREADY_EXIST_NICKNAME("이미 존재하는 닉네임입니다."),
    EXPIRE_USER("탈퇴된 계정입니다."),
    ALREADY_EXIST_PHONE_NUMBER("해당 전화번호로 가입한 계정이 이미 존재합니다."),
    ALREADY_LOGOUT_USER("로그아웃된 회원입니다."),
    MISMATCH_USERNAME_TOKEN("이메일과 토큰값이 일치하지 않습니다."),
    NOT_AUTHORIZE_ACCESS("인증되지 않은 접근입니다."),
    CHECK_FAIL_TOKEN("토큰 검증에 실패했습니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN_VALID_TIME("토큰의 유효기간이 만료되었습니다."),
    NOT_FOUND_TOKEN("토큰을 찾을 수 없습니다."),
    NOT_FOUND_USER("일치하는 회원 정보가 없습니다."),
    MISMATCH_TOKEN("토큰명이 일치하지 않습니다."),
    NOT_SUPPORTED_JWT("JWT 토큰이 지원하지 않습니다."),
    NOT_EXPIRED_REFRESH_TOKEN("Refresh Token이 만료되지 않았습니다."),
    INVALID_MEMBER_TYPE("회원 타입이 올바르지 않습니다"),
    NOT_FOUND_SOCIAL_INFO("알맞는 소셜 서비스를 찾을 수 없습니다."),
    NOT_DEFAULT_TYPE_USER("소셜 연동 계정입니다. 소셜 로그인을 사용하여 로그인해주세요."),
    NOT_EXIST_PROVIDER_ID("소셜 회원가입에서 providerId 값이 존재하지 않습니다."),
    WEB_CLIENT_ERROR("웹 API 호출 예외 발생. 자세한 건 서버 로그를 참고하세요."),
    NOT_FOUND_DATE("날짜 선택을 해주세요."),
    INVALID_REQUEST_BODY_TYPE("유효하지 않은 요청입니다."),
    NOT_FOUND_STUDYCAFE("스터디카페를 찾을 수 없습니다."),
    NOT_FOUND_HASHTAG("해시태그를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER("멤버를 찾을 수 없습니다."),
    INVALID_SORT_TYPE("정렬 기준이 잘못되었습니다."),
    NOT_PARSING_BODY("JSON 형식이 잘못되었습니다."),
    NOT_FOUND_REVIEW("리뷰를 찾을 수 없습니다."),
    NOT_FOUND_START_TIME("시작 시간을 찾을 수 없습니다."),
    NOT_FOUND_END_TIME("종료 시간을 찾을 수 없습니다."),
    INVALID_RESERVATION_CANCEL_DATE("이미 지난 예약입니다."),
    PAYMENT_NOT_CANCELED("결제가 취소되지 않았습니다."),
    CANT_CONVERT_TO_RESERVATION_SETTING_STATUS("해당하는 탭이 없습니다."),
    INVALID_RESERVE_DATE("유효하지 않은 예약 날짜입니다."),
    INVALID_USING_TIME("유효하지 않은 예약 이용 시간입니다."),
    MISCALCULATED_USING_TIME("잘못 계산된 이용 시간입니다."),
    USING_TIME_NOT_PER_HOUR("이용시간은 시간단위여야 합니다."),
    START_TIME_AFTER_THAN_END_TIME("시작 날짜는 끝나는 날짜보다 항상 이전이어야합니다."),
    MISMATCH_PAYMENT_METHOD("결제 수단이 일치하지 않습니다."),
    INVALID_PAYMENT_SECRET("올바른 시크릿 값이 아닙니다."),
    INVALID_REFUND_RECEIVE_ACCOUNT_INFO("가상계좌 환불 시 환불 계좌는 필수입니다."),
    MISMATCH_MEMBER_AND_STUDYCAFE("회원 정보와 일치하는 스터디카페를 찾을 수 없습니다");

    private final String message;
    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
