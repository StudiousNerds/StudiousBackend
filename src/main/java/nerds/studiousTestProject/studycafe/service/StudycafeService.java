package nerds.studiousTestProject.studycafe.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.hashtag.service.HashtagService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.dto.EventCafeResponse;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.MainPageResponse;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.RecommendCafeResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudycafeService {
    private final StudycafeRepository studycafeRepository;
    private final ReviewService reviewService;
    private final RoomService roomService;
    private final SubPhotoService subPhotoService;
    private final HashtagService hashtagService;
    private final ConvenienceService convenienceService;

    public FindStudycafeResponse findByDate(Long id, FindStudycafeRequest findStudycafeRequest){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new RuntimeException("No Such Studycafe"));

        return FindStudycafeResponse.builder()
                .cafeId(studycafe.getId())
                .cafeName(studycafe.getName())
                .photos(subPhotoService.findCafePhotos(id))
                .accumResCnt(studycafe.getAccumReserveCount())
                .duration(studycafe.getDuration())
                .nearestStation(studycafe.getNearestStation())
                .hashtags(hashtagService.findHashtags(id))
                .introduction(studycafe.getIntroduction())
                .conveniences(convenienceService.getAllCafeConveniences(id))
                .notification(studycafe.getNotificationInfo())
                .refundPolicy(studycafe.getRefundPolicyInfo())
                .notice(getNotice(id))
                .rooms(roomService.getRooms(findStudycafeRequest.getDate(), id))
                .recommendationRate(reviewService.getAvgRecommendation(id))
                .cleanliness(reviewService.getAvgCleanliness(id))
                .deafening(reviewService.getAvgDeafening(id))
                .fixturesStatus(reviewService.getAvgFixturesStatus(id))
                .total(studycafe.getTotalGrade())
                .reviewInfo(reviewService.findAllReviews(studycafe.getId()))
                .build();
    }

    public MainPageResponse getMainPage(){
        List<RecommendCafeResponse> recommendStduycafes = getRecommendStduycafes();
        List<EventCafeResponse> eventStudycafes = getEventStudycafes();
        return MainPageResponse.builder().recommend(recommendStduycafes).event(eventStudycafes).build();
    }

    public List<RecommendCafeResponse> getRecommendStduycafes(){
        List<Studycafe> recommend = studycafeRepository.findTop2ByOrderByTotalGradeDesc();
        List<RecommendCafeResponse> recommendCafeList = new ArrayList<>();

        for (Studycafe studycafe : recommend) {
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            RecommendCafeResponse foundStudycafe = RecommendCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .nearestStation(studycafe.getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            recommendCafeList.add(foundStudycafe);
        }
        return recommendCafeList;

    }

    public List<EventCafeResponse> getEventStudycafes(){
        List<Studycafe> event = studycafeRepository.findTop2ByOrderByCreatedDateDesc();
        List<EventCafeResponse> eventCafeList = new ArrayList<>();

        for (Studycafe studycafe : event) {
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            EventCafeResponse foundStudycafe = EventCafeResponse.builder()
                    .cafeId(studycafe.getId())
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .nearestStation(studycafe.getNearestStation())
                    .grade(studycafe.getTotalGrade())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            eventCafeList.add(foundStudycafe);
        }
        return eventCafeList;
    }

    public Studycafe getStudyCafe(Long studycafeId){
        return studycafeRepository.findById(studycafeId).orElseThrow(() -> new EntityNotFoundException("No Such Cafe"));
    }

    public Studycafe getStudyCafeByName(String cafeName){
        return studycafeRepository.findByName(cafeName).orElseThrow(() -> new EntityNotFoundException("No Such Cafe"));
    }
    public String[] getNotice(Long id){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new RuntimeException("No Such Studycafe"));

        List<String> noticeList = studycafe.getNotice();
        Integer arrSize = noticeList.size();
        String notices[] = noticeList.toArray(new String[arrSize]);

        return notices;
    }
}
