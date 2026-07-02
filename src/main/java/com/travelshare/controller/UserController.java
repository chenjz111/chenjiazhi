package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.LoginDTO;
import com.travelshare.dto.RegisterDTO;
import com.travelshare.entity.User;
import com.travelshare.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result list() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    @PostMapping("/login")
    public Result login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    /** 获取当前登录用户信息 */
    @GetMapping("/current")
    public Result currentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getCurrentUser(userId);
        return Result.success(user);
    }

    /** 禁用/启用用户 */
    @PutMapping("/toggle-status/{id}")
    public Result toggleStatus(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) throw new RuntimeException("用户不存在");
        user.setRole(user.getRole().equals("DISABLED") ? "USER" : "DISABLED");
        userService.updateById(user);
        return Result.success();
    }

    /** 删除用户 */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}