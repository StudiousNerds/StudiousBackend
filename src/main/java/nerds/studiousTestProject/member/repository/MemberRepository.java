package nerds.studiousTestProject.member.repository;

import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.entity.member.MemberType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByEmail(String email);
    Optional<Member> findByEmailAndType(String email, MemberType type);
    Optional<Member> findByProviderIdAndType(Long providerId, MemberType type);
    Optional<Member> findByPhoneNumber(String PhoneNumber);
    boolean existsByProviderIdAndType(Long providerId, MemberType type);
    boolean existsByEmailAndType(String email, MemberType type);
    boolean existsByPhoneNumber(String phoneNumber);
}