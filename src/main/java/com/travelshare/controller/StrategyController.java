package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.StrategyDTO;
import com.travelshare.entity.Strategy;
import com.travelshare.service.StrategyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyService strategyService;

    @PostMapping("/add")
    public Result add(@Valid @RequestBody StrategyDTO dto) {
        Strategy strategy = strategyService.add(dto);
        return Result.success(strategy);
    }

    @GetMapping("/list")
    public Result approvedList() {
        List<Strategy> list = strategyService.approvedList();
        return Result.success(list);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Strategy strategy = strategyService.detail(id);
        return Result.success(strategy);
    }

    @GetMapping("/search")
    public Result search(@RequestParam(required = false) String city,
                         @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return Result.success(strategyService.searchByKeyword(keyword));
        }
        if (city != null && !city.isEmpty()) {
            return Result.success(strategyService.searchByCity(city));
        }
        return Result.success(strategyService.approvedList());
    }

    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestParam String result,
                          @RequestParam(required = false) String reason,
                          @RequestParam Long adminId) {
        strategyService.approve(id, result, reason, adminId);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result pendingList() {
        return Result.success(strategyService.pendingList());
    }

    @GetMapping("/my")
    public Result myStrategies(@RequestParam Long userId) {
        return Result.success(strategyService.myStrategies(userId));
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id, @RequestParam Long userId) {
        strategyService.delete(id, userId);
        return Result.success();
    }
}