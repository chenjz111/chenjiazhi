package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.StrategyDTO;
import com.travelshare.entity.AdminRecord;
import com.travelshare.entity.Strategy;
import com.travelshare.entity.User;
import com.travelshare.mapper.AdminRecordMapper;
import com.travelshare.mapper.StrategyMapper;
import com.travelshare.mapper.UserMapper;
import com.travelshare.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements StrategyService {

    @Autowired
    private AdminRecordMapper adminRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Strategy add(StrategyDTO dto) {
        Strategy strategy = new Strategy();
        strategy.setTitle(dto.getTitle());
        strategy.setContent(dto.getContent());
        strategy.setCity(dto.getCity());
        strategy.setAuthorId(dto.getAuthorId());
        strategy.setImages(dto.getImages());
        strategy.setStatus("PENDING");
        strategy.setCreateTime(LocalDateTime.now());
        save(strategy);
        return strategy;
    }

    @Override
    public Strategy detail(Long id) {
        return getById(id);
    }

    @Override
    public List<Strategy> searchByCity(String city) {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getCity, city).eq(Strategy::getStatus, "APPROVED");
        return list(wrapper);
    }

    @Override
    public List<Strategy> searchByKeyword(String keyword) {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Strategy::getTitle, keyword).eq(Strategy::getStatus, "APPROVED");
        return list(wrapper);
    }

    @Override
    @Transactional
    public void approve(Long id, String result, String reason, Long adminId) {
        Strategy strategy = getById(id);
        if (strategy == null) throw new RuntimeException("攻略不存在");
        strategy.setStatus(result);
        updateById(strategy);

        AdminRecord record = new AdminRecord();
        record.setTargetType("strategy");
        record.setTargetId(id);
        record.setAdminId(adminId);
        record.setResult(result);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        adminRecordMapper.insert(record);
    }

    @Override
    public List<Strategy> approvedList() {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getStatus, "APPROVED").orderByDesc(Strategy::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Strategy> pendingList() {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getStatus, "PENDING").orderByDesc(Strategy::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Strategy> myStrategies(Long userId) {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getAuthorId, userId).orderByDesc(Strategy::getCreateTime);
        return list(wrapper);
    }

    @Override
    public long countApproved() {
        LambdaQueryWrapper<Strategy> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Strategy::getStatus, "APPROVED");
        return count(wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Strategy strategy = getById(id);
        if (strategy == null) throw new RuntimeException("攻略不存在");
        User user = userMapper.selectById(userId);
        if (!strategy.getAuthorId().equals(userId) && (user == null || !"ADMIN".equals(user.getRole()))) {
            throw new RuntimeException("无权删除此攻略");
        }
        removeById(id);
    }
}
