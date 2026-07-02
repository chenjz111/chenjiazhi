package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.PartnerDTO;
import com.travelshare.entity.Partner;
import com.travelshare.service.PartnerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/partner")
public class PartnerController {

    @Autowired
    private PartnerService partnerService;

    @PostMapping("/add")
    public Result add(@Valid @RequestBody PartnerDTO dto) {
        Partner partner = partnerService.add(dto);
        return Result.success(partner);
    }

    @GetMapping("/list")
    public Result approvedList() {
        return Result.success(partnerService.approvedList());
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(partnerService.detail(id));
    }

    @GetMapping("/city")
    public Result searchByCity(@RequestParam String city) {
        return Result.success(partnerService.searchByCity(city));
    }

    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestParam String result,
                          @RequestParam(required = false) String reason,
                          @RequestParam Long adminId) {
        partnerService.approve(id, result, reason, adminId);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result pendingList() {
        return Result.success(partnerService.pendingList());
    }

    @GetMapping("/my")
    public Result myPartners(@RequestParam Long userId) {
        return Result.success(partnerService.myPartners(userId));
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id, @RequestParam Long userId) {
        partnerService.delete(id, userId);
        return Result.success();
    }
}