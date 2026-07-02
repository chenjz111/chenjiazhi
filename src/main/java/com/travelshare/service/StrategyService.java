package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.StrategyDTO;
import com.travelshare.entity.Strategy;
import java.util.List;

public interface StrategyService extends IService<Strategy> {
    Strategy add(StrategyDTO dto);
    Strategy detail(Long id);
    List<Strategy> searchByCity(String city);
    List<Strategy> searchByKeyword(String keyword);
    void approve(Long id, String result, String reason, Long adminId);
    List<Strategy> approvedList();
    List<Strategy> pendingList();
    List<Strategy> myStrategies(Long userId);
    long countApproved();
    void delete(Long id, Long userId);
}