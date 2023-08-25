package nerds.studiousTestProject.member.validator.register;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SignUpDuplicatedNicknameCheckValidator.class)
public @interface SignUpDuplicatedNicknameCheck {
    String message() default "이미 존재하는 닉네임입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
