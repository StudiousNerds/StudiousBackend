package nerds.studiousTestProject.common.util;

import jakarta.annotation.Nullable;
import nerds.studiousTestProject.common.exception.NotAuthorizedException;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.security.Principal;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_AUTHORIZE_ACCESS;

public class LoggedInMemberResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoggedInMember.class);
    }

    @Override
    public Long resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Principal principal = webRequest.getUserPrincipal();

        try {
            return Long.parseLong(principal.getName());
        } catch (NullPointerException e) {
            if (parameter.hasParameterAnnotation(Nullable.class)) {
                return null;
            }

            throw new NotAuthorizedException(NOT_AUTHORIZE_ACCESS);
        }
    }
}
