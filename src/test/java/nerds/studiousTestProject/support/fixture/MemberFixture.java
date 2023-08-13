package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.Member.MemberBuilder;

public enum MemberFixture {

    BEAVER("길가은", "rlfrkdms@naver.com"),
    BURNED_POTATO("김민우", "rlaalsdn@naver.com"),
    POTATO("최보현", "chlqhgus@naver.com");

    private final String name;
    private final String email;

    MemberFixture(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public Member 생성(){
        return 생성(null);
    }

    public Member 생성(Long id){
        return 기본_정보_빌더_생성(id).build();
    }

    public MemberBuilder 기본_정보_빌더_생성(Long id){
        return Member.builder()
                .name(this.name)
                .email(this.email);
    }
}
