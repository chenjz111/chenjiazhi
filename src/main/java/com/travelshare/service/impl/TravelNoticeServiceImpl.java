package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.TravelNoticeDTO;
import com.travelshare.entity.AdminRecord;
import com.travelshare.entity.TravelNotice;
import com.travelshare.entity.User;
import com.travelshare.mapper.AdminRecordMapper;
import com.travelshare.mapper.TravelNoticeMapper;
import com.travelshare.mapper.UserMapper;
import com.travelshare.service.TravelNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TravelNoticeServiceImpl extends ServiceImpl<TravelNoticeMapper, TravelNotice> implements TravelNoticeService {

    @Autowired
    private AdminRecordMapper adminRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public TravelNotice add(TravelNoticeDTO dto) {
        TravelNotice notice = new TravelNotice();
        notice.setCity(dto.getCity());
        notice.setAttractionId(dto.getAttractionId());
        notice.setAttractionName(dto.getAttractionName());
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setCategory(dto.getCategory());
        notice.setLevel(dto.getLevel() != null ? dto.getLevel() : "info");
        notice.setImages(dto.getImages());
        notice.setPublisherId(dto.getPublisherId());
        notice.setStatus("PENDING");
        notice.setCreateTime(LocalDateTime.now());
        save(notice);
        return notice;
    }

    @Override
    public TravelNotice detail(Long id) {
        return getById(id);
    }

    @Override
    public List<TravelNotice> approvedList() {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getStatus, "APPROVED")
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TravelNotice> searchByCity(String city) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getCity, city)
               .eq(TravelNotice::getStatus, "APPROVED")
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TravelNotice> searchByAttraction(String attractionName) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TravelNotice::getAttractionName, attractionName)
               .eq(TravelNotice::getStatus, "APPROVED")
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TravelNotice> searchByCategory(String category) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getCategory, category)
               .eq(TravelNotice::getStatus, "APPROVED")
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void approve(Long id, String result, String reason, Long adminId) {
        TravelNotice notice = getById(id);
        if (notice == null) throw new RuntimeException("动态不存在");
        notice.setStatus(result);
        updateById(notice);

        // 记录审核信息
        AdminRecord record = new AdminRecord();
        record.setTargetType("travel_notice");
        record.setTargetId(id);
        record.setAdminId(adminId);
        record.setResult(result);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        adminRecordMapper.insert(record);
    }

    @Override
    public List<TravelNotice> getWarningsByCity(String city) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getCity, city)
               .eq(TravelNotice::getStatus, "APPROVED")
               .ne(TravelNotice::getCategory, "临时活动") // 非活动类即为预警
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TravelNotice> getWarningsByAttraction(String attractionName) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(TravelNotice::getAttractionName, attractionName)
               .eq(TravelNotice::getStatus, "APPROVED")
               .orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public long countApproved() {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getStatus, "APPROVED");
        return count(wrapper);
    }

    @Override
    public List<TravelNotice> pendingList() {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getStatus, "PENDING").orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<TravelNotice> myNotices(Long userId) {
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TravelNotice::getPublisherId, userId).orderByDesc(TravelNotice::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        TravelNotice notice = getById(id);
        if (notice == null) throw new RuntimeException("动态不存在");
        User user = userMapper.selectById(userId);
        if (!notice.getPublisherId().equals(userId) && (user == null || !"ADMIN".equals(user.getRole()))) {
            throw new RuntimeException("无权删除此动态");
        }
        removeById(id);
    }

    @Override
    @Transactional
    public int cleanExpiredNotices() {
        // 删除创建超过24小时的动态
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        LambdaQueryWrapper<TravelNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(TravelNotice::getCreateTime, cutoff);
        List<TravelNotice> expiredList = list(wrapper);
        for (TravelNotice notice : expiredList) {
            removeById(notice.getId());
        }
        return expiredList.size();
    }
}