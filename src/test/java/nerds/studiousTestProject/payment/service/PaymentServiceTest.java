package nerds.studiousTestProject.payment.service;

import nerds.studiousTestProject.exception.InvalidRequestToTossException;
import nerds.studiousTestProject.payment.repository.PaymentRepository;
import nerds.studiousTestProject.reservationRecord.service.ReservationRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    WebClient webClient;

    @Mock
    ReservationRecordService reservationRecordService;

    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentService paymentService;

    @Test
    void 토스에게_보내는_요청엔_null값이_없어야_한다(){
        assertThatThrownBy(()->paymentService.confirmPayToToss(null, null, null))
                .isExactlyInstanceOf(InvalidRequestToTossException.class);
    }



}