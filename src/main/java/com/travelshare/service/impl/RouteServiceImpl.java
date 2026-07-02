package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.RouteDTO;
import com.travelshare.entity.AdminRecord;
import com.travelshare.entity.Route;
import com.travelshare.entity.User;
import com.travelshare.mapper.AdminRecordMapper;
import com.travelshare.mapper.RouteMapper;
import com.travelshare.mapper.UserMapper;
import com.travelshare.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements RouteService {

    @Autowired
    private AdminRecordMapper adminRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Route add(RouteDTO dto) {
        Route route = new Route();
        route.setProvince(dto.getProvince());
        route.setStartCity(dto.getStartCity());
        route.setMiddleCity(dto.getMiddleCity());
        route.setEndCity(dto.getEndCity());
        route.setTransportType(dto.getTransportType());
        route.setDuration(dto.getDuration());
        route.setPrice(dto.getPrice());
        route.setDescription(dto.getDescription());
        route.setPublisherId(dto.getPublisherId());
        route.setStatus("PENDING");
        route.setCreateTime(LocalDateTime.now());
        save(route);
        return route;
    }

    @Override
    public Route detail(Long id) {
        return getById(id);
    }

    @Override
    public List<Route> approvedList() {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "APPROVED").orderByDesc(Route::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Route> searchByProvince(String province) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getProvince, province).eq(Route::getStatus, "APPROVED");
        return list(wrapper);
    }

    @Override
    public List<Route> searchByCity(String city) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "APPROVED");
        List<Route> routes = list(wrapper);
        return routes.stream()
                .filter(r -> city.equals(r.getStartCity()) || city.equals(r.getMiddleCity()) || city.equals(r.getEndCity()))
                .toList();
    }

    @Override
    @Transactional
    public void approve(Long id, String result, String reason, Long adminId) {
        Route route = getById(id);
        if (route == null) throw new RuntimeException("路线不存在");
        route.setStatus(result);
        updateById(route);

        AdminRecord record = new AdminRecord();
        record.setTargetType("route");
        record.setTargetId(id);
        record.setAdminId(adminId);
        record.setResult(result);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        adminRecordMapper.insert(record);
    }

    @Override
    public List<Route> pendingList() {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "PENDING").orderByDesc(Route::getCreateTime);
        return list(wrapper);
    }

    @Override
    public long countApproved() {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getStatus, "APPROVED");
        return count(wrapper);
    }

    @Override
    public List<Route> myRoutes(Long userId) {
        LambdaQueryWrapper<Route> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Route::getPublisherId, userId).orderByDesc(Route::getCreateTime);
        return list(wrapper);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userId) {
        Route route = getById(id);
        if (route == null) throw new RuntimeException("路线不存在");
        User user = userMapper.selectById(userId);
        // 只有发布者本人或管理员可以删除
        if (!route.getPublisherId().equals(userId) && (user == null || !"ADMIN".equals(user.getRole()))) {
            throw new RuntimeException("无权删除此路线");
        }
        removeById(id);
    }
}
