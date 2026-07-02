package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private StrategyService strategyService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private TravelNoticeService travelNoticeService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private CityService cityService;
    @Autowired
    private AttractionService attractionService;

    @GetMapping("/home")
    public Result home() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", userService.count());
        stats.put("strategyCount", strategyService.countApproved());
        stats.put("routeCount", routeService.countApproved());
        stats.put("noticeCount", travelNoticeService.countApproved());
        stats.put("partnerCount", partnerService.countApproved());
        stats.put("cityCount", cityService.count());
        stats.put("attractionCount", attractionService.count());
        return Result.success(stats);
    }
}