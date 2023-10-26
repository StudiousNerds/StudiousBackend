package nerds.studiousTestProject.payment.dto.virtual.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.payment.entity.Payment;

import java.time.LocalDateTime;

@Getter
@Builder
public class VirtualAccountInfoResponse {

    private LocalDateTime dueDate;
    private String bankName;
    private String virtualAccount;

    public static VirtualAccountInfoResponse from(Payment payment) {
        return VirtualAccountInfoResponse.builder()
                .dueDate(payment.getDueDate())
                .virtualAccount(payment.getVirtualAccount())
                .bankName(payment.getBankCode().name())
                .build();
    }
}
