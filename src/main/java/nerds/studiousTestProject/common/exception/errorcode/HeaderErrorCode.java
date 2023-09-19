package nerds.studiousTestProject.common.exception.errorcode;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HeaderErrorCode {
    NOT_EXIST_AUTHORIZATION_HEADER("Authorization", "토큰이 존재하지 않거나 잘못되었습니다.");

    private static final Map<String, String> ENUM_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(HeaderErrorCode::getHeaderName, HeaderErrorCode::name))
    );
    private final String headerName;
    private final String message;

    HeaderErrorCode(String headerName, String message) {
        this.headerName = headerName;
        this.message = message;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getMessage() {
        return message;
    }

    public static HeaderErrorCode of(String headerName) {
        return HeaderErrorCode.valueOf(ENUM_MAP.get(headerName));
    }
}
