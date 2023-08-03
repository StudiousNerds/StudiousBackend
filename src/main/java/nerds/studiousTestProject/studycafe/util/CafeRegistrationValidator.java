package nerds.studiousTestProject.studycafe.util;

import io.netty.handler.codec.http.HttpScheme;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CafeRegistrationValidator {
    private final WebClient webClient;

    /**
     * 사용자가 입력한 계좌 정보가 유효한지 검증하는 메소드
     * @param accountInfoRequest 사용자가 입력한
     * @return
     */
    public ValidResponse getAccountInfoValidResponse(AccountInfoRequest accountInfoRequest) {
        MultiValueMap<String, String> params = MultiValueMapConverter.convert(
                OpenBankTokenRequest.builder()
                        .client_id("7338b43b-eb71-40e9-9414-749b772e4907")
                        .client_secret("3cfae1dc-b541-45a9-8528-a5707aaf36c4")
                        .scope("oob")
                        .grant_type("client_credentials")
                        .build()
        );

        accountInfoRequest.setAccount_holder_info_type("");
        OpenBankTokenResponse openBankTokenResponse = webClient.post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host("testapi.openbanking.or.kr")
                                .path("/oauth/2.0/token")
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

        accountInfoRequest.setTran_dtime(
                getNowTimeString()
        );
        AccountInfoResponse accountInfoResponse = webClient.post()
                .uri(
                        UriComponentsBuilder.newInstance()
                                .scheme(HttpScheme.HTTPS.toString())
                                .host("testapi.openbanking.or.kr")
                                .path("/v2.0/inquiry/real_name")
                                .queryParam("bank_tran_id", "M202301745U123456789")
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
                .body(BodyInserters.fromValue(accountInfoRequest))
                .retrieve()
                .bodyToMono(AccountInfoResponse.class)
                .block();

        log.info("response = {}", accountInfoResponse);

        return ValidResponse.builder()
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
     * @param businessInfoRequest 사업자 등록 번호
     * @return 사업자 등록 번호 유효성
     */
    public ValidResponse getBusinessInfoValidResponse(BusinessInfoRequest businessInfoRequest) {
        BusinessInfoResponse businessInfoResponse = webClient
                .post()
                .uri(
                        UriComponentsBuilder.fromUriString("https://api.odcloud.kr")
                                .path("/api/nts-businessman/v1/status")
                                .queryParam("serviceKey", "FgCjyyTMbN42kaatvYSk8LsS1%2Bu6BY%2BW%2BrZWqjr7TkgZD%2BNt7HGM5WqjLkG60PdNroVSEQTK9JcwunkeQ%2F%2F3kQ%3D%3D")
                                .build()
                                .toUriString()
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(businessInfoRequest)
                .retrieve()
                .bodyToMono(BusinessInfoResponse.class)
                .block();

        return ValidResponse.builder()
                .available(businessInfoResponse.getMatch_cnt() != null)
                .build();
    }

    @Data
    static class OpenBankTokenRequest {
        private String client_id;
        private String client_secret;
        private String scope;
        private String grant_type;

        @Builder
        public OpenBankTokenRequest(String client_id, String client_secret, String scope, String grant_type) {
            this.client_id = client_id;
            this.client_secret = client_secret;
            this.scope = scope;
            this.grant_type = grant_type;
        }
    }

    @Data
    static class OpenBankTokenResponse {
        private String access_token;
        private String token_type;
        private String expires_in;
        private String oop;
        private String client_use_code;
    }

    @Data
    static class AccountInfoResponse {
        private String api_tran_id;
        private String api_tran_dtm;
        private String rsp_code;
        private String rsp_message;
        private String bank_tran_id;
        private String bank_tran_date;
        private String bank_code_tran;
        private String bank_rsp_code;
        private String bank_rsp_message;
        private String bank_code_std;
        private String bank_code_sub;
        private String bank_name;
        private String account_num;
        private String account_holder_info_type;
        private String account_holder_info;
        private String account_holder_name;
        private String account_type;
    }

    @Data
    static class BusinessInfoResponse {
        private String status_code;     // 상태 코드
        private Integer match_cnt;      // 조회 결과 개수
        private Integer request_cnt;    // 조회 요청 개수
        private List<Data> data;        // 결과

        @Getter
        @Setter
        static class Data {
            private String b_no;
            private String b_stt;
            private String b_stt_cd;
            private String tax_type;
            private String tax_type_cd;
            private String end_at;
            private String utcc_yn;
            private String tax_type_change_at;
            private String rbf_tax_type;
            private String rbf_tax_type_cd;
        }
    }
}
