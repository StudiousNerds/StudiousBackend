package nerds.studiousTestProject.member.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.errorcode.ErrorCode;
import nerds.studiousTestProject.member.repository.MemberRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Cacheable(value = "UserCacheStore", key = "#username")
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
        return memberRepository.findById(Long.valueOf(username))
                .map(UserDetails.class::cast)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
    }
}