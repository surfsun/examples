// 联网搜索功能JavaScript (支持流式输出)

// 生成Session ID (如果不存在)
if (!sessionStorage.getItem('chat_session_id')) {
    sessionStorage.setItem('chat_session_id', 'web_' + Math.random().toString(36).substring(2, 15));
}
const sessionId = sessionStorage.getItem('chat_session_id');

// 发送消息
function sendMessage() {
    const input = document.getElementById('messageInput');
    const message = input.value.trim();

    if (!message) {
        alert('请输入问题');
        return;
    }

    // 显示用户消息
    addMessage(message, 'user');

    // 清空输入框
    input.value = '';

    // 禁用发送按钮
    const sendBtn = document.getElementById('sendBtn');
    sendBtn.disabled = true;
    sendBtn.innerHTML = '<span class="loading"></span> 搜索并思考中...';

    // 创建AI回复的消息框
    const messagesDiv = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message message-assistant';
    messageDiv.innerHTML = `<strong>AI助手：</strong><p id="currentStreamingMsg"></p>`;
    messagesDiv.appendChild(messageDiv);
    const contentP = messageDiv.querySelector('p');

    // 滚动到底部
    messagesDiv.scrollTop = messagesDiv.scrollHeight;

    // 使用 EventSource 或 fetch 流式读取
    // 这里由于是 GET 请求且带参数，我们直接使用 fetch 流式处理
    let fullContent = '';

    const url = `/api/web/chat?message=${encodeURIComponent(message)}&sessionId=${sessionId}`;

    const eventSource = new EventSource(url);

    eventSource.onmessage = function (event) {
        // EventSource 接收到的是字符串
        const data = event.data;
        fullContent += data;
        contentP.innerHTML = escapeHtml(fullContent);
        messagesDiv.scrollTop = messagesDiv.scrollHeight;
    };

    eventSource.onerror = function (err) {
        console.error('EventSource failed:', err);
        if (eventSource.readyState === EventSource.CLOSED) {
            // 正常结束（或者服务器关闭了连接）
        } else {
            // 报错了
            if (fullContent === '') {
                contentP.innerHTML = '<span style="color: red;">抱歉，发生了错误。请稍后重试。</span>';
            }
        }
        eventSource.close();

        // 恢复发送按钮
        sendBtn.disabled = false;
        sendBtn.innerHTML = '发送';
    };

    // 监听结束符 (如果后端发送了某种结束信号，但标准的 SSE 没这个，通常是后端关闭连接)
    // 我们在这里添加一个超时处理或者期待后端关闭
}

// 添加消息到聊天界面 (非流式使用)
function addMessage(content, role) {
    const messagesDiv = document.getElementById('chatMessages');
    const messageDiv = document.createElement('div');
    messageDiv.className = 'message message-' + role;

    const label = role === 'user' ? '您' : 'AI助手';

    messageDiv.innerHTML = `
        <strong>${label}：</strong>
        <p>${escapeHtml(content)}</p>
    `;

    messagesDiv.appendChild(messageDiv);

    // 滚动到底部
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

// HTML转义，防止XSS
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML.replace(/\n/g, '<br>');
}

// 页面加载完成后聚焦输入框
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('messageInput').focus();
});
