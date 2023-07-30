package nerds.studiousTestProject.member.entity.member;

public enum MemberType {
   DEFAULT, NAVER, KAKAO, GOOGLE;

   public static MemberType handle(MemberType type) {
      return type == null ? MemberType.DEFAULT : type;
   }
}