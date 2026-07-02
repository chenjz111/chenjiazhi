package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.PartnerDTO;
import com.travelshare.entity.AdminRecord;
import com.travelshare.entity.Partner;
import com.travelshare.entity.User;
import com.travelshare.mapper.AdminRecordMapper;
import com.travelshare.mapper.PartnerMapper;
import com.travelshare.mapper.UserMapper;
import com.travelshare.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartnerServiceImpl extends ServiceImpl<PartnerMapper, Partner> implements PartnerService {

    @Autowired
    private AdminRecordMapper adminRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Partner add(PartnerDTO dto) {
        Partner partner = new Partner();
        partner.setTitle(dto.getTitle());
        partner.setStartCity(dto.getStartCity());
        partner.setTargetCity(dto.getTargetCity());
        partner.setDepartureDate(dto.getDepartureDate());
        partner.setRequiredPeople(dto.getRequiredPeople());
        partner.setCurrentPeople(dto.getCurrentPeople() != null ? dto.getCurrentPeople() : 1);
        partner.setDescription(dto.getDescription());
        partner.setImages(dto.getImages());
        partner.setPublisherId(dto.getPublisherId());
        partner.setStatus("PENDING");
        partner.setCreateTime(LocalDateTime.now());
        save(partner);
        return partner;
    }

    @Override
    public Partner detail(Long id) {
        return getById(id);
    }

    @Override
    public List<Partner> approvedList() {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Partner::getStatus, "APPROVED").orderByDesc(Partner::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Partner> searchByCity(String city) {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Partner::getStatus, "APPROVED");
        List<Partner> list = list(wrapper);
        return list.stream()
                .filter(p -> city.equals(p.getStartCity()) || city.equals(p.getTargetCity()))
                .toList();
    }

    @Override
    @Transactional
    public void approve(Long id, String result, String reason, Long adminId) {
        Partner partner = getById(id);
        if (partner == null) throw new RuntimeException("搭子信息不存在");
        partner.setStatus(result);
        updateById(partner);

        AdminRecord record = new AdminRecord();
        record.setTargetType("partner");
        record.setTargetId(id);
        record.setAdminId(adminId);
        record.setResult(result);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        adminRecordMapper.insert(record);
    }

    @Override
    public List<Partner> pendingList() {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Partner::getStatus, "PENDING").orderByDesc(Partner::getCreateTime);
        return list(wrapper);
    }

    @Override
    public long countApproved() {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Partner::getStatus, "APPROVED");
        return count(wrapper);
    }

    @Override
    public List<Partner> myPartners(Long userId) {
        LambdaQueryWrapper<Partner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Partner::getPublisherId, userId).orderByDesc(Partner::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Partner partner = getById(id);
        if (partner == null) throw new RuntimeException("搭子信息不存在");
        User user = userMapper.selectById(userId);
        if (!partner.getPublisherId().equals(userId) && (user == null || !"ADMIN".equals(user.getRole()))) {
            throw new RuntimeException("无权删除此搭子信息");
        }
        removeById(id);
    }
}
