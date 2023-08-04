package nerds.studiousTestProject.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.member.MemberRepository;
import nerds.studiousTestProject.member.util.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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
                orElseThrow(() -> new NotFoundException(ErrorCode.MISMATCH_USERNAME_TOKEN));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            log.info("auth = {}", authentication);
            throw new NotFoundException(ErrorCode.NOT_AUTHORIZE_ACCESS);
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        if (!userDetails.getUsername().equals(member.getUsername())) {
            throw new NotFoundException(ErrorCode.NOT_AUTHORIZE_ACCESS);
        }

        return member;
    }
}
