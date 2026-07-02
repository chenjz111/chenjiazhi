package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.CityNoticeDTO;
import com.travelshare.service.CityNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notice")
public class CityNoticeController {

    @Autowired
    private CityNoticeService cityNoticeService;

    @PostMapping("/add")
    public Result add(@RequestBody CityNoticeDTO dto){

        cityNoticeService.add(dto);

        return Result.success();
    }

    @GetMapping("/list")
    public Result list(){

        return Result.success(
                cityNoticeService.approvedList()
        );
    }

    @GetMapping("/detail/{id}")
    public Result detail(@PathVariable Long id){

        return Result.success(
                cityNoticeService.detail(id)
        );
    }

    @GetMapping("/search")
    public Result search(String city){

        return Result.success(
                cityNoticeService.searchByCity(city)
        );
    }

    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id){

        cityNoticeService.approve(id);

        return Result.success();
    }
}