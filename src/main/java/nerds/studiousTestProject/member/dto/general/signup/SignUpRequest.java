package nerds.studiousTestProject.member.dto.general.signup;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.entity.member.MemberType;
import nerds.studiousTestProject.member.entity.member.Role;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private Long providerId;
    private MemberType type;
    private Date birthday;
    private String phoneNumber;
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
                .createdDate(new Date())
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
