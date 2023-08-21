package nerds.studiousTestProject.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    /**
     * memberId를 사용하여 UserDetailsService 구현
     * @param username the username identifying the user whose data is required.
     * @return 현재 사용자의 정보
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, NumberFormatException {
        return memberRepository.findById(Long.valueOf(username))
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    private UserDetails createUserDetails(Member member) {
        List<String> roles = member.getRoles().stream().map(r -> r.getValue().name()).toList();
        User.UserBuilder userBuilder = User.builder()
                .username(member.getUsername())
                .password(member.getPassword());

        for (String role : roles) {
            userBuilder.roles(role);
        }

        return userBuilder.build();
    }
}