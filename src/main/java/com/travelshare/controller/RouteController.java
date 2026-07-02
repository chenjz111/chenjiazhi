package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.RouteDTO;
import com.travelshare.entity.Route;
import com.travelshare.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    @PostMapping("/add")
    public Result add(@Valid @RequestBody RouteDTO dto) {
        Route route = routeService.add(dto);
        return Result.success(route);
    }

    @GetMapping("/list")
    public Result approvedList() {
        return Result.success(routeService.approvedList());
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        return Result.success(routeService.detail(id));
    }

    @GetMapping("/province")
    public Result searchByProvince(@RequestParam String province) {
        return Result.success(routeService.searchByProvince(province));
    }

    @GetMapping("/city")
    public Result searchByCity(@RequestParam String city) {
        return Result.success(routeService.searchByCity(city));
    }

    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestParam String result,
                          @RequestParam(required = false) String reason,
                          @RequestParam Long adminId) {
        routeService.approve(id, result, reason, adminId);
        return Result.success();
    }

    @GetMapping("/pending")
    public Result pendingList() {
        return Result.success(routeService.pendingList());
    }

    @GetMapping("/my")
    public Result myRoutes(@RequestParam Long userId) {
        return Result.success(routeService.myRoutes(userId));
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id, @RequestParam Long userId) {
        routeService.delete(id, userId);
        return Result.success();
    }
}