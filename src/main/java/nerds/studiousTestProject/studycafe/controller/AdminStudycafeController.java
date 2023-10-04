package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studycafes/managements")
@Slf4j
@Validated
public class AdminStudycafeController {
    private final StudycafeService studycafeService;

    private static final int MANAGED_ENTRY_STUDYCAFE_SIZE = 4;

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
    public RegisterResponse register(@LoggedInMember Long memberId, @RequestBody @Valid RegisterRequest registerRequest) {
        return studycafeService.register(memberId, registerRequest);
    }

    @GetMapping
    public List<CafeBasicInfoResponse> findManagedEntryStudycafes(@LoggedInMember Long memberId, @RequestParam Integer page) {
        return studycafeService.inquireManagedEntryStudycafes(memberId, PageRequestConverter.of(page, 4));
    }

    @GetMapping("/{studycafeId}")
    public CafeDetailsResponse findManagedDetailStudycafe(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        return studycafeService.inquireManagedStudycafe(memberId, studycafeId);
    }

    @PatchMapping("/{studycafeId}")
    public void editManagedStudycafe(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @RequestBody CafeInfoEditRequest cafeInfoEditRequest) {
        studycafeService.edit(memberId, studycafeId, cafeInfoEditRequest);
    }

    @GetMapping("/notificationInfos/{studycafeId}")
    public List<AnnouncementResponse> findNotificationInfos(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        return studycafeService.inquireAnnouncements(memberId, studycafeId);
    }

    @PostMapping("/notificationInfos/{studycafeId}")
    public void addNotificationInfo(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @RequestBody @Valid AnnouncementRequest announcementRequest) {
        studycafeService.insertAnnouncements(memberId, studycafeId, announcementRequest);
    }

    @DeleteMapping("/{studycafeId}")
    public void delete(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        studycafeService.delete(memberId, studycafeId);
    }
}
