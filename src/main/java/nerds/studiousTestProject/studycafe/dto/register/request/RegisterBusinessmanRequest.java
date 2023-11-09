package nerds.studiousTestProject.studycafe.dto.register.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterBusinessmanRequest {
    @Size(min = 10, max = 10, message = "사업자 등록번호는 하이픈(-)을 포함하지 않는 10자리 숫자입니다.")
    private String number; // 사업자 등록 번호

    @Size(min = 2, max = 4, message = "대표자 성명이 잘못되었습니다.")
    private String name;    // 대표자 성명

    @NotNull(message = "계좌 정보는 필수입니다.")
    @Valid
    private RegisterAccountRequest account;    // 계좌 정보

    @NotBlank(message = "사업자 등록증은 필수입니다.")
    private String registration;    // 사업자 등록증
}
