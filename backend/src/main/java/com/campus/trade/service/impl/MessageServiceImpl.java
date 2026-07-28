package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.entity.Message;
import com.campus.trade.entity.User;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
        // find all distinct users I've communicated with
        LambdaQueryWrapper<Message> sentWrapper = new LambdaQueryWrapper<>();
        sentWrapper.eq(Message::getSenderId, userId).select(Message::getReceiverId);
        List<Object> sentList = this.listObjs(sentWrapper);

        LambdaQueryWrapper<Message> recvWrapper = new LambdaQueryWrapper<>();
        recvWrapper.eq(Message::getReceiverId, userId).select(Message::getSenderId);
        List<Object> recvList = this.listObjs(recvWrapper);

        Set<Long> userIds = new HashSet<>();
        sentList.forEach(o -> userIds.add((Long) o));
        recvList.forEach(o -> userIds.add((Long) o));
        userIds.remove(userId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Long uid : userIds) {
            User user = userMapper.selectById(uid);
            if (user == null) continue;

            // get last message in this conversation
            LambdaQueryWrapper<Message> lastWrapper = new LambdaQueryWrapper<>();
            lastWrapper.and(w -> w
                    .eq(Message::getSenderId, userId).eq(Message::getReceiverId, uid)
                    .or()
                    .eq(Message::getSenderId, uid).eq(Message::getReceiverId, userId)
            ).orderByDesc(Message::getCreateTime).last("LIMIT 1");
            Message lastMsg = this.getOne(lastWrapper);

            // count unread from this user to me
            LambdaQueryWrapper<Message> unreadWrapper = new LambdaQueryWrapper<>();
            unreadWrapper.eq(Message::getSenderId, uid)
                    .eq(Message::getReceiverId, userId)
                    .eq(Message::getIsRead, 0);
            long unreadCount = this.count(unreadWrapper);

            Map<String, Object> item = new HashMap<>();
            item.put("userId", uid);
            item.put("username", user.getUsername());
            item.put("realName", user.getRealName());
            item.put("avatar", user.getAvatar());
            item.put("lastContent", lastMsg != null ? lastMsg.getContent() : "");
            item.put("lastTime", lastMsg != null ? lastMsg.getCreateTime() : null);
            item.put("unreadCount", unreadCount);

            result.add(item);
        }

        // sort by last message time descending
        result.sort((a, b) -> {
            LocalDateTime ta = (LocalDateTime) a.get("lastTime");
            LocalDateTime tb = (LocalDateTime) b.get("lastTime");
            if (ta == null && tb == null) return 0;
            if (ta == null) return 1;
            if (tb == null) return -1;
            return tb.compareTo(ta);
        });

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
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getSenderId, senderId)
                .eq(Message::getReceiverId, receiverId)
                .eq(Message::getIsRead, 0);
        List<Message> unreadList = this.list(wrapper);
        for (Message msg : unreadList) {
            msg.setIsRead(1);
            this.updateById(msg);
        }
    }
}
