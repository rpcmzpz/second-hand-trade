package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dto.ConversationSummaryDTO;
import com.campus.trade.entity.Message;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Message> getConversation(Long userId1, Long userId2) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w
                        .eq(Message::getSenderId, userId1).eq(Message::getReceiverId, userId2)
                        .or()
                        .eq(Message::getSenderId, userId2).eq(Message::getReceiverId, userId1)
        ).orderByAsc(Message::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Message sendMessage(Long senderId, Long receiverId, String content) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        this.save(message);
        return message;
    }

    @Override
    public List<Map<String, Object>> getConversationList(Long userId) {
        // 聚合查询：一次拿到所有会话的「对端 + 最后一条消息 + 未读数」
        // （原来的写法是「2 次查对端 + 每个对端 1 次查用户 + 1 次查最后一条 + 1 次查未读」，
        //   即 2 + 3N 次 SQL，会话越多越慢）
        List<ConversationSummaryDTO> summaries = baseMapper.selectConversationSummary(userId);
        if (summaries.isEmpty()) {
            return List.of();
        }

        // 批量取对端用户信息：一次 IN 查询，替代 N 次 selectById
        Set<Long> peerIds = summaries.stream()
                .map(ConversationSummaryDTO::getPeerId)
                .collect(Collectors.toSet());
        Map<Long, User> userMap = userMapper.selectBatchIds(peerIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        List<Map<String, Object>> result = new ArrayList<>(summaries.size());
        for (ConversationSummaryDTO s : summaries) {
            User user = userMap.get(s.getPeerId());
            if (user == null) {
                continue; // 对端账号已不存在，跳过该会话
            }
            Map<String, Object> item = new HashMap<>();
            item.put("userId", s.getPeerId());
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("avatar", user.getAvatar());
            item.put("lastContent", s.getLastContent() != null ? s.getLastContent() : "");
            item.put("lastTime", s.getLastTime());
            item.put("unreadCount", s.getUnreadCount() != null ? s.getUnreadCount().intValue() : 0);
            result.add(item);
        }
        // 排序（最后一条消息时间倒序）已由 SQL 的 ORDER BY 完成，无需再在内存里排
        return result;
    }

    @Override
    public int getUnreadCount(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId).eq(Message::getIsRead, 0);
        return Math.toIntExact(this.count(wrapper));
    }

    @Override
    public void markAsRead(Long senderId, Long receiverId) {
        // 一条 UPDATE 直接改（原来是「查出来 + 逐条 updateById」，未读 N 条就要 N+1 次 SQL）
        LambdaUpdateWrapper<Message> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Message::getSenderId, senderId)
                .eq(Message::getReceiverId, receiverId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1);
        this.update(wrapper);
    }
}
