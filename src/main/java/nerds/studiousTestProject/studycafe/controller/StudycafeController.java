package nerds.studiousTestProject.studycafe.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeRequest;
import nerds.studiousTestProject.studycafe.dto.FindStudycafeResponse;
import nerds.studiousTestProject.studycafe.service.StudycafeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studious")
public class StudycafeController {
    private final StudycafeService studycafeService;

    @GetMapping("/studycafes/{cafeId}")
    public FindStudycafeResponse findStudycafeInfo(@PathVariable("cafeId") Long cafeId, @RequestBody FindStudycafeRequest findStudycafeRequest){
        return studycafeService.findByDate(cafeId, findStudycafeRequest);
    }


}
