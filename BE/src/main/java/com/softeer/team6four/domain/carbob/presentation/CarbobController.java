package com.softeer.team6four.domain.carbob.presentation;

import com.softeer.team6four.domain.carbob.application.CarbobSearchService;
import com.softeer.team6four.domain.carbob.application.response.MyCarbobDetailInfo;
import com.softeer.team6four.domain.carbob.application.response.MyCarbobSummary;
import com.softeer.team6four.global.annotation.Auth;
import com.softeer.team6four.global.filter.UserContextHolder;
import com.softeer.team6four.global.response.ResponseDto;
import com.softeer.team6four.global.response.SliceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/carbob")
@RequiredArgsConstructor
public class CarbobController {
    private final CarbobSearchService carbobSearchService;

    @Auth
    @GetMapping(value = "/my")
    public ResponseDto<SliceResponse<MyCarbobSummary>> getMyCarbobList
        (
            @RequestParam MyCarbobSortType sortType,
            @RequestParam(required = false) Long lastCarbobId,
            @RequestParam(required = false) Long lastReservationCount,
            @PageableDefault(size = 6) Pageable pageable
        )
    {
        Long userId = UserContextHolder.get();
        return carbobSearchService.findMyCarbobList(userId, sortType, lastCarbobId, lastReservationCount, pageable);
    }

    @Auth
    @GetMapping(value = "/detail/{carbobId}")
    public ResponseDto<MyCarbobDetailInfo> getMyCarbobDetail(@PathVariable Long carbobId) {
        Long userId = UserContextHolder.get();
        return carbobSearchService.findMyCarbobDetailInfo(userId, carbobId);
    }
}
