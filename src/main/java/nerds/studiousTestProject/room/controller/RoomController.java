package nerds.studiousTestProject.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.util.LoggedInMember;
import nerds.studiousTestProject.common.util.RoleType;
import nerds.studiousTestProject.room.dto.find.response.FindAllRoomResponse;
import nerds.studiousTestProject.room.dto.modify.request.ModifyRoomRequest;
import nerds.studiousTestProject.room.dto.modify.response.ModifyRoomResponse;
import nerds.studiousTestProject.room.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Secured(value = RoleType.ADMIN)
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/studycafes/managements/{studycafeId}/rooms/{roomId}")
    public FindAllRoomResponse inquireModifyRoom(@LoggedInMember Long memberId, @PathVariable Long studycafeId, @PathVariable Long roomId) {
        return roomService.getAllRooms(memberId, studycafeId, roomId);
    }

    @PatchMapping("/studycafes/managements/{studycafeId}/rooms/{roomId}")
    public ModifyRoomResponse modifyRoom(@PathVariable Long studycafeId, @PathVariable Long roomId,
                                         @RequestPart("modifyRoomReuqest") @Valid ModifyRoomRequest modifyRoomRequest,
                                         @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return roomService.modifyRoom(studycafeId, roomId, modifyRoomRequest, files);
    }

    @DeleteMapping("/studycafes/managements/{studycafeId}/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long studycafeId, @PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}
