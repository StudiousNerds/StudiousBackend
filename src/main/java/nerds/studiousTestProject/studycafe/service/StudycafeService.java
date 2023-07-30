package nerds.studiousTestProject.studycafe.service;

import io.jsonwebtoken.impl.crypto.MacProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.convenience.service.ConvenienceService;
import nerds.studiousTestProject.hashtag.service.HashtagService;
import nerds.studiousTestProject.photo.service.SubPhotoService;
import nerds.studiousTestProject.review.service.ReviewService;
import nerds.studiousTestProject.room.service.RoomService;
import nerds.studiousTestProject.studycafe.dto.FindRecommendStudycafeResponse;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.entity.Studycafe;
import nerds.studiousTestProject.studycafe.repository.StudycafeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public FindStudycafeResponse findByDate(Long id, LocalDate date, LocalTime startTime, LocalTime endTime, Integer headCount){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new RuntimeException("No Such Studycafe"));

        return FindStudycafeResponse.builder()
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
                .rooms(roomService.getRooms(date, id))
                .recommendationRate(reviewService.getAvgRecommendation(id))
                .cleanliness(reviewService.getAvgCleanliness(id))
                .deafening(reviewService.getAvgDeafening(id))
                .fixturesStatus(reviewService.getAvgFixturesStatus(id))
                .total(reviewService.getAvgGrade(id))
                .reviewInfo(reviewService.findAllReviews(studycafe.getId()))
                .build();
    }

    public List<FindRecommendStudycafeResponse> getRecommendStduycafes(){
        List<Studycafe> topTenCafeList = studycafeRepository.findTop10ByOrderByTotalGardeDesc();
        List<FindRecommendStudycafeResponse> recommendStudycafeList = new ArrayList<>();

        for (Studycafe studycafe : topTenCafeList) {
            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
            FindRecommendStudycafeResponse foundStudycafe = FindRecommendStudycafeResponse.builder()
                    .cafeName(studycafe.getName())
                    .photo(cafePhotos[0])
                    .accumRevCnt(studycafe.getAccumReserveCount())
                    .distance(studycafe.getDuration())
                    .grade(studycafe.getTotalGarde())
                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
                    .build();
            recommendStudycafeList.add(foundStudycafe);
        }
        return recommendStudycafeList;
    }

//    public List<FindRecommendStudycafeResponse> getRecommendStudycafe(){
//        Map<Studycafe, Double> averageList = getStudycafeAvgGrade();
//        Map<Studycafe, Double> topTenList = getTopTenStudycafeList(averageList);
//        List<Studycafe> topTenCafeList = getTopTenStudycafesName(topTenList);
//        List<FindRecommendStudycafeResponse> recommendStudycafeList = getRecommendStudycafes(topTenList, topTenCafeList);
//        return recommendStudycafeList;
//    }
//
//
//    private Map<Studycafe, Double> getStudycafeAvgGrade() {
//        List<Studycafe> studycafeList = studycafeRepository.findAll();
//        Map<Studycafe, Double> averageList = new HashMap<>();
//        for (Studycafe studyCafe : studycafeList) {
//            averageList.put(studyCafe, studyCafe.getTotalGarde());
//        }
//        return averageList;
//    }
//
//    private Map<Studycafe, Double> getTopTenStudycafeList(Map<Studycafe, Double> averageList) {
//        Map<Studycafe, Double> topTenList = new HashMap<>();
//        List<Studycafe> keySetList = new ArrayList<>(averageList.keySet());
//        Collections.sort(keySetList, (o1, o2) -> averageList.get(o2).compareTo(averageList.get(o1)));
//        for (Studycafe key : keySetList) {
//            topTenList.put(key, averageList.get(key));
//        }
//        return topTenList;
//    }
//
//    private List<Studycafe> getTopTenStudycafesName(Map<Studycafe, Double> topTenList) {
//        List<Studycafe> topTenCafeList = new ArrayList<>();
//        for (Studycafe studycafe : topTenList.keySet()) {
//            topTenCafeList.add(studycafe);
//        }
//        return topTenCafeList;
//    }
//
//    private List<FindRecommendStudycafeResponse> getRecommendStudycafes(Map<Studycafe, Double> topTenList, List<Studycafe> topTenCafeList) {
//        List<FindRecommendStudycafeResponse> recommendStudycafeList = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            Studycafe studycafe = topTenCafeList.get(i);
//            String[] cafePhotos = subPhotoService.findCafePhotos(studycafe.getId());
//            FindRecommendStudycafeResponse foundStudycafe = FindRecommendStudycafeResponse.builder()
//                    .cafeName(studycafe.getName())
//                    .photo(cafePhotos[0])
//                    .accumRevCnt(studycafe.getAccumReserveCount())
//                    .distance(studycafe.getDuration())
//                    .grade(topTenList.get(studycafe))
//                    .hashtags(hashtagService.findHashtags(studycafe.getId()))
//                    .build();
//            recommendStudycafeList.add(foundStudycafe);
//        }
//        return recommendStudycafeList;
//    }

    public String[] getNotice(Long id){
        Studycafe studycafe = studycafeRepository.findById(id).orElseThrow(() -> new RuntimeException("No Such Studycafe"));

        List<String> noticeList = studycafe.getNotice();
        Integer arrSize = noticeList.size();
        String notices[] = noticeList.toArray(new String[arrSize]);

        return notices;
    }

//    public LocalTime getOpenTime(Long studycafeId){
//        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new RuntimeException("No Such Studycafe"));
//
//        return studycafe.getStartTime();
//    }
//
//    public LocalTime getEndTime(Long studycafeId){
//        Studycafe studycafe = studycafeRepository.findById(studycafeId).orElseThrow(() -> new RuntimeException("No Such Studycafe"));
//
//        return studycafe.getEndTime();
//    }
}
