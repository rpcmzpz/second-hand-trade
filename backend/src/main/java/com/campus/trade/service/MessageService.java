package com.campus.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService extends IService<Message> {

    List<Message> getConversation(Long userId1, Long userId2);

    Message sendMessage(Long senderId, Long receiverId, String content);

    List<Map<String, Object>> getConversationList(Long userId);

    int getUnreadCount(Long userId);

    void markAsRead(Long senderId, Long receiverId);
}
