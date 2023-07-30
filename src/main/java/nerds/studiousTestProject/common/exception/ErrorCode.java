package nerds.studiousTestProject.common.exception;

public enum ErrorCode {
    MISMATCH_EMAIL("이메일이 일치하지 않습니다."),
    MISMATCH_PASSWORD("비밀번호가 일치하지 않습니다."),
    MISMATCH_PHONE_NUMBER("전화번호가 일치하지 않습니다."),
    NOT_EXIST_PASSWORD("비밀번호가 존재하지 않습니다. 일반회원인 경우 비밀번호는 필수입니다."),
    ALREADY_EXIST_USER("이미 존재하는 회원입니다."),
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
    NOT_FOUND_SOCIAL_INFO("알맞는 소셜 서비스를 찾을 수 없습니다."),
    NOT_DEFAULT_TYPE_USER("소셜 연동 계정입니다. 소셜 로그인을 사용하여 로그인해주세요."),
    NOT_EXIST_PROVIDER_ID("소셜 회원가입에서 providerId 값이 존재하지 않습니다."),
    WEB_CLIENT_ERROR("웹 API 호출 예외 발생. 자세한 건 서버 로그를 참고하세요."),
    NOT_FOUND_DATE("날짜 선택을 해주세요."),
    START_TIME_AFTER_THAN_END_TIME("시작 시간은 끝 시간보다 클 수 없습니다.");

    private final String message;
    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
