package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.entity.Message;
import com.campus.trade.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/send")
    public Result<Message> send(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long senderId = (Long) request.getAttribute("userId");
        Long receiverId = Long.valueOf(params.get("receiverId").toString());
        String content = (String) params.get("content");
        Message message = messageService.sendMessage(senderId, receiverId, content);
        return Result.success("发送成功", message);
    }

    @GetMapping("/conversation/{userId}")
    public Result<List<Message>> getConversation(@PathVariable Long userId, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        // mark received messages as read
        messageService.markAsRead(userId, currentUserId);
        List<Message> messages = messageService.getConversation(currentUserId, userId);
        return Result.success(messages);
    }

    @GetMapping("/conversations")
    public Result<List<Map<String, Object>>> getConversationList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageService.getConversationList(userId));
    }

    @GetMapping("/unread")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageService.getUnreadCount(userId));
    }
}
