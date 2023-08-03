package nerds.studiousTestProject.studycafe.dto.valid.request;

import lombok.Data;

import java.util.List;

@Data
public class BusinessInfoRequest {
    private List<String> b_no;    // 사업자 등록번호
}
