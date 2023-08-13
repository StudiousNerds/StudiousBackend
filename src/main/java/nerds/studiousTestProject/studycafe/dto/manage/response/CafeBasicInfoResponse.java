package nerds.studiousTestProject.studycafe.dto.manage.response;

import lombok.Builder;
import lombok.Data;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

@Builder
@Data
public class CafeBasicInfoResponse {
    private Long id;
    private String name;
    private String address;
    private String photo;

    public static CafeBasicInfoResponse from(Studycafe studycafe) {
        return CafeBasicInfoResponse.builder()
                .id(studycafe.getId())
                .name(studycafe.getName())
                .address(studycafe.getAddress().getEntryAddress())
                .photo(studycafe.getPhoto())
                .build();
    }
}
