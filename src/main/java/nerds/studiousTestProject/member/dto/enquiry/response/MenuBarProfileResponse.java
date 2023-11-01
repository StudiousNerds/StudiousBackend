package nerds.studiousTestProject.member.dto.enquiry.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nerds.studiousTestProject.member.entity.member.Member;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class MenuBarProfileResponse {
    private String photo;
    private String nickname;

    public static MenuBarProfileResponse from(Member member) {
        return MenuBarProfileResponse.builder()
                .photo(member.getPhoto())
                .nickname(member.getNickname())
                .build();
    }
}
