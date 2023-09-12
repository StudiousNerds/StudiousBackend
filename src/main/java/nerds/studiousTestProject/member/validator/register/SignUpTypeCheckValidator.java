//package nerds.studiousTestProject.member.validator.register;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
//import nerds.studiousTestProject.member.entity.member.MemberType;
//
//public class SignUpTypeCheckValidator implements ConstraintValidator<SignUpTypeCheck, SignUpRequest> {
//    @Override
//    public void initialize(SignUpTypeCheck constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
//        // 이 검증기가 동작하는 과정에서 아직 Json Parser가 작동하지 않으므로 null 처리를 해줘야 한다... 이 부분 좀 이상해
//        MemberType type = signUpRequest.getType() == null ? MemberType.DEFAULT : signUpRequest.getType();
//        if (signUpRequest.getProviderId() == null && !type.equals(MemberType.DEFAULT)) {
//            return false;
//        }
//
//        if (signUpRequest.getProviderId() != null && type.equals(MemberType.DEFAULT)) {
//            return false;
//        }
//
//        if (signUpRequest.getPassword() == null && type.equals(MemberType.DEFAULT)) {
//            return false;
//        }
//
//        return true;
//    }
//}
