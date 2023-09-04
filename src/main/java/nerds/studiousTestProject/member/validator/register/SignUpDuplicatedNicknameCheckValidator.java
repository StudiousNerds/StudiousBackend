//package nerds.studiousTestProject.member.validator.register;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import lombok.RequiredArgsConstructor;
//import nerds.studiousTestProject.member.dto.general.signup.SignUpRequest;
//import nerds.studiousTestProject.member.repository.MemberRepository;
//
//@RequiredArgsConstructor
//public class SignUpDuplicatedNicknameCheckValidator implements ConstraintValidator<SignUpDuplicatedNicknameCheck, SignUpRequest> {
//    private final MemberRepository memberRepository;
//
//    @Override
//    public void initialize(SignUpDuplicatedNicknameCheck constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
//    }
//
//    @Override
//    public boolean isValid(SignUpRequest signUpRequest, ConstraintValidatorContext context) {
//        return !memberRepository.existsByNickname(signUpRequest.getNickname());
//    }
//}
