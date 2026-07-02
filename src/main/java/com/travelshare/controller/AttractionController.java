package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.entity.Attraction;
import com.travelshare.service.AttractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/attraction")
public class AttractionController {

    @Autowired
    private AttractionService attractionService;

    @GetMapping("/list")
    public Result listByCity(@RequestParam(required = false) Long cityId) {
        if (cityId != null) {
            List<Attraction> attractions = attractionService.listByCityId(cityId);
            return Result.success(attractions);
        }
        return Result.success(attractionService.list());
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        Attraction attraction = attractionService.detail(id);
        return Result.success(attraction);
    }

    @GetMapping("/search")
    public Result search(@RequestParam String name) {
        List<Attraction> attractions = attractionService.searchByName(name);
        return Result.success(attractions);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Attraction attraction) {
        attractionService.save(attraction);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Attraction attraction) {
        attractionService.updateById(attraction);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        attractionService.removeById(id);
        return Result.success();
    }
}