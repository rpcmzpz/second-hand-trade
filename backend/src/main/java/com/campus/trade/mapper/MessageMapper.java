package com.campus.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.dto.ConversationSummaryDTO;
import com.campus.trade.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 一次查出当前用户的全部会话摘要（对端 ID + 最后一条消息内容/时间 + 未读条数）。
     *
     * <p>三个要点：
     * <ol>
     *   <li>{@code IF(sender_id = 我, receiver_id, sender_id)} 把「我发出」「我收到」两个方向
     *       归一化成同一个「对端 ID」，比 UNION 两次扫表再 GROUP BY 更省；</li>
     *   <li>{@code ROW_NUMBER() OVER (PARTITION BY 对端 ORDER BY 时间倒序)} 取 rn = 1，
     *       即每个会话的最后一条（同一时间再按 message_id 兜底，保证结果稳定）；</li>
     *   <li>未读数用 {@code SUM(...) OVER (PARTITION BY 对端)} 在同一趟扫描里顺带算出，
     *       不需要为每个会话再单独 count 一次。</li>
     * </ol>
     * 依赖 MySQL 8.0+ 的窗口函数。
     */
    @Select("""
            SELECT t.peer_id      AS peerId,
                   t.content      AS lastContent,
                   t.create_time  AS lastTime,
                   t.unread_count AS unreadCount
            FROM (
                SELECT IF(m.sender_id = #{userId}, m.receiver_id, m.sender_id) AS peer_id,
                       m.content,
                       m.create_time,
                       ROW_NUMBER() OVER (
                           PARTITION BY IF(m.sender_id = #{userId}, m.receiver_id, m.sender_id)
                           ORDER BY m.create_time DESC, m.message_id DESC
                       ) AS rn,
                       SUM(CASE WHEN m.receiver_id = #{userId} AND m.is_read = 0 THEN 1 ELSE 0 END)
                           OVER (PARTITION BY IF(m.sender_id = #{userId}, m.receiver_id, m.sender_id)) AS unread_count
                FROM message m
                WHERE m.sender_id = #{userId} OR m.receiver_id = #{userId}
            ) t
            WHERE t.rn = 1
              AND t.peer_id <> #{userId}
            ORDER BY t.create_time DESC
            """)
    List<ConversationSummaryDTO> selectConversationSummary(@Param("userId") Long userId);
}
