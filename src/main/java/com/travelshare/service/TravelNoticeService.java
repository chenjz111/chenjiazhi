package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.entity.TravelNotice;
import com.travelshare.dto.TravelNoticeDTO;
import java.util.List;
import java.util.Map;

public interface TravelNoticeService extends IService<TravelNotice> {
    TravelNotice add(TravelNoticeDTO dto);
    TravelNotice detail(Long id);
    List<TravelNotice> approvedList();
    List<TravelNotice> searchByCity(String city);
    List<TravelNotice> searchByAttraction(String attractionName);
    List<TravelNotice> searchByCategory(String category);
    void approve(Long id, String result, String reason, Long adminId);
    /** 智能预警：根据城市查询该城市当前所有有效动态 */
    List<TravelNotice> getWarningsByCity(String city);
    /** 智能预警：根据景点名称查询该景点当前所有有效动态 */
    List<TravelNotice> getWarningsByAttraction(String attractionName);
    /** 首页统计：动态数量 */
    long countApproved();
    /** 待审核列表 */
    List<TravelNotice> pendingList();
    /** 我的动态 */
    List<TravelNotice> myNotices(Long userId);
    void delete(Long id, Long userId);
    /** 定时清理：删除超过24小时未删除的动态 */
    int cleanExpiredNotices();
}