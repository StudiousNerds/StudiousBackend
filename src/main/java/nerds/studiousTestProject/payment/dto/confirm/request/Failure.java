package nerds.studiousTestProject.payment.dto.confirm.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class Failure {
    private String code;
    private String message;
}