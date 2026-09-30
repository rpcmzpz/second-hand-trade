package com.campus.trade.dto;

import java.time.LocalDateTime;

/**
 * 会话摘要：会话列表里的一行。
 * 由 MessageMapper.selectConversationSummary 的聚合查询直接映射，
 * 用 DTO 而不是 Map 承接，避免弱类型取值和 JDBC 返回时间类型不确定的问题。
 */
public class ConversationSummaryDTO {

    /** 对端用户 ID */
    private Long peerId;

    /** 该会话最后一条消息的内容 */
    private String lastContent;

    /** 该会话最后一条消息的时间 */
    private LocalDateTime lastTime;

    /** 对端发给我、且我尚未读的消息条数 */
    private Long unreadCount;

    public Long getPeerId() { return peerId; }
    public void setPeerId(Long peerId) { this.peerId = peerId; }
    public String getLastContent() { return lastContent; }
    public void setLastContent(String lastContent) { this.lastContent = lastContent; }
    public LocalDateTime getLastTime() { return lastTime; }
    public void setLastTime(LocalDateTime lastTime) { this.lastTime = lastTime; }
    public Long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }
}
