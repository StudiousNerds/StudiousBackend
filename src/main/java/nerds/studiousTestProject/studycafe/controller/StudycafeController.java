package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.enquiry.request.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.enquiry.response.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.manage.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.AnnouncementResponse;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
@Validated
public class StudycafeController {
    private final StudycafeService studycafeService;

    @GetMapping("/search")
    public List<SearchResponse> search(@RequestParam Integer page, @ModelAttribute @Valid SearchRequest searchRequest) {
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

    @PostMapping("/validations/accountInfos")
    public ValidResponse checkAccountInfo(@RequestBody AccountInfoRequest accountInfoRequest) {
        return studycafeService.validateAccountInfo(accountInfoRequest);
    }

    @PostMapping("/validations/businessInfos")
    public ValidResponse checkBusinessInfo(@RequestBody BusinessInfoRequest businessInfoRequest) {
        log.info("business = {}", businessInfoRequest);
        return studycafeService.validateBusinessInfo(businessInfoRequest);
    }

    @PostMapping("/studycafes/registrations")
    public RegisterResponse register(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody @Valid RegisterRequest registerRequest) {
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

    @PatchMapping("/studycafes/managements/{cafeId}")
    public void editManagedStudycafe(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long cafeId, @RequestBody CafeInfoEditRequest cafeInfoEditRequest) {
        studycafeService.edit(accessToken, cafeId, cafeInfoEditRequest);
    }

    @GetMapping("/studycafes/managements/notificationInfos/{cafeId}")
    public List<AnnouncementResponse> findNotificationInfos(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long cafeId) {
        return studycafeService.inquireNotificationInfos(accessToken, cafeId);
    }

    @PostMapping("/studycafes/managements/notificationInfos/{cafeId}")
    public void addNotificationInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long cafeId, @RequestBody @Valid AnnouncementRequest announcementRequest) {
        studycafeService.insertNotificationInfos(accessToken, cafeId, announcementRequest);
    }

    @DeleteMapping("/studycafes/managements/{cafeId}")
    public void delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long cafeId) {
        studycafeService.deleteStudycafe(accessToken, cafeId);
    }
}
