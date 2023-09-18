package nerds.studiousTestProject.payment.dto.confirm.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class ConfirmFailResponse {
    private String message;
    private int statusCode;

    public static ConfirmFailResponse of(String message) {
        return ConfirmFailResponse.builder()
                .message(message)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .build();
    }

}
