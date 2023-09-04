package nerds.studiousTestProject.member.controller;

import nerds.studiousTestProject.member.dto.signup.SignUpRequest;
import nerds.studiousTestProject.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;

class MemberControllerTest {

    @InjectMocks
    private MemberController memberController;

    @Mock
    private MemberService memberService;

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }


    @Test
    @DisplayName("회원 가입 성공")
    public void 회원가입_성공() throws Exception {

        // given
        SignUpRequest signUpRequest = signUpRequest();

        // when

        // then

    }

    private SignUpRequest signUpRequest() {
        return SignUpRequest.builder()
                .email("test@test.com")
                .password("123456")
                .name("김민우")
                .nickname("킹민우")
                .roles(Collections.singletonList("USER"))
                .phoneNumber("01090432652")
                .birthday(LocalDate.of(1999, 12, 18))
                .build();
    }
}