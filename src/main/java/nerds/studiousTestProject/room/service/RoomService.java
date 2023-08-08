package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.entity.ConvenienceName;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.dto.reserve.response.PaidConvenience;
import nerds.studiousTestProject.reservation.service.ReservationService;
import nerds.studiousTestProject.room.dto.FindRoomResponse;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import nerds.studiousTestProject.studycafe.repository.OperationInfoRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_END_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_START_TIME;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_FOUND_STUDYCAFE;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class RoomService {
    private final RoomRepository roomRepository;
    private final SubPhotoService subPhotoService;
    private final ReservationService reservationService;
    private final StudycafeRepository studycafeRepository;
    private final OperationInfoRepository operationInfoRepository;

    public List<FindRoomResponse> getRooms(LocalDate date, Long studycafeId){

        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        List<FindRoomResponse> rooms = new ArrayList<>();

        for (Room room : roomList) {
            rooms.add(FindRoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .standCount(room.getStandardHeadCount())
                    .minCount(room.getMinHeadCount())
                    .maxCount(room.getMaxHeadCount())
                    .price(room.getPrice())
                    .type(room.getType().toString())
                    .minUsingTime(room.getMinUsingTime())
                    .photos(subPhotoService.findRoomPhotos(room.getId()))
                    .canReserveDatetime(getCanReserveDatetime(date, studycafeId, room.getId()))
                    .conveniences(getConveniences(room.getId()))
                    .paidConveniences(getPaidConveniences(room.getId()))
                    .build());
        }

        return rooms;
    }

    public Integer[] getCanReserveTime(LocalDate date,Long studycafeId, Long roomId){

        Map<Integer, Boolean> reservationTimes = reservationService.getReservationTimes(date, studycafeId, roomId);
        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
        int start = findStartTimeByWeek(Week.of(date)).getHour();
        int end = findEndTimeByWeek(Week.of(date)).getHour();
        int size = end - start;
        Integer timeList[] = new Integer[size+1];

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

        for (int i = 0; i <= oneMonth; i++){
            log.info("반복문 확인", i);
            Integer[] canReserveTime = getCanReserveTime(date, studycafeId, roomId);
            reservationList.put(date.toString(), canReserveTime);
            date.plusDays(i);
        }

        return reservationList;
    }

    public String[] getConveniences(Long roomId) {
        Room room = findRoomById(roomId);

        List<ConvenienceName> convenienceList = room.getConveniences().stream().map(Convenience::getName).toList();
        Integer arrSize = convenienceList.size();
        String conveniences[] = convenienceList.toArray(new String[arrSize]);

        return conveniences;
    }

    public List<PaidConvenience> getPaidConveniences(Long roomId) {
        Room room = findRoomById(roomId);
        List<Convenience> conveniences = room.getConveniences();

        return conveniences.stream()
                .filter(convenience -> !convenience.isFree())
                .map(PaidConvenience::from)
                .toList();
    }

    public Room findRoomById(Long roomId){
        return roomRepository.findById(roomId)
                .orElseThrow(()->new NotFoundException(NOT_FOUND_ROOM));
    }

    public LocalTime findStartTimeByWeek(Week week) {
        return operationInfoRepository.findStartTime(week)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_START_TIME));
    }

    public LocalTime findEndTimeByWeek(Week week) {
        return operationInfoRepository.findEndTime(week)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_END_TIME));
    }
}