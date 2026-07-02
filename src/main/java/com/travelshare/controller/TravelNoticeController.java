package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.TravelNoticeDTO;
import com.travelshare.entity.TravelNotice;
import com.travelshare.service.TravelNoticeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/travel-notice")
public class TravelNoticeController {

    @Autowired
    private TravelNoticeService travelNoticeService;

    @PostMapping("/add")
    public Result add(@Valid @RequestBody TravelNoticeDTO dto) {
        TravelNotice notice = travelNoticeService.add(dto);
        return Result.success(notice);
    }

    @GetMapping("/list")
    public Result approvedList() {
        List<TravelNotice> notices = travelNoticeService.approvedList();
        return Result.success(notices);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        TravelNotice notice = travelNoticeService.detail(id);
        return Result.success(notice);
    }

    @GetMapping("/search/city")
    public Result searchByCity(@RequestParam String city) {
        List<TravelNotice> notices = travelNoticeService.searchByCity(city);
        return Result.success(notices);
    }

    @GetMapping("/search/attraction")
    public Result searchByAttraction(@RequestParam String attractionName) {
        List<TravelNotice> notices = travelNoticeService.searchByAttraction(attractionName);
        return Result.success(notices);
    }

    @GetMapping("/search/category")
    public Result searchByCategory(@RequestParam String category) {
        List<TravelNotice> notices = travelNoticeService.searchByCategory(category);
        return Result.success(notices);
    }

    /** 智能预警：城市联动提醒 */
    @GetMapping("/warning/city")
    public Result warningByCity(@RequestParam String city) {
        List<TravelNotice> warnings = travelNoticeService.getWarningsByCity(city);
        return Result.success(warnings);
    }

    /** 智能预警：景点联动提醒 */
    @GetMapping("/warning/attraction")
    public Result warningByAttraction(@RequestParam String attractionName) {
        List<TravelNotice> warnings = travelNoticeService.getWarningsByAttraction(attractionName);
        return Result.success(warnings);
    }

    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id, @RequestParam String result,
                          @RequestParam(required = false) String reason,
                          @RequestParam Long adminId) {
        travelNoticeService.approve(id, result, reason, adminId);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result pendingList() {
        return Result.success(travelNoticeService.pendingList());
    }

    @GetMapping("/my")
    public Result myNotices(@RequestParam Long userId) {
        return Result.success(travelNoticeService.myNotices(userId));
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id, @RequestParam Long userId) {
        travelNoticeService.delete(id, userId);
        return Result.success();
    }

    /** 定时清理：删除超过24小时的动态 */
    @PostMapping("/clean-expired")
    public Result cleanExpired() {
        int count = travelNoticeService.cleanExpiredNotices();
        return Result.success("已清理 " + count + " 条过期动态");
    }
}