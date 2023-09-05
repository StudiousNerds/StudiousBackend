//package nerds.studiousTestProject.member.validator.register;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import lombok.RequiredArgsConstructor;
//import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
//import nerds.studiousTestProject.member.repository.MemberRepository;
//
//@RequiredArgsConstructor
//public class SignUpDuplicatedPhoneNumberCheckValidator implements ConstraintValidator<SignUpDuplicatedPhoneNumberCheck, SignUpRequest> {
//    private final MemberRepository memberRepository;
//
//    @Override
//    public void initialize(SignUpDuplicatedPhoneNumberCheck constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
//        return !memberRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber());
//    }
//}
