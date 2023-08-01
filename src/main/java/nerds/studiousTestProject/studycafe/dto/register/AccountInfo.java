package nerds.studiousTestProject.studycafe.dto.register;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountInfo {
    private String name;        // 은행명 (추후, 열거체로 리펙토링)
    private String number;      // 계좌 번호
}
