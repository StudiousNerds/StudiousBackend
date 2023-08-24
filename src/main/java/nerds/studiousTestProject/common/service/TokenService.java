package nerds.studiousTestProject.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotAuthorizedException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import static nerds.studiousTestProject.common.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public Member getMemberFromAccessToken(String accessToken) {
        String resolvedAccessToken = jwtTokenProvider.resolveToken(accessToken);
        Long memberId = jwtTokenProvider.parseToken(resolvedAccessToken);

        Member member =  memberRepository.findById(memberId).
                orElseThrow(() -> new NotAuthorizedException(MISMATCH_USERNAME_TOKEN));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.info("auth = {}", authentication);
            throw new NotAuthorizedException(NOT_AUTHORIZE_ACCESS);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!userDetails.getUsername().equals(member.getUsername())) {
            throw new NotAuthorizedException(NOT_AUTHORIZE_ACCESS);
        }

        return member;
    }
}
