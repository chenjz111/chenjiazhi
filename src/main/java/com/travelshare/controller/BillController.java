package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.BillRecordDTO;
import com.travelshare.dto.CreateRoomDTO;
import com.travelshare.dto.JoinRoomDTO;
import com.travelshare.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    // ==================== 房间操作 ====================

    @PostMapping("/room/create")
    public Result createRoom(@Valid @RequestBody CreateRoomDTO dto) {
        return Result.success(billService.createRoom(dto));
    }

    @PostMapping("/room/join")
    public Result joinRoom(@Valid @RequestBody JoinRoomDTO dto) {
        billService.joinRoom(dto);
        return Result.success();
    }

    @PostMapping("/room/approve")
    public Result approveMember(@RequestParam Long roomId,
                                @RequestParam Long memberId,
                                @RequestParam Long approverId) {
        billService.approveMember(roomId, memberId, approverId);
        return Result.success();
    }

    @PostMapping("/room/reject")
    public Result rejectMember(@RequestParam Long roomId,
                               @RequestParam Long memberId,
                               @RequestParam Long approverId) {
        billService.rejectMember(roomId, memberId, approverId);
        return Result.success();
    }

    @GetMapping("/room/my")
    public Result myRooms(@RequestParam Long userId) {
        return Result.success(billService.myRooms(userId));
    }

    @GetMapping("/room/detail/{id}")
    public Result roomDetail(@PathVariable Long id) {
        return Result.success(billService.roomDetail(id));
    }

    @GetMapping("/room/members")
    public Result roomMembers(@RequestParam Long roomId) {
        return Result.success(billService.roomMembers(roomId));
    }

    // ==================== 账单操作 ====================

    @PostMapping("/record/add")
    public Result addRecord(@Valid @RequestBody BillRecordDTO dto) {
        return Result.success(billService.addRecord(dto));
    }

    @GetMapping("/record/list")
    public Result roomRecords(@RequestParam Long roomId) {
        return Result.success(billService.roomRecords(roomId));
    }

    @DeleteMapping("/record/delete/{id}")
    public Result deleteRecord(@PathVariable Long id, @RequestParam Long userId) {
        billService.deleteRecord(id, userId);
        return Result.success();
    }

    // ==================== 结算 ====================

    @GetMapping("/settle")
    public Result settle(@RequestParam Long roomId) {
        return Result.success(billService.settle(roomId));
    }
}
