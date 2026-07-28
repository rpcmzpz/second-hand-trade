<template>
  <div class="messages-container">
    <h2>站内信</h2>
    <div class="messages-layout">
      <div class="conversation-sidebar">
        <div class="sidebar-header">
          <el-input v-model="searchKeyword" placeholder="搜索会话..." :prefix-icon="Search" clearable size="small" />
        </div>
        <div class="conversation-list" v-loading="convLoading">
          <div
            v-for="conv in filteredConversations"
            :key="conv.userId"
            class="conversation-item"
            :class="{ active: activeUserId === conv.userId }"
            @click="selectConversation(conv)"
          >
            <div class="conv-avatar">
              <el-avatar :size="44" :icon="UserFilled" />
              <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" class="unread-badge" />
            </div>
            <div class="conv-info">
              <div class="conv-top">
                <span class="conv-name">{{ conv.realName || conv.username }}</span>
                <span class="conv-time">{{ formatTime(conv.lastTime) }}</span>
              </div>
              <div class="conv-preview">{{ truncate(conv.lastContent, 30) }}</div>
            </div>
          </div>
          <el-empty v-if="filteredConversations.length===0 && !convLoading" description="暂无会话" :image-size="60" />
        </div>
      </div>

      <div class="chat-area">
        <div v-if="!activeUserId" class="chat-placeholder">
          <el-empty description="选择一个会话开始聊天" :image-size="80" />
        </div>
        <template v-else>
          <div class="chat-header">
            <span class="chat-partner-name">{{ activeUserName }}</span>
            <el-button link type="primary" size="small" @click="refreshMessages" :loading="msgLoading">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="chat-messages" ref="chatMessagesRef" v-loading="msgLoading" @scroll="onScroll">
            <div v-if="!msgLoading && messages.length === 0" class="chat-empty-hint">发送第一条消息开始对话吧~</div>
            <div
              v-for="(msg, idx) in messages"
              :key="idx"
              class="message-item"
              :class="{ mine: msg.senderId === currentUserId }"
            >
              <div class="message-bubble">
                {{ msg.content }}
                <span v-if="msg.senderId === currentUserId && msg.isRead === 1" class="read-tag">已读</span>
              </div>
              <div class="message-time">{{ formatTime(msg.createTime) }}</div>
            </div>
          </div>
          <div class="chat-input">
            <el-input
              v-model="inputText"
              placeholder="输入消息，Enter 发送..."
              :rows="2"
              type="textarea"
              resize="none"
              @keydown.enter.exact.prevent="handleSend"
            />
            <div class="input-actions">
              <span class="input-hint">Enter 发送</span>
              <el-button type="primary" :icon="Promotion" @click="handleSend" :loading="sending" size="small">发送</el-button>
            </div>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, UserFilled, Promotion, Refresh } from '@element-plus/icons-vue'
import { getConversations, getConversation, sendMessage } from '@/api/message'
import { getUserInfo } from '@/api/user'

const searchKeyword = ref('')
const convLoading = ref(false)
const msgLoading = ref(false)
const sending = ref(false)
const conversations = ref([])
const messages = ref([])
const activeUserId = ref(null)
const activeUserName = ref('')
const currentUserId = ref(null)
const inputText = ref('')
const chatMessagesRef = ref(null)
let refreshTimer = null

const filteredConversations = computed(() => {
  if (!searchKeyword.value) return conversations.value
  const kw = searchKeyword.value.toLowerCase()
  return conversations.value.filter(c =>
    (c.username || '').toLowerCase().includes(kw) ||
    (c.realName || '').toLowerCase().includes(kw)
  )
})

const formatTime = (t) => {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  if (d.toDateString() === now.toDateString()) {
    return pad(d.getHours()) + ':' + pad(d.getMinutes())
  }
  return (d.getMonth() + 1) + '/' + d.getDate() + ' ' + pad(d.getHours()) + ':' + pad(d.getMinutes())
}

const truncate = (text, max) => {
  if (!text) return ''
  return text.length > max ? text.substring(0, max) + '...' : text
}

const loadCurrentUser = async () => {
  try {
    const res = await getUserInfo()
    currentUserId.value = res.data.userId
  } catch {}
}

const fetchConversations = async () => {
  convLoading.value = true
  try {
    const res = await getConversations()
    conversations.value = res.data || []

    // keep selection if active user still exists
    if (activeUserId.value && !conversations.value.find(c => c.userId === activeUserId.value)) {
      activeUserId.value = null
      activeUserName.value = ''
      messages.value = []
    }
  } catch {} finally { convLoading.value = false }
}

const fetchMessages = async () => {
  if (!activeUserId.value) return
  msgLoading.value = true
  try {
    const res = await getConversation(activeUserId.value)
    messages.value = res.data || []
    await nextTick()
    scrollToBottom()
    // refresh conversation list to update unread counts
    fetchConversations()
  } catch {} finally { msgLoading.value = false }
}

const refreshMessages = () => {
  fetchMessages()
}

const selectConversation = (conv) => {
  activeUserId.value = conv.userId
  activeUserName.value = conv.realName || conv.username || '用户'
  fetchMessages()
}

const scrollToBottom = () => {
  const el = chatMessagesRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

const onScroll = () => {}

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || !activeUserId.value) return
  sending.value = true
  try {
    await sendMessage({ receiverId: activeUserId.value, content: text })
    inputText.value = ''
    // append local optimistically
    messages.value.push({
      senderId: currentUserId.value,
      receiverId: activeUserId.value,
      content: text,
      isRead: 0,
      createTime: new Date().toISOString()
    })
    await nextTick()
    scrollToBottom()
    // refresh real data
    await fetchMessages()
  } catch {} finally { sending.value = false }
}

const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    fetchConversations()
    if (activeUserId.value) fetchMessages()
  }, 10000)
}

onMounted(async () => {
  await loadCurrentUser()
  await fetchConversations()
  startAutoRefresh()

  // handle query param: auto-select user from product detail "contact seller"
  const route = useRoute()
  const queryUserId = route.query.userId
  if (queryUserId) {
    const conv = conversations.value.find(c => String(c.userId) === String(queryUserId))
    if (conv) {
      selectConversation(conv)
    }
  }
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
})
</script>

<style scoped>
.messages-container { padding: 20px 0; }
.messages-layout { display:flex; height:calc(100vh - 180px); border:1px solid #ebeef5; border-radius:6px; overflow:hidden; background:#fff }
.conversation-sidebar { width:300px; border-right:1px solid #ebeef5; display:flex;flex-direction:column; background:#fafafa }
.sidebar-header { padding:12px; border-bottom:1px solid #ebeef5 }
.conversation-list { flex:1; overflow-y:auto }
.conversation-item { display:flex; align-items:center; gap:12px; padding:12px 14px; cursor:pointer; border-bottom:1px solid #f0f0f0; transition:background .2s }
.conversation-item:hover { background:#f0f5ff }
.conversation-item.active { background:#e6f0ff }
.conv-avatar { position:relative; flex-shrink:0 }
.unread-badge { position:absolute; top:-6px; right:-6px }
.conv-info { flex:1; min-width:0; overflow:hidden }
.conv-top { display:flex; justify-content:space-between; align-items:center; margin-bottom:4px }
.conv-name { font-size:14px; font-weight:500; color:#303133 }
.conv-time { font-size:11px; color:#c0c4cc; flex-shrink:0 }
.conv-preview { font-size:12px; color:#909399; overflow:hidden; text-overflow:ellipsis; white-space:nowrap }
.chat-area { flex:1; display:flex; flex-direction:column }
.chat-placeholder { flex:1; display:flex; align-items:center; justify-content:center }
.chat-header { padding:12px 20px; border-bottom:1px solid #ebeef5; display:flex; justify-content:space-between; align-items:center }
.chat-partner-name { font-weight:600; font-size:15px; color:#303133 }
.chat-messages { flex:1; padding:16px 20px; overflow-y:auto; display:flex; flex-direction:column; gap:10px; background:#f5f7fa }
.chat-empty-hint { text-align:center; color:#c0c4cc; font-size:13px; margin-top:40px }
.message-item { display:flex; flex-direction:column; max-width:70% }
.message-item.mine { align-self:flex-end; align-items:flex-end }
.message-bubble { padding:10px 14px; border-radius:10px; background:#fff; word-break:break-all; font-size:14px; line-height:1.6; box-shadow:0 1px 2px rgba(0,0,0,0.06); position:relative }
.message-item.mine .message-bubble { background:#409eff; color:#fff; border-bottom-right-radius:4px }
.message-item:not(.mine) .message-bubble { border-bottom-left-radius:4px }
.read-tag { font-size:10px; color:rgba(255,255,255,0.7); margin-left:6px }
.message-time { font-size:11px; color:#c0c4cc; margin-top:3px; padding:0 4px }
.chat-input { padding:12px 16px; border-top:1px solid #ebeef5; background:#fff }
.input-actions { display:flex; justify-content:flex-end; align-items:center; gap:10px; margin-top:6px }
.input-hint { font-size:11px; color:#c0c4cc }
</style>
