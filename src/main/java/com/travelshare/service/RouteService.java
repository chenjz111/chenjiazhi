package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.RouteDTO;
import com.travelshare.entity.Route;
import java.util.List;

public interface RouteService extends IService<Route> {
    Route add(RouteDTO dto);
    Route detail(Long id);
    List<Route> approvedList();
    List<Route> searchByProvince(String province);
    List<Route> searchByCity(String city);
    void approve(Long id, String result, String reason, Long adminId);
    List<Route> pendingList();
    long countApproved();
    List<Route> myRoutes(Long userId);
    void delete(Long id, Long userId);
}