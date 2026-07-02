package com.travelshare.service;

import com.travelshare.dto.BillRecordDTO;
import com.travelshare.dto.CreateRoomDTO;
import com.travelshare.dto.JoinRoomDTO;
import com.travelshare.entity.BillRecord;
import com.travelshare.entity.BillRoom;
import com.travelshare.entity.RoomMember;
import com.travelshare.vo.BillRecordVO;
import com.travelshare.vo.RoomMemberVO;
import com.travelshare.vo.SettleVO;

import java.util.List;

public interface BillService {

    // 房间操作
    BillRoom createRoom(CreateRoomDTO dto);
    void joinRoom(JoinRoomDTO dto);
    void approveMember(Long roomId, Long memberId, Long approverId);
    void rejectMember(Long roomId, Long memberId, Long approverId);
    List<BillRoom> myRooms(Long userId);
    BillRoom roomDetail(Long roomId);
    List<RoomMemberVO> roomMembers(Long roomId);

    // 账单操作
    BillRecord addRecord(BillRecordDTO dto);
    List<BillRecordVO> roomRecords(Long roomId);
    void deleteRecord(Long recordId, Long userId);

    // 统计结算
    SettleVO settle(Long roomId);
}
