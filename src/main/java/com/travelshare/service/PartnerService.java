package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.PartnerDTO;
import com.travelshare.entity.Partner;
import java.util.List;

public interface PartnerService extends IService<Partner> {
    Partner add(PartnerDTO dto);
    Partner detail(Long id);
    List<Partner> approvedList();
    List<Partner> searchByCity(String city);
    void approve(Long id, String result, String reason, Long adminId);
    List<Partner> pendingList();
    long countApproved();
    List<Partner> myPartners(Long userId);
    void delete(Long id, Long userId);
}