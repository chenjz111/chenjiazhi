package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.entity.City;
import com.travelshare.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/city")
public class CityController {

    @Autowired
    private CityService cityService;

    @GetMapping("/list")
    public Result listAll() {
        List<City> cities = cityService.listAll();
        return Result.success(cities);
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id) {
        City city = cityService.detail(id);
        return Result.success(city);
    }

    @GetMapping("/search")
    public Result searchByProvince(@RequestParam String province) {
        List<City> cities = cityService.searchByProvince(province);
        return Result.success(cities);
    }

    @PostMapping("/add")
    public Result add(@RequestBody City city) {
        cityService.save(city);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody City city) {
        cityService.updateById(city);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        cityService.removeById(id);
        return Result.success();
    }
}