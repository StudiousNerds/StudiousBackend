package nerds.studiousTestProject.support.fixture;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.Member.MemberBuilder;
import nerds.studiousTestProject.member.entity.member.MemberType;

import java.time.LocalDate;

import static nerds.studiousTestProject.member.entity.member.MemberType.DEFAULT;
import static nerds.studiousTestProject.member.entity.member.MemberType.KAKAO;
import static nerds.studiousTestProject.member.entity.member.MemberType.NAVER;

public enum MemberFixture {

    BEAVER("길가은", "비버", "rlfrkdms@naver.com", "rlfrkdms", DEFAULT, LocalDate.of(1999, 12, 18), "01012345678"),
    BURNED_POTATO("김민우", "탄감자", "rlaalsdn@naver.com", "rlaalsdn", KAKAO, LocalDate.of(1999, 12, 18), "01012345678"),
    POTATO("최보현", "말감", "chlqhgus@naver.com", "chlqhgus", NAVER, LocalDate.of(1999, 12, 18), "01012345678");

    private final String name;
    private final String nickname;
    private final String email;
    private final String password;
    private final MemberType type;
    private final LocalDate birthday;
    private final String phoneNumber;

    MemberFixture(String name, String nickname, String email, String password, MemberType type, LocalDate birthday, String phoneNumber) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.type = type;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
    }

    public Member 생성(){
        return 생성(null);
    }

    public Member 생성(Long id){
        return 기본_정보_빌더_생성(id).build();
    }

    public Member 기본_정보_생성(Long id, String password, String nickname, MemberType type, LocalDate birthday, String phoneNumber) {
        return 기본_정보_빌더_생성(id)
                .password(password)
                .nickname(nickname)
                .type(type)
                .birthday(birthday)
                .phoneNumber(phoneNumber)
                .build();
    }

    public MemberBuilder 기본_정보_빌더_생성(Long id){
        return Member.builder()
                .name(this.name)
                .nickname(this.nickname)
                .email(this.email)
                .password(this.password)
                .type(this.type)
                .birthday(this.birthday)
                .phoneNumber(this.phoneNumber)
                .usable(true);
    }
}
