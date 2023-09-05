//package nerds.studiousTestProject.member.validator.register;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target(ElementType.TYPE)
//@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = SignUpTypeCheckValidator.class)
//public @interface SignUpTypeCheck {
//    String message() default "일반/소셜 회원가입에 필요한 파라미터가 잘못되었습니다.";
//    Class<?>[] groups() default {};
//    Class<? extends Payload>[] payload() default {};
//}
