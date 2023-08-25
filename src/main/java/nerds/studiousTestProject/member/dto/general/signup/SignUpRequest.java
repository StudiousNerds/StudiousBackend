package nerds.studiousTestProject.member.dto.general.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.entity.member.Role;
import nerds.studiousTestProject.member.validator.register.SignUpDuplicatedEmailCheck;
import nerds.studiousTestProject.member.validator.register.SignUpDuplicatedNicknameCheck;
import nerds.studiousTestProject.member.validator.register.SignUpDuplicatedPhoneNumberCheck;
import nerds.studiousTestProject.member.validator.register.SignUpTypeCheck;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@SignUpDuplicatedEmailCheck
@SignUpDuplicatedNicknameCheck
@SignUpDuplicatedPhoneNumberCheck
@SignUpTypeCheck
public class SignUpRequest {

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    private String nickname;

    private Long providerId;

    private MemberType type;

    @NotNull(message = "생일은 필수입니다.")
    private LocalDate birthday;

    @NotBlank(message = "전화번호는 공백일 수 없습니다.")
    private String phoneNumber;

    @NotNull(message = "권한은 필수입니다.")
    @Size(min = 1, message = "권한은 최소 1개 이상이여야 합니다.")
    private List<String> roles;

    public Member toEntity(String encodedPassword) {
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .providerId(providerId)
                .name(name)
                .nickname(nickname)
                .type(MemberType.handle(type))
                .birthday(birthday)
                .phoneNumber(phoneNumber)
                .createdDate(LocalDate.now())
                .usable(true)
                .resignedDate(null)
                .build();

        roles.stream().map(
                s -> Role.builder()
                        .value(MemberRole.valueOf(s))
                        .build()
        ).forEach(member::addRole);

        return member;
    }
}
