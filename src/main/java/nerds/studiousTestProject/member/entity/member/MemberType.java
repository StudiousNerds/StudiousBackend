package nerds.studiousTestProject.member.entity.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import nerds.studiousTestProject.common.exception.BadRequestException;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_MEMBER_TYPE;

public enum MemberType {
   DEFAULT, NAVER, KAKAO, GOOGLE;

   public static MemberType handle(MemberType type) {
      return type == null ? MemberType.DEFAULT : type;
   }

   @JsonCreator
   public static MemberType handle(String value) {
      if (value == null) {
         return DEFAULT;
      }

      try {
         return valueOf(value);
      } catch (IllegalArgumentException e) {
         throw new BadRequestException(INVALID_MEMBER_TYPE);
      }
   }
}