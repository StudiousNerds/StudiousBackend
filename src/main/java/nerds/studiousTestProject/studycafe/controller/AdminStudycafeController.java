package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.studycafe.dto.modify.request.AnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.CafeInfoEditRequest;
import nerds.studiousTestProject.studycafe.dto.modify.response.AnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.CafeBasicInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.CafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.valid.request.AccountInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.request.BusinessInfoRequest;
import nerds.studiousTestProject.studycafe.dto.valid.response.ValidResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/studycafes")
@Slf4j
public class AdminStudycafeController {
    private final StudycafeService studycafeService;

    private static final int MANAGED_ENTRY_STUDYCAFE_SIZE = 4;

    @PostMapping("/accountInfos")
    public ValidResponse checkAccountInfo(@RequestBody AccountInfoRequest accountInfoRequest) {
        return studycafeService.validateAccountInfo(accountInfoRequest);
    }

    @PostMapping("/businessInfos")
    public ValidResponse checkBusinessInfo(@RequestBody BusinessInfoRequest businessInfoRequest) {
        return studycafeService.validateBusinessInfo(businessInfoRequest);
    }

    @PostMapping
    public RegisterResponse register(@LoggedInMember Long memberId,
                                     @RequestPart @Valid RegisterRequest registerRequest,
                                     MultipartHttpServletRequest request) {
        return studycafeService.register(memberId, registerRequest, request.getMultiFileMap());
    }

    @GetMapping
    public List<CafeBasicInfoResponse> findManagedEntryStudycafes(@LoggedInMember Long memberId, @RequestParam Integer page) {
        return studycafeService.enquiryManagedEntryStudycafes(memberId, PageRequestConverter.of(page, MANAGED_ENTRY_STUDYCAFE_SIZE));
    }

    @GetMapping("/{studycafeId}")
    public CafeDetailsResponse findManagedDetailStudycafe(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        return studycafeService.enquiryManagedStudycafe(memberId, studycafeId);
    }

    @PatchMapping("/{studycafeId}")
    public void modifyManagedStudycafe(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @RequestBody CafeInfoEditRequest cafeInfoEditRequest) {
        studycafeService.modify(memberId, studycafeId, cafeInfoEditRequest);
    }

    @GetMapping("/{studycafeId}/notificationInfos")
    public List<AnnouncementResponse> findNotificationInfos(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        return studycafeService.enquiryAnnouncements(memberId, studycafeId);
    }

    @PostMapping("/{studycafeId}/notificationInfos")
    public void addNotificationInfo(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @RequestBody @Valid AnnouncementRequest announcementRequest) {
        studycafeService.registerAnnouncements(memberId, studycafeId, announcementRequest);
    }

    @DeleteMapping("/{studycafeId}")
    public void delete(@LoggedInMember Long memberId, @PathVariable Long studycafeId) {
        studycafeService.delete(memberId, studycafeId);
    }
}
