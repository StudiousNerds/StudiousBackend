package nerds.studiousTestProject.room.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.room.dto.find.response.FindAllRoomResponse;
import nerds.studiousTestProject.room.service.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public FindAllRoomResponse inquireModifyRoom(@RequestParam Long studycafeId, @RequestParam Long roomId) {
        return roomService.getAllRooms(studycafeId, roomId);
    }
}
