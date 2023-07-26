package nerds.studiousTestProject.reservation.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.reservation.repository.ReservationRecordRepository;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRecordRepository reservationRecordRepository;
    private final RoomRepository roomRepository;
    private final StudycafeRepository studycafeRepository;
    //private final RoomService roomService;
//private final StudycafeService studycafeService;

    private Map<Integer, Boolean> reservationTimes = new ConcurrentHashMap<>();

    public Map<Integer, Boolean> getReservationTimes(LocalDate date, Long studycafeId, Long roomId){
        /* LocalTime openTime = studycafeService.getOpenTime(studycafeId);
        LocalTime endTime = studycafeService.getEndTime(studycafeId); */
        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new EntityNotFoundException("No such Studycafe"));
        LocalTime openTime = studycafe.getStartTime();
        LocalTime endTime = studycafe.getEndTime();

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException("No such Studyroom"));
        Integer minUsingTime = room.getMinUsingTime();

        for(int i = openTime.getHour(); i < endTime.getHour(); i+=minUsingTime){
            reservationTimes.put(i, true);
        }

        List<Object[]> allReservedTime = reservationRecordRepository.findAllReservedTime(date, roomId);
        for (Object[] localTimes : allReservedTime){
            for (int i = ((LocalTime) localTimes[0]).getHour(); i < ((LocalTime) localTimes[1]).getHour(); i++){
                reservationTimes.put(i, false);
            }
        }
        return reservationTimes;
    }
}
