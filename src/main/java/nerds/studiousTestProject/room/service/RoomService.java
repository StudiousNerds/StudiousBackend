package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.service.ReservationService;
import nerds.studiousTestProject.room.dto.FindRoomResponse;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final SubPhotoService subPhotoService;
    private final ConvenienceService convenienceService;
    private final ReservationService reservationService;

    public List<FindRoomResponse> getRooms(LocalDate date, Long studycafeId){
        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        List<FindRoomResponse> rooms = new ArrayList<>();

        for (Room room : roomList) {
            rooms.add(FindRoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .standCount(room.getStandCount())
                    .minCount(room.getMinCount())
                    .maxCount(room.getMaxCount())
                    .price(room.getPrice())
                    .type(room.getType())
                    .minUsingTime(room.getMinUsingTime())
                    .photos(subPhotoService.findRoomPhotos(room.getId()))
                    .canReserveDatetime(getCanReserveDatetime(date, studycafeId, room.getId()))
                    .conveniences(convenienceService.getAllRoomConveniences(studycafeId, room.getId()))
                    .build());
        }

        return rooms;
    }

    public Integer[] getCanReserveTime(LocalDate date,Long studycafeId, Long roomId){
        Map<Integer, Boolean> reservationTimes = reservationService.getReservationTimes(date, studycafeId, roomId);
        Integer timeList[] = new Integer[24];

        List<Boolean> values = reservationTimes.values().stream().toList();
        List<Integer> timeZone = reservationTimes.keySet().stream().toList();

        for (int i = 0; i < values.size(); i++) {
            if(values.get(i) == true){
                timeList[i] = timeZone.get(i);
            }
        }

        return timeList;
    }

    public Map<String, Integer[]> getCanReserveDatetime(LocalDate date,Long studycafeId, Long roomId){
        Integer oneMonth = date.lengthOfMonth();
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        Map<String, Integer[]> reservationList = new ConcurrentHashMap<>();

        for (int i = 0; i < oneMonth; i++){
            Integer[] canReserveTime = getCanReserveTime(firstDayOfMonth, studycafeId, roomId);
            reservationList.put(firstDayOfMonth.toString(), canReserveTime);
            firstDayOfMonth.plusDays(1);
        }

        return reservationList;
    }
}