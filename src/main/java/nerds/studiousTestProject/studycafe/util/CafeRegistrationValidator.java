package nerds.studiousTestProject.studycafe.util;

import io.netty.handler.codec.http.HttpScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.MultiValueMapConverter;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateAccountRequest;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateBusinessmanRequest;
import nerds.studiousTestProject.studycafe.dto.validate.response.ValidateResponse;
import nerds.studiousTestProject.studycafe.util.odcloud.response.BusinessInfoResponse;
import nerds.studiousTestProject.studycafe.util.openbank.request.OpenBankTokenRequest;
import nerds.studiousTestProject.studycafe.util.openbank.response.AccountInfoResponse;
import nerds.studiousTestProject.studycafe.util.openbank.response.OpenBankTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class CafeRegistrationValidator {
    private static final String OPEN_BANK_CLIENT_ID = "7338b43b-eb71-40e9-9414-749b772e4907";
    private static final String OPEN_BANK_CLIENT_SECRET = "3cfae1dc-b541-45a9-8528-a5707aaf36c4";
    private static final String OPEN_BANK_SCOPE = "oob";
    private static final String OPEN_BANK_GRANT_TYPE = "client_credentials";
    private static final String OPEN_BANK_HOST = "testapi.openbanking.or.kr";
    private static final String OPEN_BANK_TOKEN_PATH = "/oauth/2.0/token";
    private static final String OPEN_BANK_ACCOUNT_INQUIRE_PATH = "/v2.0/inquiry/real_name";
    private static final String OPEN_BANK_TRAN_ID_PREFIX = "bank_tran_id";
    private static final String OPEN_BANK_TRAN_ID = "M202301745" + "U" + "123456789";
    private static final String OD_CLOUD_HOST = "api.odcloud.kr";
    private static final String OD_CLOUD_BUSINESS_STATUS_PATH = "/api/nts-businessman/v1/status";
    private static final String OD_CLOUD_SERVICE_KEY_PREFIX = "serviceKey";
    private static final String OD_CLOUD_SERVICE_KEY = "FgCjyyTMbN42kaatvYSk8LsS1%2Bu6BY%2BW%2BrZWqjr7TkgZD%2BNt7HGM5WqjLkG60PdNroVSEQTK9JcwunkeQ%2F%2F3kQ%3D%3D";

    private final WebClient webClient;

    /**
     * 사용자가 입력한 계좌 정보가 유효한지 검증하는 메소드
     * @param validateAccountRequest 사용자가 입력한
     * @return
     */
    public ValidateResponse getAccountInfoValidResponse(ValidateAccountRequest validateAccountRequest) {
        MultiValueMap<String, String> params = MultiValueMapConverter.convert(
                OpenBankTokenRequest.builder()
                        .client_id(OPEN_BANK_CLIENT_ID)
                        .client_secret(OPEN_BANK_CLIENT_SECRET)
                        .scope(OPEN_BANK_SCOPE)
                        .grant_type(OPEN_BANK_GRANT_TYPE)
                        .build()
        );

        validateAccountRequest.setAccount_holder_info_type("");
        OpenBankTokenResponse openBankTokenResponse = webClient.post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host(OPEN_BANK_HOST)
                                .path(OPEN_BANK_TOKEN_PATH)
                                .encode()
                                .build()
                                .toUri()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(params))
                .retrieve()
                .bodyToMono(OpenBankTokenResponse.class)
                .block();

        log.info("openBankTokenResponse = {}", openBankTokenResponse);

        validateAccountRequest.setTran_dtime(
                getNowTimeString()
        );
        AccountInfoResponse accountInfoResponse = webClient.post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host(OPEN_BANK_HOST)
                                .path(OPEN_BANK_ACCOUNT_INQUIRE_PATH)
                                .queryParam(OPEN_BANK_TRAN_ID_PREFIX, OPEN_BANK_TRAN_ID)
                                .encode()
                                .build()
                                .toUri()
                )
                .headers(
                        httpHeaders -> {
                            httpHeaders.setBearerAuth(openBankTokenResponse.getAccess_token());
                            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                        }
                )
                .body(BodyInserters.fromValue(validateAccountRequest))
                .retrieve()
                .bodyToMono(AccountInfoResponse.class)
                .block();

        log.info("response = {}", accountInfoResponse);

        return ValidateResponse.builder()
                .available(true)
                .build();
    }

    private String getNowTimeString() {
        return LocalDateTime.now().toString()
                .replaceAll("-", "")
                .replaceAll("T", "")
                .replaceAll(":", "")
                .replaceAll("\\.", "")
                .substring(0, 14);
    }

    /**
     * 사용자가 입력한 사업자 번호가 유효한지 검증하는 메소드
     * @param validateBusinessmanRequest 사업자 등록 번호
     * @return 사업자 등록 번호 유효성
     */
    public ValidateResponse getBusinessInfoValidResponse(ValidateBusinessmanRequest validateBusinessmanRequest) {
        BusinessInfoResponse businessInfoResponse = webClient
                .post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host(OD_CLOUD_HOST)
                                .path(OD_CLOUD_BUSINESS_STATUS_PATH)
                                .queryParam(OD_CLOUD_SERVICE_KEY_PREFIX, OD_CLOUD_SERVICE_KEY)
                                .build()
                                .toUriString()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(validateBusinessmanRequest)
                .retrieve()
                .bodyToMono(BusinessInfoResponse.class)
                .block();

        return ValidateResponse.builder()
                .available(businessInfoResponse.getMatch_cnt() != null)
                .build();
    }
}
