package nerds.studiousTestProject.reservation.dto.show.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.refundpolicy.dto.RefundPolicyResponse;
import nerds.studiousTestProject.reservation.dto.ReserveUserResponse;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.dto.PlaceInfo;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class ReserveResponse {

    private PlaceInfo place;
    private List<String> conveniences;
    private List<PaidConvenienceResponse> paidConveniences;
    private ReserveUserResponse user;
    private List<RefundPolicyResponse> refundPolicies;

    public static ReserveResponse of(Member member, Room room, Studycafe studycafe, List<Convenience> conveniences, List<RefundPolicy> refundPolicyList) {
        return ReserveResponse.builder()
                .conveniences(getRoomConvenienceList(conveniences))
                .paidConveniences(getPaidConvenienceList(conveniences))
                .place(PlaceInfo.of(studycafe, room))
                .user(ReserveUserResponse.from(member))
                .refundPolicies(getRefundPolicyInfo(refundPolicyList))
                .build();
    }

    private static List<RefundPolicyResponse> getRefundPolicyInfo(List<RefundPolicy> refundPolicyList) {
        return refundPolicyList.stream()
                .sorted(Comparator.comparing((RefundPolicy refundPolicy) -> refundPolicy.getRemaining().getRemain()).reversed())
                .map(RefundPolicyResponse::from)
                .collect(Collectors.toList());
    }

    private static List<PaidConvenienceResponse> getPaidConvenienceList(List<Convenience> conveniences) {
        List<PaidConvenienceResponse> paidConvenienceResponseList = conveniences.stream().filter(convenience -> !convenience.isFree()).map(PaidConvenienceResponse::from).toList();
        return paidConvenienceResponseList;
    }

    private static List<String> getRoomConvenienceList(List<Convenience> conveniences) {
        List<String> roomConvenienceList = conveniences.stream().map(convenience -> convenience.getName().name()).toList();
        return roomConvenienceList;
    }


}

