package nerds.studiousTestProject.room.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.room.dto.find.response.FindAllRoomResponse;
import nerds.studiousTestProject.room.dto.modify.request.ModifyRoomRequest;
import nerds.studiousTestProject.room.dto.modify.response.ModifyRoomResponse;
import nerds.studiousTestProject.room.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/studycafes/managements/{studycafeId}/rooms/{roomId}")
    public FindAllRoomResponse inquireModifyRoom(@RequestParam Long studycafeId, @RequestParam Long roomId) {
        return roomService.getAllRooms(studycafeId, roomId);
    }

    @PatchMapping("/studycafes/managements/{studycafeId}/rooms/{roomId}")
    public ModifyRoomResponse modifyRoom(@RequestParam Long studycafeId, @RequestParam Long roomId,
                                         @RequestPart("modifyRoomReuqest") @Valid ModifyRoomRequest modifyRoomRequest,
                                         @RequestPart(value = "file", required = false) List<MultipartFile> files) {
        return roomService.modifyRoom(studycafeId, roomId, modifyRoomRequest, files);
    }
}
