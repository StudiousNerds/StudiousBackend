package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.common.util.RoleType;
import nerds.studiousTestProject.studycafe.dto.manage.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.manage.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.manage.response.AnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.manage.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/studious/studycafes")
@Secured(RoleType.ADMIN)
@Slf4j
@Validated
public class AdminStudycafeController {
    private final StudycafeService studycafeService;

    @PostMapping("/validations/accountInfos")
    public ValidResponse checkAccountInfo(@RequestBody AccountInfoRequest accountInfoRequest) {
        return studycafeService.validateAccountInfo(accountInfoRequest);
    }

    @PostMapping("/validations/businessInfos")
    public ValidResponse checkBusinessInfo(@RequestBody BusinessInfoRequest businessInfoRequest) {
        log.info("business = {}", businessInfoRequest);
        return studycafeService.validateBusinessInfo(businessInfoRequest);
    }

    @PostMapping("/registrations")
    public RegisterResponse register(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody @Valid RegisterRequest registerRequest) {
        return studycafeService.register(accessToken, registerRequest);
    }

    @GetMapping("/managements")
    public List<CafeBasicInfoResponse> findManagedEntryStudycafes(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam Integer page) {
        return studycafeService.inquireManagedEntryStudycafes(accessToken, PageRequestConverter.of(page, 4));
    }

    @GetMapping("/managements/{studycafeId}")
    public CafeDetailsResponse findManagedDetailStudycafe(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId) {
        return studycafeService.inquireManagedStudycafe(accessToken, studycafeId);
    }

    @PatchMapping("/managements/{studycafeId}")
    public void editManagedStudycafe(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId, @RequestBody CafeInfoEditRequest cafeInfoEditRequest) {
        studycafeService.edit(accessToken, studycafeId, cafeInfoEditRequest);
    }

    @GetMapping("/managements/notificationInfos/{studycafeId}")
    public List<AnnouncementResponse> findNotificationInfos(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId) {
        return studycafeService.inquireAnnouncements(accessToken, studycafeId);
    }

    @PostMapping("/managements/notificationInfos/{studycafeId}")
    @Secured(RoleType.ADMIN)
    public void addNotificationInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId, @RequestBody @Valid AnnouncementRequest announcementRequest) {
        studycafeService.insertAnnouncements(accessToken, studycafeId, announcementRequest);
    }

    @DeleteMapping("/managements/{studycafeId}")
    public void delete(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @PathVariable Long studycafeId) {
        studycafeService.delete(accessToken, studycafeId);
    }
}