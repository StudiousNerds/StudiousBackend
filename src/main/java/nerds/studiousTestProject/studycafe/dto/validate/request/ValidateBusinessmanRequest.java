package nerds.studiousTestProject.studycafe.dto.validate.request;

import lombok.Data;

import java.util.List;

@Data
public class ValidateBusinessmanRequest {
    private List<String> b_no;    // 사업자 등록번호
}
