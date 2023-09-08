package nerds.studiousTestProject.member.dto.inquire.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberType;

@Builder
@Data
public class MemberInfoResponse {
    private String name;
    private String nickname;
    private String email;
    private MemberType type;
    private String photo;
    private String phoneNumber;

    public static MemberInfoResponse of(Member member) {
        return MemberInfoResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .type(member.getType())
                .photo(member.getPhoto())
                .phoneNumber(member.getPhoneNumber())
                .build();
    }
}
