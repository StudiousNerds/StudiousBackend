package nerds.studiousTestProject.payment.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.payment.util.totoss.RequestToToss;
import nerds.studiousTestProject.payment.util.fromtoss.PaymentResponseFromToss;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class PaymentGenerator {

    private final WebClient webClient;
    private static final String SECRET_KEY_PREFIX = "Basic ";
    private static final String SECRET_KEY = "test_sk_BE92LAa5PVb07oOEEzp87YmpXyJj";
    private static final String ENCODED_SECRET = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
    private static final String AUTHORIZATION_VALUE = SECRET_KEY_PREFIX + ENCODED_SECRET;

    @NonNull
    public PaymentResponseFromToss requestToToss(RequestToToss request, String requestURI) {
        String secretKey = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        PaymentResponseFromToss responseFromToss = webClient.post()
                .uri(requestURI)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(PaymentResponseFromToss.class)
                .block();
        return responseFromToss;
    }

    public PaymentResponseFromToss requestToToss(String requestURI) {
        String secretKey = Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes());
        PaymentResponseFromToss responseFromToss = WebClient.create().get()
                .uri(requestURI)
                .header(HttpHeaders.AUTHORIZATION, AUTHORIZATION_VALUE)
                .retrieve()
                .bodyToMono(PaymentResponseFromToss.class)
                .block();
        return responseFromToss;
    }
}
