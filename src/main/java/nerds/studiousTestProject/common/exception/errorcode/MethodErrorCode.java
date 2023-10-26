package nerds.studiousTestProject.common.exception.errorcode;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MethodErrorCode {
    GET_METHOD_NOT_ALLOW("GET", "GET 메소드는 지원하지 않습니다."),
    POST_METHOD_NOT_ALLOW("POST", "POST 메소드는 지원하지 않습니다."),
    PUT_METHOD_NOT_ALLOW("PUT", "PUT 메소드는 지원하지 않습니다."),
    PATCH_METHOD_NOT_ALLOW("PATCH", "PATCH 메소드는 지원하지 않습니다."),
    DELETE_METHOD_NOT_ALLOW("DELETE", "DELETE 메소드는 지원하지 않습니다.");

    private final static Map<String, String> ENUM_MAP = Collections.unmodifiableMap(
            Stream.of(values()).collect(Collectors.toMap(MethodErrorCode::getMethodName, MethodErrorCode::name))
    );
    private final String methodName;
    private final String message;

    MethodErrorCode(String methodName, String message) {
        this.methodName = methodName;
        this.message = message;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getMessage() {
        return message;
    }

    public String getEntryMessage(String[] supportedMethods) {
        return message +  " 사용 가능한 메소드 : " + Arrays.toString(supportedMethods);
    }

    public static MethodErrorCode of(String methodName) {
        return MethodErrorCode.valueOf(ENUM_MAP.get(methodName));
    }
}
