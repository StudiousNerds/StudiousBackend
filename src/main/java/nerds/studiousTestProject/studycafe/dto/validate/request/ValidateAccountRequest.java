package nerds.studiousTestProject.studycafe.dto.validate.request;

import lombok.Data;

@Data
public class ValidateAccountRequest {
    private String bank_tran_id;    // 은행 거래 고유 번호
    private String bank_code_std;   // 개설 기관 표준 코드
    private String account_num;     // 계좌 번호
    private String account_holder_info_type;    // 예금주 실명번호 구분 코드
    private String account_holder_info;     // 예금주 인증정보 (주민번호 앞 7자리)
    private String tran_dtime;      // 요청 일시(yymmddhhmmss 형태)
}
