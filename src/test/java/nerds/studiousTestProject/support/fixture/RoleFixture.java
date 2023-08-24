package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberRole;
import nerds.studiousTestProject.member.entity.member.Role;

public enum RoleFixture {
    USER(MemberRole.USER),
    ADMIN(MemberRole.ADMIN),
    SUPER_ADMIN(MemberRole.SUPER_ADMIN);

    private final MemberRole value;

    RoleFixture(MemberRole value) {
        this.value = value;
    }

    public Role 멤버_생성(Member member) {
        return Role.builder()
                .member(member)
                .value(value)
                .build();
    }
}
