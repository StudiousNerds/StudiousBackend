package nerds.studiousTestProject.studycafe.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.PageRequestConverter;
import nerds.studiousTestProject.room.dto.show.ShowRoomDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowManagedStudycafeDetailsResponse;
import nerds.studiousTestProject.room.dto.show.ShowRoomBasicResponse;
import nerds.studiousTestProject.room.dto.modify.ModifyRoomRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.ModifyAnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.ModifyStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyAnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowManagedStudycafeBasicResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateAccountRequest;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateBusinessmanRequest;
import nerds.studiousTestProject.studycafe.dto.validate.response.ValidateResponse;
import nerds.studiousTestProject.studycafe.service.AdminStudycafeService;
import org.springframework.http.ResponseEntity;
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
    private final AdminStudycafeService adminStudycafeService;

    private static final String BASIC_STUDYCAFES_SIZE = "4";

    @PostMapping("/accounts")
    public ValidateResponse checkAccountInfo(@RequestBody final ValidateAccountRequest validateAccountRequest) {
        return adminStudycafeService.validateAccount(validateAccountRequest);
    }

    @PostMapping("/businessmen")
    public ValidateResponse checkBusinessInfo(@RequestBody final ValidateBusinessmanRequest validateBusinessmanRequest) {
        return adminStudycafeService.validateBusinessman(validateBusinessmanRequest);
    }

    @PostMapping
    public RegisterResponse register(@LoggedInMember final Long memberId,
                                     @RequestPart @Valid final RegisterRequest registerRequest,
                                     final MultipartHttpServletRequest request) {
        return adminStudycafeService.register(memberId, registerRequest, request.getMultiFileMap());
    }

    @GetMapping
    public List<ShowManagedStudycafeBasicResponse> findManagedEntryStudycafes(@LoggedInMember final Long memberId,
                                                                              @RequestParam(required = false) final Integer page,
                                                                              @RequestParam(defaultValue = BASIC_STUDYCAFES_SIZE) final Integer size) {
        return adminStudycafeService.enquireStudycafes(memberId, PageRequestConverter.of(page, size));
    }

    @GetMapping("/{studycafeId}")
    public ShowManagedStudycafeDetailsResponse findManagedDetailStudycafe(@LoggedInMember final Long memberId, @PathVariable final Long studycafeId) {
        return adminStudycafeService.enquireStudycafe(memberId, studycafeId);
    }

    @PatchMapping("/{studycafeId}")
    public void modifyManagedStudycafe(@LoggedInMember final Long memberId,
                                       @PathVariable final Long studycafeId,
                                       @RequestBody final ModifyStudycafeRequest modifyStudycafeRequest) {
        adminStudycafeService.modify(memberId, studycafeId, modifyStudycafeRequest);
    }

    @GetMapping("/{studycafeId}/notifications")
    public List<ModifyAnnouncementResponse> findNotificationInfos(@LoggedInMember final Long memberId, @PathVariable final Long studycafeId) {
        return adminStudycafeService.enquiryAnnouncements(memberId, studycafeId);
    }

    @PostMapping("/{studycafeId}/notifications")
    public void addNotificationInfo(@LoggedInMember final Long memberId,
                                    @PathVariable final Long studycafeId,
                                    @RequestBody @Valid final ModifyAnnouncementRequest modifyAnnouncementRequest) {
        adminStudycafeService.registerAnnouncements(memberId, studycafeId, modifyAnnouncementRequest);
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
