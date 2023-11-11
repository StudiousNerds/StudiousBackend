package nerds.studiousTestProject.studycafe.service;

import lombok.RequiredArgsConstructor;
import nerds.studiousTestProject.common.exception.BadRequestException;
import nerds.studiousTestProject.common.exception.NotFoundException;
import nerds.studiousTestProject.common.service.StorageProvider;
import nerds.studiousTestProject.convenience.dto.modify.ModifyConvenienceRequest;
import nerds.studiousTestProject.convenience.dto.register.RegisterConvenienceRequest;
import nerds.studiousTestProject.member.entity.member.Member;
import nerds.studiousTestProject.member.repository.MemberRepository;
import nerds.studiousTestProject.photo.entity.SubPhoto;
import nerds.studiousTestProject.photo.entity.SubPhotoType;
import nerds.studiousTestProject.photo.repository.SubPhotoRepository;
import nerds.studiousTestProject.refundpolicy.dto.modify.ModifyRefundPolicyRequest;
import nerds.studiousTestProject.refundpolicy.dto.register.RegisterRefundPolicyRequest;
import nerds.studiousTestProject.room.dto.modify.ModifyRoomRequest;
import nerds.studiousTestProject.room.dto.register.RegisterRoomRequest;
import nerds.studiousTestProject.room.dto.show.ShowRoomBasicResponse;
import nerds.studiousTestProject.room.dto.show.ShowRoomDetailsResponse;
import nerds.studiousTestProject.room.entity.PriceType;
import nerds.studiousTestProject.room.entity.Room;
import nerds.studiousTestProject.room.repository.RoomRepository;
import nerds.studiousTestProject.studycafe.dto.modify.request.ModifyAnnouncementRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.ModifyOperationInfoRequest;
import nerds.studiousTestProject.studycafe.dto.modify.request.ModifyStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyAddressResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyAnnouncementResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyConvenienceResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyOperationInfoResponse;
import nerds.studiousTestProject.studycafe.dto.modify.response.ModifyRefundPolicyResponse;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterOperationInfoRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterRequest;
import nerds.studiousTestProject.studycafe.dto.register.request.RegisterStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.register.response.NearestStationResponse;
import nerds.studiousTestProject.studycafe.dto.register.response.RegisterResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowManagedStudycafeBasicResponse;
import nerds.studiousTestProject.studycafe.dto.show.response.ShowManagedStudycafeDetailsResponse;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateAccountRequest;
import nerds.studiousTestProject.studycafe.dto.validate.request.ValidateBusinessmanRequest;
import nerds.studiousTestProject.studycafe.dto.validate.response.ValidateResponse;
import nerds.studiousTestProject.studycafe.entity.Notice;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import nerds.studiousTestProject.studycafe.util.RegistrationValidator;
import nerds.studiousTestProject.studycafe.util.NearestStationInfoCalculator;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_CAFE_MAIN_PHOTO_SIZE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.INVALID_ROOM_PHOTOS;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_ROOM;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_STUDYCAFE;
import static nerds.studiousTestProject.common.exception.errorcode.ErrorCode.NOT_FOUND_USER;
import static nerds.studiousTestProject.photo.entity.SubPhotoType.ROOM;
import static nerds.studiousTestProject.photo.entity.SubPhotoType.STUDYCAFE;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AdminStudycafeService {
    private final RegistrationValidator registrationValidator;
    private final NearestStationInfoCalculator nearestStationInfoCalculator;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final StorageProvider storageProvider;
    private final StudycafeRepository studycafeRepository;
    private final SubPhotoRepository subPhotoRepository;

    private static final String STUDYCAFE_MAIN_PHOTO_KEY = "studycafeMainPhoto";
    private static final String STUDYCAFE_SUB_PHOTOS_KEY = "studycafeSubPhotos";
    private static final String ROOM_PHOTOS_KEY = "roomPhotos";

    public ValidateResponse validateAccount(final ValidateAccountRequest validateAccountRequest) {
        return registrationValidator.getValidateAccountRequest(validateAccountRequest);
    }

    public ValidateResponse validateBusinessman(final ValidateBusinessmanRequest validateBusinessmanRequest) {
        return registrationValidator.getValidateBusinessmanRequest(validateBusinessmanRequest);
    }

    @Transactional
    public RegisterResponse register(final Long memberId,
                                     final RegisterRequest registerRequest,
                                     final MultiValueMap<String, MultipartFile> multipartFileMap) {
        final RegisterStudycafeRequest registerStudycafeRequest = registerRequest.getStudycafe();
        final List<RegisterRoomRequest> registerRoomRequests = registerRequest.getRooms();
        validatePhotos(multipartFileMap, registerRoomRequests);

        final Member member = findMemberById(memberId);
        final NearestStationResponse nearestStationResponse = getNearestStationResponse(registerStudycafeRequest);
        final Studycafe studycafe = registerStudycafeRequest.toStudycafe(member, nearestStationResponse);

        registerStudycafe(multipartFileMap, registerStudycafeRequest, studycafe);
        registerRooms(registerRoomRequests, multipartFileMap, studycafe);

        studycafeRepository.save(studycafe);
        return RegisterResponse.builder()
                .cafeName(registerStudycafeRequest.getName())
                .build();
    }

    private void validatePhotos(final MultiValueMap<String, MultipartFile> multipartFileMap, final List<RegisterRoomRequest> registerRoomRequests) {
        if (!multipartFileMap.containsKey(STUDYCAFE_MAIN_PHOTO_KEY) || multipartFileMap.get(STUDYCAFE_MAIN_PHOTO_KEY).size() != 1) {
            throw new BadRequestException(INVALID_CAFE_MAIN_PHOTO_SIZE);
        }

        final int size = registerRoomRequests.size();
        final long count = multipartFileMap.keySet().stream().filter(f -> f.contains(ROOM_PHOTOS_KEY)).count();
        if (size != count) {
            throw new BadRequestException(INVALID_ROOM_PHOTOS);
        }

        for (int i = 0; i < size; i++) {
            final List<MultipartFile> multipartFiles = multipartFileMap.get(ROOM_PHOTOS_KEY + i);
            if (multipartFiles == null || multipartFiles.isEmpty()) {
                throw new BadRequestException(INVALID_ROOM_PHOTOS);
            }
        }
    }

    private void registerStudycafe(final MultiValueMap<String, MultipartFile> multipartFileMap,
                                   final RegisterStudycafeRequest registerStudycafeRequest,
                                   final Studycafe studycafe) {
        registerStudycafeMainPhoto(multipartFileMap, studycafe);
        registerStudycafeSubPhotos(multipartFileMap, studycafe);
        registerNotices(registerStudycafeRequest, studycafe);
        registerRefundPolices(registerStudycafeRequest, studycafe);
        registerOperationInfos(registerStudycafeRequest, studycafe);
        registerStudycafeConveniences(registerStudycafeRequest, studycafe);
    }

    private void registerStudycafeMainPhoto(final MultiValueMap<String, MultipartFile> multipartFileMap, final Studycafe studycafe) {
        final String cafeMainPhoto = storageProvider.uploadFile(multipartFileMap.getFirst(STUDYCAFE_MAIN_PHOTO_KEY));
        studycafe.updatePhoto(cafeMainPhoto);
    }

    private void registerStudycafeSubPhotos(final MultiValueMap<String, MultipartFile> multipartFileMap, final Studycafe studycafe) {
        final List<MultipartFile> cafeSubPhotos = multipartFileMap.get(STUDYCAFE_SUB_PHOTOS_KEY);
        for (MultipartFile cafeSubPhoto : cafeSubPhotos) {
            final String photoUrl = storageProvider.uploadFile(cafeSubPhoto);
            studycafe.addSubPhoto(SubPhoto.builder().path(photoUrl).type(STUDYCAFE).build());
        }
    }

    private void registerStudycafeConveniences(final RegisterStudycafeRequest registerStudycafeRequest, final Studycafe studycafe) {
        final List<RegisterConvenienceRequest> cafeConveniences = registerStudycafeRequest.getConveniences();
        for (RegisterConvenienceRequest registerConvenienceRequest : cafeConveniences) {
            studycafe.addConvenience(registerConvenienceRequest.toEntity());
        }
    }

    private void registerRooms(final List<RegisterRoomRequest> registerRoomRequests,
                               final MultiValueMap<String, MultipartFile> multipartFileMap,
                               final Studycafe studycafe) {
        for (int i = 0; i < registerRoomRequests.size(); i++) {
            final RegisterRoomRequest registerRoomRequest = registerRoomRequests.get(i);
            final Room room = registerRoomRequest.toRoom();
            final List<RegisterConvenienceRequest> conveniences = registerRoomRequest.getConveniences();

            registerRoomPhotos(multipartFileMap, ROOM_PHOTOS_KEY + i, room);
            registerRoomConveniences(conveniences, room);
            studycafe.addRoom(room);
        }
    }

    private void registerRoomPhotos(final MultiValueMap<String, MultipartFile> multipartFileMap,
                                    final String key,
                                    final Room room) {
        final List<MultipartFile> roomSubPhotos = multipartFileMap.get(key);
        for (MultipartFile roomSubPhoto : roomSubPhotos) {
            final String roomSubPhotoUrl = storageProvider.uploadFile(roomSubPhoto);
            room.addSubPhoto(SubPhoto.builder().path(roomSubPhotoUrl).type(ROOM).build());
        }
    }

    private void registerRoomConveniences(final List<RegisterConvenienceRequest> registerConvenienceRequests, final Room room) {
        for (RegisterConvenienceRequest registerConvenienceRequest : registerConvenienceRequests) {
            room.addConvenience(registerConvenienceRequest.toEntity());
        }
    }

    private void registerOperationInfos(final RegisterStudycafeRequest registerStudycafeRequest, final Studycafe studycafe) {
        final List<RegisterOperationInfoRequest> registerOperationInfoRequests = registerStudycafeRequest.getOperationInfos();
        for (RegisterOperationInfoRequest registerOperationInfoRequest : registerOperationInfoRequests) {
            studycafe.addOperationInfo(registerOperationInfoRequest.toOperationInfo());
        }
    }

    private void registerRefundPolices(final RegisterStudycafeRequest registerStudycafeRequest, final Studycafe studycafe) {
        final List<RegisterRefundPolicyRequest> refundPolicies = registerStudycafeRequest.getRefundPolicies();
        for (RegisterRefundPolicyRequest registerRefundPolicyRequest : refundPolicies) {
            studycafe.addRefundPolicy(registerRefundPolicyRequest.toEntity());
        }
    }

    private void registerNotices(final RegisterStudycafeRequest registerStudycafeRequest, final Studycafe studycafe) {
        final List<String> noticeDetails = registerStudycafeRequest.getNotices();
        for (String noticeDetail : noticeDetails) {
            studycafe.addNotice(Notice.builder()
                    .detail(noticeDetail)
                    .studycafe(studycafe)
                    .build()
            );
        }
    }

    @Transactional
    public void registerAnnouncements(final Long memberId,
                                      final Long studycafeId,
                                      final ModifyAnnouncementRequest modifyAnnouncementRequest) {
        final Studycafe studycafe = findStudycafeByIdAndMember(studycafeId, memberId);
        studycafe.addAnnouncement(modifyAnnouncementRequest.toAnnouncement());
    }

    private NearestStationResponse getNearestStationResponse(final RegisterStudycafeRequest registerStudycafeRequest) {
        final String latitude = registerStudycafeRequest.getAddress().getLatitude();
        final String longitude = registerStudycafeRequest.getAddress().getLongitude();
        return nearestStationInfoCalculator.getPlaceResponse(latitude, longitude);
    }

    public List<ShowManagedStudycafeBasicResponse> enquireStudycafes(final Long memberId, final Pageable pageable) {
        final Member member = findMemberById(memberId);
        return studycafeRepository.findByMemberOrderByCreatedDateAsc(member, pageable).getContent()
                .stream().map(ShowManagedStudycafeBasicResponse::from).toList();
    }

    public ShowManagedStudycafeDetailsResponse enquireStudycafe(final Long memberId, final Long studycafeId) {
        final Studycafe studycafe = findStudycafeByIdAndMember(studycafeId, memberId);
        final ModifyAddressResponse modifyAddressResponse = ModifyAddressResponse.from(studycafe.getAddress());
        final List<ModifyConvenienceResponse> modifyConvenienceResponses = studycafe.getConveniences().stream().map(ModifyConvenienceResponse::from).toList();
        final List<ModifyOperationInfoResponse> modifyOperationInfoResponses = studycafe.getOperationInfos().stream().map(ModifyOperationInfoResponse::from).toList();
        final List<String> notices = studycafe.getNotices().stream().map(Notice::getDetail).toList();
        final List<String> photos = getPhotos(studycafe);
        final List<ModifyRefundPolicyResponse> modifyRefundPolicyResponses = studycafe.getRefundPolicies().stream().map(ModifyRefundPolicyResponse::from).toList();

        return ShowManagedStudycafeDetailsResponse.of(studycafe, modifyAddressResponse, modifyConvenienceResponses, modifyOperationInfoResponses, modifyRefundPolicyResponses, photos, notices);
    }

    public List<ShowRoomBasicResponse> enquireRooms(final Long memberId,
                                                    final Long studycafeId) {
        Studycafe studycafe = findStudycafeByIdAndMember(studycafeId, memberId);
        return studycafe.getRooms().stream().map(ShowRoomBasicResponse::from).toList();
    }

    public ShowRoomDetailsResponse enquireRoom(final Long memberId,
                                               final Long studycafeId,
                                               final Long roomId) {
        // memberId, studycafeId 를 통한 검증로직 있으면 좋을 것 같음 (이번 스프린트때 진행할 내용. 구현되는데로 주석 삭제할 예정)
        Room room = findRoomById(roomId);
        return ShowRoomDetailsResponse.from(room);
    }

    private List<String> getPhotos(final Studycafe studycafe) {
        final List<String> photos = new ArrayList<>();
        photos.add(studycafe.getPhoto());

        final List<String> subPhotos = studycafe.getSubPhotos().stream().map(SubPhoto::getPath).toList();
        photos.addAll(subPhotos);

        return photos;
    }

    @Transactional
    public void modify(final Long memberId,
                       final Long studycafeId,
                       final ModifyStudycafeRequest modifyStudycafeRequest) {
        final Studycafe studycafe = findStudycafeByIdAndMember(studycafeId, memberId);
        studycafe.update(
                modifyStudycafeRequest.getIntroduction(),
                modifyStudycafeRequest.getConveniences().stream().map(ModifyConvenienceRequest::toConvenience).toList(),
                modifyStudycafeRequest.getNotices().stream().map(s -> Notice.builder().detail(s).build()).toList(),
                modifyStudycafeRequest.getOperationInfos().stream().map(ModifyOperationInfoRequest::toOperationInfo).toList(),
                modifyStudycafeRequest.getRefundPolicies().stream().map(ModifyRefundPolicyRequest::toEntity).toList()
        );
        // 사진은 추후 (이번 스프린트때 진행. 구현하는대로 주석 삭제)
    }

    public List<ModifyAnnouncementResponse> enquiryAnnouncements(final Long memberId, final Long studycafeId) {
        final Studycafe studycafe = findStudycafeByIdAndMember(studycafeId, memberId);
        return studycafe.getAnnouncements().stream().map(ModifyAnnouncementResponse::from).toList();
    }

    @Transactional
    public void modifyRoom(final Long roomId,
                           final ModifyRoomRequest modifyRoomRequest,
                           final List<MultipartFile> roomPhotos) {
        Room room = findRoomById(roomId);
        room.update(
                modifyRoomRequest.getName(),
                modifyRoomRequest.getMinHeadCount(),
                modifyRoomRequest.getMaxHeadCount(),
                modifyRoomRequest.getPrice(),
                PriceType.valueOf(modifyRoomRequest.getType()),
                modifyRoomRequest.getMinUsingTime(),
                modifyRoomRequest.getConveniences().stream().map(ModifyConvenienceRequest::toConvenience).toList()
        );

        modifyRoomPhotos(room, roomPhotos);
    }

    private void modifyRoomPhotos(Room room, List<MultipartFile> roomPhotos) {
        if (roomPhotos == null) {
            return;
        }

        deletePhoto(room.getSubPhotos());
        deleteAllByRoomId(room.getId());
        for (MultipartFile file : roomPhotos) {
            String photoUrl = storageProvider.uploadFile(file);
            room.addSubPhoto(SubPhoto.builder().room(room).type(SubPhotoType.ROOM).path(photoUrl).build());
        }
    }

    private void deletePhoto(List<SubPhoto> subPhotos) {
        for (SubPhoto subPhoto : subPhotos) {
            storageProvider.deleteFile(subPhoto.getPath());
        }
    }

    @Transactional
    public void delete(final Long memberId, final Long studycafeId) {
        studycafeRepository.deleteByIdAndMemberId(studycafeId, memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    @Transactional
    public void deleteRoom(final Long roomId) {
        roomRepository.deleteById(roomId);
    }

    private void deleteAllByRoomId(Long roomId) {
        subPhotoRepository.deleteAllByRoomId(roomId);
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(NOT_FOUND_USER));
    }

    private Studycafe findStudycafeByIdAndMember(final Long studycafeId, final Long memberId) {
        return studycafeRepository.findByIdAndMemberId(studycafeId, memberId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_STUDYCAFE));
    }

    private Room findRoomById(final Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                () -> new NotFoundException(NOT_FOUND_ROOM));
    }
}
