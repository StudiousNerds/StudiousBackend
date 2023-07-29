package nerds.studiousTestProject.user.service.token;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.ErrorCode;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.user.entity.token.RefreshToken;
import nerds.studiousTestProject.user.repository.token.RefreshTokenRepository;
import nerds.studiousTestProject.user.util.JwtTokenConst;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken save(Long memberId, String refreshToken) {
        return refreshTokenRepository.save(
                RefreshToken.from(
                        memberId,
                        refreshToken,
                        JwtTokenConst.REFRESH_TOKEN_EXPIRE_TIME
                )
        );
    }

    public RefreshToken findByMemberId(Long memberId) {
        return refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_TOKEN));
    }

    public void deleteByMemberId(Long memberId) {
        refreshTokenRepository.deleteById(memberId);
    }
}
