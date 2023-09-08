package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotAuthorizedException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageService;
import nerds.studiousTestProject.common.service.TokenService;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhotoType;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.reservation.dto.show.response.PaidConvenience;
import nerds.studiousTestProject.reservation.service.ReservationRecordService;
import nerds.studiousTestProject.room.dto.find.response.BasicRoomInfo;
import nerds.studiousTestProject.room.dto.find.response.FindAllRoomResponse;
import nerds.studiousTestProject.room.dto.find.response.FindRoomResponse;
import nerds.studiousTestProject.room.dto.modify.request.ModifyConvenienceRequest;
import nerds.studiousTestProject.room.dto.modify.request.ModifyRoomRequest;
import nerds.studiousTestProject.room.dto.modify.response.ModifyRoomResponse;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.entity.Week;
import nerds.studiousTestProject.studycafe.repository.OperationInfoRepository;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static nerds.studiousTestProject.common.exception.ErrorCode.MISMATCH_MEMBER_AND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.ErrorCode.NOT_AUTHORIZE_ACCESS;
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
    private final ReservationRecordService reservationRecordService;
    private final StudycafeRepository studycafeRepository;
    private final OperationInfoRepository operationInfoRepository;
    private final StorageService storageService;
    private final SubPhotoService subPhotoService;
    private final ConvenienceService convenienceService;
    private final TokenService tokenService;

    public List<FindRoomResponse> getRooms(LocalDate date, Long studycafeId) {

        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);

        return roomList.stream()
                .map(room -> FindRoomResponse.builder()
                        .id(room.getId())
                        .roomName(room.getName())
                        .minHeadCount(room.getMinHeadCount())
                        .maxHeadCount(room.getMaxHeadCount())
                        .price(room.getPrice())
                        .priceType(room.getPriceType().name())
                        .minUsingTime(room.getMinUsingTime())
                        .photos(getPhotos(room))
                        .canReserveDatetime(getCanReserveDatetime(date, studycafeId, room.getId()))
                        .conveniences(getConveniences(room.getId()))
                        .paidConveniences(getPaidConveniences(room.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    public FindAllRoomResponse getAllRooms(String accessToken, Long studycafeId, Long roomId) {
        Member memberFromAccessToken = tokenService.getMemberFromAccessToken(accessToken);

        if (!matchStudycafeAndMember(studycafeId, memberFromAccessToken)) {
            throw new NotFoundException(MISMATCH_MEMBER_AND_STUDYCAFE);
        }

        List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        Room room = findRoomById(roomId);

        return FindAllRoomResponse.builder()
                .roomInfos(getBasicInfo(roomList))
                .roomName(room.getName())
                .minCount(room.getMinHeadCount())
                .maxCount(room.getMaxHeadCount())
                .price(room.getPrice())
                .type(room.getPriceType().name())
                .minUsingTime(room.getMinUsingTime())
                .photos(getPhotos(room))
                .conveniences(getConveniences(room.getId()))
                .paidConveniences(getPaidConveniences(room.getId()))
                .build();
    }

    @Transactional
    public ModifyRoomResponse modifyRoom(Long studycafeId, Long roomId, ModifyRoomRequest modifyRoomRequest, List<MultipartFile> photos) {
        Studycafe studycafe = getStudycafeById(studycafeId);
        Room room = findRoomById(roomId);
        Room updatedRoom = ModifyRoomRequest.toRoom(studycafe, roomId, modifyRoomRequest);

        room.update(updatedRoom);
        updateRoomPhotos(room, photos);
        updateConveniences(room, modifyRoomRequest.getConveniences());

        return ModifyRoomResponse.builder().roomId(roomId).modifiedAt(LocalDate.now()).build();
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room room = findRoomById(roomId);
        deletePhotos(room);
        deleteConveniences(room);
        roomRepository.deleteById(roomId);
    }

    public Integer[] getCanReserveTime(LocalDate date, Long studycafeId, Long roomId) {
        getStudycafeById(studycafeId);
        Map<Integer, Boolean> reservationTimes = reservationRecordService.getReservationTimes(date, studycafeId, roomId);

        int start = findStartTimeByWeek(Week.of(date)).getHour();
        int end = findEndTimeByWeek(Week.of(date)).getHour();
        int size = end - start;
        Integer timeList[] = new Integer[size + 1];

        List<Boolean> values = reservationTimes.values().stream().toList();
        List<Integer> timeZone = reservationTimes.keySet().stream().toList();

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == true) {
                timeList[i] = timeZone.get(i);
            }
        }

        return timeList;
    }

    public Map<String, Integer[]> getCanReserveDatetime(LocalDate date, Long studycafeId, Long roomId) {
        Integer oneMonth = date.lengthOfMonth();
        int today = date.getDayOfMonth();
        Map<String, Integer[]> reservationList = new ConcurrentHashMap<>();

        for (int i = today; i <= oneMonth; i++) {
            log.info("반복문 확인", i);
            Integer[] canReserveTime = getCanReserveTime(date, studycafeId, roomId);
            reservationList.put(date.toString(), canReserveTime);
            date.plusDays(1);
        }

        return reservationList;
    }

    public List<String> getConveniences(Long roomId) {
        Room room = findRoomById(roomId);

        return room.getConveniences().stream()
                .map(Convenience -> Convenience.getName().name())
                .toList();
    }

    public List<PaidConvenience> getPaidConveniences(Long roomId) {
        Room room = findRoomById(roomId);
        List<Convenience> conveniences = room.getConveniences();

        return conveniences.stream()
                .filter(convenience -> !convenience.isFree())
                .map(PaidConvenience::from)
                .toList();
    }

    public Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
    }

    public LocalTime findStartTimeByWeek(Week week) {
        return operationInfoRepository.findStartTime(week)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_START_TIME));
    }

    public LocalTime findEndTimeByWeek(Week week) {
        return operationInfoRepository.findEndTime(week)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_END_TIME));
    }

    private List<String> getPhotos(Room room) {
        return room.getSubPhotos().stream().map(SubPhoto::getPath).collect(Collectors.toList());
    }

    private Studycafe getStudycafeById(Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private void updateRoomPhotos(Room room, List<MultipartFile> photos) {
        if (photos != null) {
            deletePhotos(room);
            for (MultipartFile file : photos) {
                String photoUrl = storageService.uploadFile(file);
                room.addSubPhoto(SubPhoto.builder().room(room).type(SubPhotoType.ROOM).path(photoUrl).build());
            }
        }
    }

    private void updateConveniences(Room room, List<ModifyConvenienceRequest> conveniences) {
        if (conveniences != null) {
            deleteConveniences(room);
            for (ModifyConvenienceRequest convenienceInfo : conveniences) {
                room.addConvenience(convenienceInfo.toConvenience());
            }
        }
    }

    private void deletePhotos(Room room) {
        for (SubPhoto photo : room.getSubPhotos()) {
            storageService.deleteFile(photo.getPath());
            room.getSubPhotos().remove(photo.getPath());
        }
        subPhotoService.removeAllRoomPhotos(room.getId());
    }

    private void deleteConveniences(Room room) {
        room.deleteConveniences();
        convenienceService.deleteRoomConveniences(room.getId());
    }

    private List<BasicRoomInfo> getBasicInfo(List<Room> roomList) {
        return roomList.stream()
                .map(room -> BasicRoomInfo.of(room))
                .collect(Collectors.toList());
    }

    private boolean matchStudycafeAndMember(Long studycafeId, Member member) {
        return studycafeRepository.existsByIdAndMember(studycafeId, member);
    }
}