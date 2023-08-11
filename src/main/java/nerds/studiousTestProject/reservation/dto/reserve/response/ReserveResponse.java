package nerds.studiousTestProject.reservation.dto.reserve.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInResponse;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class ReserveResponse {

    private String cafeName;
    private String studycafePhoto;
    private String roomName;
    private List<String> conveniences;
    private List<PaidConvenience> paidConveniences;
    private String username;
    private String userPhoneNumber;
    private List<RefundPolicyInResponse> refundPolicy;

    public static ReserveResponse of(Member member, Room room, Studycafe studycafe) {
        List<Convenience> convenienceLists = studycafe.getConveniences();
        List<String> convenienceList = new ArrayList<>();
        List<PaidConvenience> paidConvenienceList = new ArrayList<>();
        for (Convenience convenience : convenienceLists) {
            convenienceList.add(convenience.getName().name());
            if (!convenience.isFree()) {
                paidConvenienceList.add(PaidConvenience.from(convenience));
            }
        }
        return ReserveResponse.builder()
                .conveniences(convenienceList)
                .paidConveniences(paidConvenienceList)
                .cafeName(studycafe.getName())
                .roomName(room.getName())
                .studycafePhoto(studycafe.getPhoto())
                .username(member.getName())
                .userPhoneNumber(member.getPhoneNumber())
                .refundPolicy(
                        studycafe.getRefundPolicies().stream()
                        .map(RefundPolicyInResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }


}

