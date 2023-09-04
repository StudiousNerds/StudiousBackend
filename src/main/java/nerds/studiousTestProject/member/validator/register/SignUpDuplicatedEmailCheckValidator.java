//package nerds.studiousTestProject.member.validator.register;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import lombok.RequiredArgsConstructor;
//import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
//import nerds.studiousTestProject.member.entity.member.MemberType;
//import nerds.studiousTestProject.member.repository.MemberRepository;
//
//@RequiredArgsConstructor
//public class SignUpDuplicatedEmailCheckValidator implements ConstraintValidator<SignUpDuplicatedEmailCheck, SignUpRequest> {
//    private final MemberRepository memberRepository;
//
//    @Override
//    public void initialize(SignUpDuplicatedEmailCheck constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
//        Long providerId = signUpRequest.getProviderId();
//        MemberType type = MemberType.handle(signUpRequest.getType());
//
//        if (providerId != null && memberRepository.existsByProviderIdAndType(providerId, type)) {
//            return false;
//        }
//
//        if (memberRepository.existsByEmailAndType(signUpRequest.getEmail(), type)) {
//            return false;
//        }
//
//        return true;
//    }
//}
