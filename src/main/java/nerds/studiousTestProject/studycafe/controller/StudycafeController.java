package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.search.request.SearchRequest;
import nerds.studiousTestProject.studycafe.dto.search.response.SearchResponse;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import nerds.studiousTestProject.studycafe.util.PageRequestConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Slf4j
public class StudycafeController {
    private final StudycafeService studycafeService;

    @GetMapping("/search")
    public List<SearchResponse> search(@RequestParam Integer page, @ModelAttribute @Valid SearchRequest searchRequest) {
        log.info("searchRequest = {}", searchRequest);
        return studycafeService.inquire(searchRequest, PageRequestConverter.of(page, 8));
    }

    @GetMapping("/studycafes/{cafeId}")
    public FindStudycafeResponse findStudycafeInfo(@PathVariable("cafeId") Long cafeId, @RequestBody FindStudycafeRequest findStudycafeRequest) {
        return studycafeService.findByDate(cafeId, findStudycafeRequest);
    }

    @GetMapping("/main")
    public MainPageResponse mainpage() {
        return studycafeService.getMainPage();
    }

    @PostMapping("/valid/accountInfo")
    public ValidResponse checkAccountInfo(@RequestBody AccountInfoRequest accountInfoRequest) {
        return studycafeService.validateAccountInfo(accountInfoRequest);
    }

    @PostMapping("/valid/businessInfo")
    public ValidResponse checkBusinessInfo(@RequestBody BusinessInfoRequest businessInfoRequest) {
        log.info("business = {}", businessInfoRequest);
        return studycafeService.validateBusinessInfo(businessInfoRequest);
    }

    @PostMapping("/studycafes/registrations")
    public RegisterResponse register(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody @Valid RegisterRequest registerRequest) {
        log.info("request = {}", registerRequest);
        return studycafeService.register(accessToken, registerRequest);
    }

    @GetMapping("/studycafes/managements")
    public List<CafeBasicInfoResponse> findManagedEntryStudycafes(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam Integer page) {
        return studycafeService.inquireManagedEntryStudycafes(accessToken, PageRequestConverter.of(page, 4));
    }

    @GetMapping("/studycafes/managements/{cafeId}")
    public CafeDetailsResponse findManagedDetailStudycafe(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long cafeId) {
        return studycafeService.inquireManagedStudycafe(accessToken, cafeId);
    }
}
