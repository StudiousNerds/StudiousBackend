package nerds.studiousTestProject.user.service.bookmark;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import nerds.studiousTestProject.user.entity.member.Member;
import nerds.studiousTestProject.user.service.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookmarkService {
    private final MemberService memberService;
    private final StudycafeService studycafeService;

    @Transactional
    public ResponseEntity<?> registerBookmark(String accessToken, Long studycafeId){
        Member member = memberService.getMemberFromAccessToken(accessToken);
        Studycafe studyCafe = studycafeService.getStudyCafe(studycafeId);
        member.registerBookmark(studyCafe.getName());

        return ResponseEntity.status(HttpStatus.OK).body("북마크 등록에 성공했습니다.");
    }
}