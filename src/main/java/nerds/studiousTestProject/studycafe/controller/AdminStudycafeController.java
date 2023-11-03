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
import org.springframework.web.multipart.MultipartFile;
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
    public ValidResponse checkAccountInfo(@RequestBody final AccountInfoRequest accountInfoRequest) {
        return studycafeService.validateAccountInfo(accountInfoRequest);
    }

    @PostMapping("/businessInfos")
    public ValidResponse checkBusinessInfo(@RequestBody final BusinessInfoRequest businessInfoRequest) {
        return studycafeService.validateBusinessInfo(businessInfoRequest);
    }

    @PostMapping
    public RegisterResponse register(@LoggedInMember final Long memberId,
                                     @RequestPart @Valid final RegisterRequest registerRequest,
                                     final MultipartHttpServletRequest request) {
        return studycafeService.register(memberId, registerRequest, request.getMultiFileMap());
    }

    @GetMapping
    public List<CafeBasicInfoResponse> findManagedEntryStudycafes(@LoggedInMember final Long memberId, @RequestParam final Integer page) {
        return studycafeService.enquiryManagedEntryStudycafes(memberId, PageRequestConverter.of(page, MANAGED_ENTRY_STUDYCAFE_SIZE));
    }

    @GetMapping("/{studycafeId}")
    public CafeDetailsResponse findManagedDetailStudycafe(@LoggedInMember final Long memberId, @PathVariable final Long studycafeId) {
        return studycafeService.enquiryManagedStudycafe(memberId, studycafeId);
    }

    @PatchMapping("/{studycafeId}")
    public void modifyManagedStudycafe(@LoggedInMember final Long memberId,
                                       @PathVariable final Long studycafeId,
                                       @RequestBody final CafeInfoEditRequest cafeInfoEditRequest) {
        studycafeService.modify(memberId, studycafeId, cafeInfoEditRequest);
    }

    @GetMapping("/{studycafeId}/notificationInfos")
    public List<AnnouncementResponse> findNotificationInfos(@LoggedInMember final Long memberId, @PathVariable final Long studycafeId) {
        return studycafeService.enquiryAnnouncements(memberId, studycafeId);
    }

    @PostMapping("/{studycafeId}/notificationInfos")
    public void addNotificationInfo(@LoggedInMember final Long memberId,
                                    @PathVariable final Long studycafeId,
                                    @RequestBody @Valid final AnnouncementRequest announcementRequest) {
        studycafeService.registerAnnouncements(memberId, studycafeId, announcementRequest);
    }

    @DeleteMapping("/{studycafeId}")
    public void delete(@LoggedInMember final Long memberId,
                       @PathVariable final Long studycafeId) {
        adminStudycafeService.delete(memberId, studycafeId);
    }

    @GetMapping("/{studycafeId}/rooms")
    public List<ShowRoomBasicResponse> inquireRooms(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @PathVariable Long roomId) {
        return adminStudycafeService.enquireRooms(memberId, studycafeId);
    }

    @GetMapping("/{studycafeId}/rooms/{roomId}")
    public ShowRoomDetailsResponse inquireRoom(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @PathVariable Long roomId) {
        return adminStudycafeService.enquireRoom(memberId, studycafeId, roomId);
    }

    @PatchMapping("/{studycafeId}/rooms/{roomId}")
    public ResponseEntity<Void> modifyRoom(@PathVariable Long studycafeId, @PathVariable Long roomId,
                                         @RequestPart("modifyRoomReuqest") @Valid ModifyRoomRequest modifyRoomRequest,
                                         @RequestPart(value = "file", required = false) List<MultipartFile> roomPhotos) {
        adminStudycafeService.modifyRoom(roomId, modifyRoomRequest, roomPhotos);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studycafeId}/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long studycafeId, @PathVariable Long roomId) {
        adminStudycafeService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
