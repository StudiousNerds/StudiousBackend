package nerds.studiousTestProject.studycafe.dto.register;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessInfo {
    @Size(min = 10, max = 10)
    private String number; // 사업자 등록 번호

    @Size(min = 2, max = 4)
    private String name;    // 대표자 성명

    @Size(min = 10, max = 14)   // 계좌 번호 길이는 10 ~ 14자
    private AccountInfo accountInfo;    // 계좌 정보
    private String registration;    // 사업자 등록증
}
