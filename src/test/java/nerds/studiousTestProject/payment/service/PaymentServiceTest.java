//package nerds.studiousTestProject.payment.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import nerds.studiousTestProject.common.exception.BadRequestException;
//import nerds.studiousTestProject.payment.PaymentFixture;
//import nerds.studiousTestProject.payment.repository.PaymentRepository;
//import nerds.studiousTestProject.reservation.service.ReservationRecordService;
//import okhttp3.mockwebserver.Dispatcher;
//import okhttp3.mockwebserver.MockResponse;
//import okhttp3.mockwebserver.MockWebServer;
//import okhttp3.mockwebserver.RecordedRequest;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.io.IOException;
//
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentServiceTest {
//
//    @InjectMocks
//    private PaymentService paymentService;
//    @Mock
//    private ReservationRecordService reservationRecordService;
//    @Mock
//    private PaymentRepository paymentRepository;
//
//    @Mock
//    private WebClient webClient;
//
//    public static MockWebServer mockWebServer;
//    private MockResponse confirmSuccessResponse;
//    private MockResponse cancelResponse;
//    private String mockServerUrl;
//    private Dispatcher dispatcher;
//
//
//    @BeforeAll
//    void setUp() throws IOException {
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();
//        mockServerUrl = mockWebServer.url("/").toString();
//        paymentService = new PaymentService(webClient, reservationRecordService, paymentRepository);
//        ObjectMapper objectMapper = new ObjectMapper();
//        confirmSuccessResponse = new MockResponse()
//                .setBody(objectMapper.writeValueAsString(PaymentFixture.createMockSuccessConfirmResponseFromToss()));
//        cancelResponse = new MockResponse()
//                .setBody(objectMapper.writeValueAsString(PaymentFixture.createMockCancelResponse()));
//        dispatcher = new Dispatcher() {
//            public MockResponse dispatch(RecordedRequest request) {
//                if (request.getPath().contains("confirm")) {
//                    return confirmSuccessResponse;
//                }
//                if (request.getPath().contains("cancel")) {
//                    return cancelResponse;
//                }
//                return new MockResponse().setResponseCode(404);
//            }
//        };
//        mockWebServer.setDispatcher(dispatcher);
//    }
//
//    @AfterAll
//    static void serverDown() throws IOException {
//        mockWebServer.shutdown();
//    }
//    @Test
//    void 토스에게_보내는_요청엔_null값이_없어야_한다(){
//        assertThatThrownBy(()->paymentService.confirmPayToToss(null, null, null))
//                .isExactlyInstanceOf(BadRequestException.class);
//    }
//
//
//
//
//}