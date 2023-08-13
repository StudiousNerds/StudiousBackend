package nerds.studiousTestProject.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.Member.MemberBuilder;

public enum MemberFixture {
    FIRST_MEMBER("최잡종","qhgus12@naver.com");

    private final String name;
    private final String email;

    MemberFixture(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Member 생성(Long id) {
        return 기본_정보_생성()
                .id(id)
                .build();
    }

    public MemberBuilder 기본_정보_생성() {
        return Member.builder()
                .name(this.name)
                .email(this.email);
    }
}
