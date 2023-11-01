package nerds.studiousTestProject.room.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.HolidayProvider;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.convenience.entity.Convenience;
import nerds.studiousTestProject.convenience.repository.ConvenienceRepository;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhotoType;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
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
import nerds.studiousTestProject.studycafe.entity.OperationInfo;
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

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.MISMATCH_MEMBER_AND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_OPERATION_INFO;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class RoomService {
    private final ConvenienceRepository convenienceRepository;
    private final MemberRepository memberRepository;
    private final OperationInfoRepository operationInfoRepository;
    private final RoomRepository roomRepository;
    private final ReservationRecordService reservationRecordService;
    private final StorageProvider storageProvider;
    private final StudycafeRepository studycafeRepository;
    private final SubPhotoRepository subPhotoRepository;
    private final HolidayProvider holidayProvider;

    public List<FindRoomResponse> getRooms(final LocalDate date, final Long studycafeId) {

        final List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);

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

    public FindAllRoomResponse getAllRooms(final Long memberId, final Long studycafeId, final Long roomId) {
        final Member member = findMemberById(memberId);

        if (!matchStudycafeAndMember(studycafeId, member)) {
            throw new NotFoundException(MISMATCH_MEMBER_AND_STUDYCAFE);
        }

        final List<Room> roomList = roomRepository.findAllByStudycafeId(studycafeId);
        final Room room = findRoomById(roomId);

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
    public ModifyRoomResponse modifyRoom(final Long studycafeId, final Long roomId, final ModifyRoomRequest modifyRequest, final List<MultipartFile> photos) {
        final Studycafe studycafe = getStudycafeById(studycafeId);
        final Room room = findRoomById(roomId);
        final Room updatedRoom = ModifyRoomRequest.toRoom(studycafe, roomId, modifyRequest);

        room.update(updatedRoom);
        updateRoomPhotos(room, photos);
        updateConveniences(room, modifyRequest.getConveniences());

        return ModifyRoomResponse.builder().roomId(roomId).modifiedAt(LocalDate.now()).build();
    }

    @Transactional
    public void deleteRoom(final Long roomId) {
        final Room room = findRoomById(roomId);
        deletePhotos(room);
        deleteConveniences(room);
        roomRepository.deleteById(roomId);
    }

    private Integer[] getCanReserveTime(final LocalDate date, final Long studycafeId, final Long roomId) {
        getStudycafeById(studycafeId);
        final Map<Integer, Boolean> reservationTimes = reservationRecordService.getReservationTimes(date, studycafeId, roomId);

        final boolean isHoliday = holidayProvider.getHolidays().contains(date);
        final Week week = date != null ? (isHoliday ? Week.HOLIDAY : Week.of(date)) : null;
        final OperationInfo operationInfo = findOperationInfoByStudycafeIdAndWeek(studycafeId, week);
        final int start = operationInfo.getStartTime() != null ? operationInfo.getStartTime().getHour() : LocalTime.MIN.getHour();
        final int end = operationInfo.getEndTime() != null ? operationInfo.getEndTime().getHour() : LocalTime.MAX.getHour();
        final int size = end - start;
        final Integer timeList[] = new Integer[size + 1];

        final List<Boolean> values = reservationTimes.values().stream().toList();
        final List<Integer> timeZone = reservationTimes.keySet().stream().toList();

        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == true) {
                timeList[i] = timeZone.get(i);
            }
        }

        return timeList;
    }

    private Map<String, Integer[]> getCanReserveDatetime(LocalDate date, final Long studycafeId, final Long roomId) {
        final Integer oneMonth = date.lengthOfMonth();
        final int today = date.getDayOfMonth();
        final Map<String, Integer[]> reservationList = new ConcurrentHashMap<>();

        for (int i = today; i <= oneMonth; i++) {
            Integer[] canReserveTime = getCanReserveTime(date, studycafeId, roomId);
            reservationList.put(date.toString(), canReserveTime);
            date = date.plusDays(1);
        }

        return reservationList;
    }

    private List<String> getConveniences(final Long roomId) {
        final Room room = findRoomById(roomId);

        return room.getConveniences().stream()
                .map(Convenience -> Convenience.getName().name())
                .toList();
    }

    private List<PaidConvenience> getPaidConveniences(final Long roomId) {
        final Room room = findRoomById(roomId);
        final List<Convenience> conveniences = room.getConveniences();

        return conveniences.stream()
                .filter(convenience -> !convenience.isFree())
                .map(PaidConvenience::from)
                .toList();
    }

    private Room findRoomById(final Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ROOM));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_USER));
    }

    private OperationInfo findOperationInfoByStudycafeIdAndWeek(final Long studycafeId, final Week week) {
        return operationInfoRepository.findByStudycafeAndWeek(studycafeId, week).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_OPERATION_INFO)
        );
    }

    private List<String> getPhotos(final Room room) {
        return room.getSubPhotos().stream().map(SubPhoto::getPath).collect(Collectors.toList());
    }

    private Studycafe getStudycafeById(final Long studycafeId) {
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private void updateRoomPhotos(final Room room, final List<MultipartFile> photos) {
        if (photos != null) {
            deletePhotos(room);
            for (MultipartFile file : photos) {
                String photoUrl = storageProvider.uploadFile(file);
                room.addSubPhoto(SubPhoto.builder().room(room).type(SubPhotoType.ROOM).path(photoUrl).build());
            }
        }
    }

    private void updateConveniences(final Room room, final List<ModifyConvenienceRequest> conveniences) {
        if (conveniences != null) {
            deleteConveniences(room);
            for (ModifyConvenienceRequest convenienceInfo : conveniences) {
                room.addConvenience(convenienceInfo.toConvenience());
            }
        }
    }

    private void deletePhotos(final Room room) {
        for (SubPhoto photo : room.getSubPhotos()) {
            storageProvider.deleteFile(photo.getPath());
            room.getSubPhotos().remove(photo.getPath());
        }
        removeAllRoomPhotos(room.getId());
    }

    private void deleteConveniences(final Room room) {
        room.deleteConveniences();
        deleteRoomConveniences(room.getId());
    }

    private List<BasicRoomInfo> getBasicInfo(final List<Room> roomList) {
        return roomList.stream()
                .map(room -> BasicRoomInfo.of(room))
                .collect(Collectors.toList());
    }

    private boolean matchStudycafeAndMember(final Long studycafeId, final Member member) {
        return studycafeRepository.existsByIdAndMember(studycafeId, member);
    }

    private void deleteRoomConveniences(final Long roomId) {
        convenienceRepository.deleteAllByRoomId(roomId);
    }

    private void removeAllRoomPhotos(final Long roomId) {
        subPhotoRepository.deleteAllByRoomId(roomId);
    }
}