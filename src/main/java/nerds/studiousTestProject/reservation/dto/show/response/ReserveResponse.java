package nerds.studiousTestProject.reservation.dto.show.response;

import lombok.Builder;
import lombok.Getter;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceType;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.refundpolicy.entity.RefundPolicy;
import nerds.studiousTestProject.reservation.dto.RefundPolicyInfo;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.studycafe.entity.Studycafe;

import java.util.Comparator;
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
    private List<RefundPolicyInfo> refundPolicy;

    public static ReserveResponse of(Member member, Room room, Studycafe studycafe, List<Convenience> conveniences, List<RefundPolicy> refundPolicyList) {
        return ReserveResponse.builder()
                .conveniences(getRoomConvenienceList(conveniences))
                .paidConveniences(getPaidConvenienceList(conveniences))
                .cafeName(studycafe.getName())
                .roomName(room.getName())
                .studycafePhoto(studycafe.getPhoto())
                .username(member.getName())
                .userPhoneNumber(member.getPhoneNumber())
                .refundPolicy(getRefundPolicyInfo(refundPolicyList))
                .build();
    }

    private static List<RefundPolicyInfo> getRefundPolicyInfo(List<RefundPolicy> refundPolicyList) {
        return refundPolicyList.stream()
                .sorted(Comparator.comparing((RefundPolicy refundPolicy) -> refundPolicy.getRemaining().getRemain()).reversed())
                .map(RefundPolicyInfo::from)
                .collect(Collectors.toList());
    }

    private static List<PaidConvenience> getPaidConvenienceList(List<Convenience> conveniences) {
        List<PaidConvenience> paidConvenienceList = conveniences.stream().filter(convenience -> !convenience.isFree()).map(PaidConvenience::from).toList();
        return paidConvenienceList;
    }

    private static List<String> getRoomConvenienceList(List<Convenience> conveniences) {
        List<String> roomConvenienceList = conveniences.stream().map(convenience -> convenience.getName().name()).toList();
        return roomConvenienceList;
    }


}

