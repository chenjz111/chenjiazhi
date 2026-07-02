package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.BillRecordDTO;
import com.travelshare.dto.CreateRoomDTO;
import com.travelshare.dto.JoinRoomDTO;
import com.travelshare.entity.*;
import com.travelshare.mapper.*;
import com.travelshare.service.BillService;
import com.travelshare.vo.MemberSummary;
import com.travelshare.vo.RoomMemberVO;
import com.travelshare.vo.SettleVO;
import com.travelshare.vo.TransferDetail;
import com.travelshare.vo.BillRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BillServiceImpl extends ServiceImpl<BillRoomMapper, BillRoom> implements BillService {

    @Autowired
    private BillRoomMapper billRoomMapper;

    @Autowired
    private RoomMemberMapper roomMemberMapper;

    @Autowired
    private BillRecordMapper billRecordMapper;

    @Autowired
    private BillSplitMapper billSplitMapper;

    @Autowired
    private UserMapper userMapper;

    private String generateRoomNumber() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public BillRoom createRoom(CreateRoomDTO dto) {
        // 生成唯一房间号
        String roomNumber;
        do {
            roomNumber = generateRoomNumber();
        } while (billRoomMapper.selectOne(
                new LambdaQueryWrapper<BillRoom>().eq(BillRoom::getRoomNumber, roomNumber)) != null);

        BillRoom room = new BillRoom();
        room.setRoomNumber(roomNumber);
        room.setName(dto.getName());
        room.setCreatorId(dto.getCreatorId());
        room.setStatus("ACTIVE");
        room.setCreateTime(LocalDateTime.now());
        billRoomMapper.insert(room);

        // 创建者自动成为成员
        RoomMember member = new RoomMember();
        member.setRoomId(room.getId());
        member.setUserId(dto.getCreatorId());
        member.setRole("CREATOR");
        member.setStatus("APPROVED");
        member.setJoinTime(LocalDateTime.now());
        roomMemberMapper.insert(member);

        return room;
    }

    @Override
    @Transactional
    public void joinRoom(JoinRoomDTO dto) {
        BillRoom room = billRoomMapper.selectOne(
                new LambdaQueryWrapper<BillRoom>().eq(BillRoom::getRoomNumber, dto.getRoomNumber()));
        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (!"ACTIVE".equals(room.getStatus())) {
            throw new RuntimeException("房间已关闭");
        }

        // 检查是否已经是成员
        RoomMember exist = roomMemberMapper.selectOne(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, room.getId())
                        .eq(RoomMember::getUserId, dto.getUserId()));
        if (exist != null) {
            if ("APPROVED".equals(exist.getStatus())) {
                throw new RuntimeException("您已是该房间成员");
            } else if ("PENDING".equals(exist.getStatus())) {
                throw new RuntimeException("您的加入申请正在等待审核");
            }
            // REJECTED 状态允许重新申请
            roomMemberMapper.deleteById(exist.getId());
        }

        RoomMember member = new RoomMember();
        member.setRoomId(room.getId());
        member.setUserId(dto.getUserId());
        member.setRole("MEMBER");
        member.setStatus("PENDING");
        member.setJoinTime(LocalDateTime.now());
        roomMemberMapper.insert(member);
    }

    @Override
    @Transactional
    public void approveMember(Long roomId, Long memberId, Long approverId) {
        // 验证审批人是否为创建者
        RoomMember approver = roomMemberMapper.selectOne(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, roomId)
                        .eq(RoomMember::getUserId, approverId));
        if (approver == null || !"CREATOR".equals(approver.getRole())) {
            throw new RuntimeException("只有房间创建者才能审批");
        }

        RoomMember member = roomMemberMapper.selectById(memberId);
        if (member == null || !member.getRoomId().equals(roomId)) {
            throw new RuntimeException("成员不存在");
        }
        member.setStatus("APPROVED");
        roomMemberMapper.updateById(member);
    }

    @Override
    @Transactional
    public void rejectMember(Long roomId, Long memberId, Long approverId) {
        RoomMember approver = roomMemberMapper.selectOne(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, roomId)
                        .eq(RoomMember::getUserId, approverId));
        if (approver == null || !"CREATOR".equals(approver.getRole())) {
            throw new RuntimeException("只有房间创建者才能审批");
        }

        RoomMember member = roomMemberMapper.selectById(memberId);
        if (member == null || !member.getRoomId().equals(roomId)) {
            throw new RuntimeException("成员不存在");
        }
        roomMemberMapper.deleteById(memberId);
    }

    @Override
    public List<BillRoom> myRooms(Long userId) {
        // 查询用户是成员的所有房间
        List<RoomMember> memberships = roomMemberMapper.selectList(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getUserId, userId)
                        .eq(RoomMember::getStatus, "APPROVED"));
        if (memberships.isEmpty()) return new ArrayList<>();

        List<Long> roomIds = memberships.stream().map(RoomMember::getRoomId).toList();
        return billRoomMapper.selectList(
                new LambdaQueryWrapper<BillRoom>()
                        .in(BillRoom::getId, roomIds)
                        .orderByDesc(BillRoom::getCreateTime));
    }

    @Override
    public BillRoom roomDetail(Long roomId) {
        BillRoom room = billRoomMapper.selectById(roomId);
        if (room == null) throw new RuntimeException("房间不存在");
        return room;
    }

    @Override
    public List<RoomMemberVO> roomMembers(Long roomId) {
        List<RoomMember> members = roomMemberMapper.selectList(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, roomId));

        List<RoomMemberVO> vos = new ArrayList<>();
        for (RoomMember member : members) {
            RoomMemberVO vo = new RoomMemberVO();
            BeanUtils.copyProperties(member, vo);
            // 查询用户名
            User user = userMapper.selectById(member.getUserId());
            vo.setUsername(user != null ? user.getUsername() : "用户" + member.getUserId());
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public BillRecord addRecord(BillRecordDTO dto) {
        // 验证是否为房间已通过成员
        RoomMember member = roomMemberMapper.selectOne(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, dto.getRoomId())
                        .eq(RoomMember::getUserId, dto.getPayerId())
                        .eq(RoomMember::getStatus, "APPROVED"));
        if (member == null) {
            throw new RuntimeException("您不是该房间成员");
        }

        BillRecord record = new BillRecord();
        record.setRoomId(dto.getRoomId());
        record.setPayerId(dto.getPayerId());
        record.setDescription(dto.getDescription());
        record.setAmount(dto.getAmount());
        record.setBillDate(dto.getBillDate());
        record.setImages(dto.getImages());
        record.setCreateTime(LocalDateTime.now());
        billRecordMapper.insert(record);

        // 保存分摊人员
        for (Long userId : dto.getSplitUserIds()) {
            BillSplit split = new BillSplit();
            split.setBillId(record.getId());
            split.setUserId(userId);
            billSplitMapper.insert(split);
        }

        return record;
    }

    @Override
    public List<BillRecordVO> roomRecords(Long roomId) {
        List<BillRecord> records = billRecordMapper.selectList(
                new LambdaQueryWrapper<BillRecord>()
                        .eq(BillRecord::getRoomId, roomId)
                        .orderByDesc(BillRecord::getBillDate));

        List<BillRecordVO> vos = new ArrayList<>();
        for (BillRecord record : records) {
            BillRecordVO vo = new BillRecordVO();
            BeanUtils.copyProperties(record, vo);
            // 加载分摊人员
            List<BillSplit> splits = billSplitMapper.selectList(
                    new LambdaQueryWrapper<BillSplit>().eq(BillSplit::getBillId, record.getId()));
            vo.setSplitUserIds(splits.stream().map(BillSplit::getUserId).toList());
            vos.add(vo);
        }
        return vos;
    }

    @Override
    @Transactional
    public void deleteRecord(Long recordId, Long userId) {
        BillRecord record = billRecordMapper.selectById(recordId);
        if (record == null) throw new RuntimeException("账单不存在");

        // 只有付款人或房间创建者可以删除
        BillRoom room = billRoomMapper.selectById(record.getRoomId());
        if (!record.getPayerId().equals(userId) && !room.getCreatorId().equals(userId)) {
            throw new RuntimeException("无权删除此账单");
        }

        // 删除分摊记录
        billSplitMapper.delete(
                new LambdaQueryWrapper<BillSplit>().eq(BillSplit::getBillId, recordId));
        billRecordMapper.deleteById(recordId);
    }

    @Override
    public SettleVO settle(Long roomId) {
        // 获取所有已通过成员
        List<RoomMember> members = roomMemberMapper.selectList(
                new LambdaQueryWrapper<RoomMember>()
                        .eq(RoomMember::getRoomId, roomId)
                        .eq(RoomMember::getStatus, "APPROVED"));
        if (members.isEmpty()) return new SettleVO();

        // 获取所有账单
        List<BillRecord> records = billRecordMapper.selectList(
                new LambdaQueryWrapper<BillRecord>().eq(BillRecord::getRoomId, roomId));

        // 计算每位成员的净余额 (正数=应收, 负数=应付)
        Map<Long, Double> balance = new HashMap<>();
        Map<Long, Double> paidMap = new HashMap<>();
        Map<Long, Double> shouldPayMap = new HashMap<>();

        for (RoomMember m : members) {
            balance.put(m.getUserId(), 0.0);
            paidMap.put(m.getUserId(), 0.0);
            shouldPayMap.put(m.getUserId(), 0.0);
        }

        double totalAmount = 0.0;

        for (BillRecord record : records) {
            totalAmount += record.getAmount();

            // 付款人已付金额增加
            paidMap.merge(record.getPayerId(), record.getAmount(), Double::sum);
            balance.merge(record.getPayerId(), record.getAmount(), Double::sum);

            // 获取分摊人员
            List<BillSplit> splits = billSplitMapper.selectList(
                    new LambdaQueryWrapper<BillSplit>().eq(BillSplit::getBillId, record.getId()));
            int splitCount = splits.size();
            if (splitCount == 0) continue;

            double perPerson = record.getAmount() / splitCount;
            for (BillSplit split : splits) {
                shouldPayMap.merge(split.getUserId(), perPerson, Double::sum);
                balance.merge(split.getUserId(), -perPerson, Double::sum);
            }
        }

        // 构建成员汇总
        List<MemberSummary> summaries = new ArrayList<>();
        for (RoomMember m : members) {
            User user = userMapper.selectById(m.getUserId());
            String username = user != null ? user.getUsername() : "用户" + m.getUserId();
            summaries.add(new MemberSummary(
                    m.getUserId(),
                    username,
                    paidMap.getOrDefault(m.getUserId(), 0.0),
                    shouldPayMap.getOrDefault(m.getUserId(), 0.0),
                    balance.getOrDefault(m.getUserId(), 0.0)
            ));
        }

        // 最优转账路径算法：贪心匹配最大应收和最大应付
        List<TransferDetail> transfers = calculateOptimalTransfers(balance, members);

        SettleVO vo = new SettleVO();
        vo.setTotalAmount(totalAmount);
        vo.setMemberSummaries(summaries);
        vo.setTransfers(transfers);
        return vo;
    }

    /**
     * 最优转账路径算法
     * 思路：每次找到最大应收者和最大应付者，两者之间转账 min(应收, 应付)，减少转账次数
     */
    private List<TransferDetail> calculateOptimalTransfers(Map<Long, Double> balance, List<RoomMember> members) {
        List<TransferDetail> transfers = new ArrayList<>();

        // 构建用户名映射
        Map<Long, String> usernameMap = new HashMap<>();
        for (RoomMember m : members) {
            User user = userMapper.selectById(m.getUserId());
            usernameMap.put(m.getUserId(), user != null ? user.getUsername() : "用户" + m.getUserId());
        }

        // 分离应收（正余额）和应付（负余额）
        List<Map.Entry<Long, Double>> creditors = new ArrayList<>(); // 应收
        List<Map.Entry<Long, Double>> debtors = new ArrayList<>();   // 应付

        for (Map.Entry<Long, Double> entry : balance.entrySet()) {
            double val = Math.round(entry.getValue() * 100.0) / 100.0; // 保留两位小数
            if (val > 0.01) {
                creditors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), val));
            } else if (val < -0.01) {
                debtors.add(new AbstractMap.SimpleEntry<>(entry.getKey(), -val));
            }
        }

        // 按金额排序（从大到小）
        creditors.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        debtors.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // 贪心匹配
        int i = 0, j = 0;
        while (i < creditors.size() && j < debtors.size()) {
            double creditAmount = creditors.get(i).getValue();
            double debtAmount = debtors.get(j).getValue();
            double transferAmount = Math.min(creditAmount, debtAmount);
            transferAmount = Math.round(transferAmount * 100.0) / 100.0;

            if (transferAmount > 0) {
                transfers.add(new TransferDetail(
                        debtors.get(j).getKey(),
                        usernameMap.get(debtors.get(j).getKey()),
                        creditors.get(i).getKey(),
                        usernameMap.get(creditors.get(i).getKey()),
                        transferAmount
                ));
            }

            creditAmount -= transferAmount;
            debtAmount -= transferAmount;

            creditors.get(i).setValue(Math.round(creditAmount * 100.0) / 100.0);
            debtors.get(j).setValue(Math.round(debtAmount * 100.0) / 100.0);

            if (creditAmount < 0.01) i++;
            if (debtAmount < 0.01) j++;
        }

        return transfers;
    }
}
