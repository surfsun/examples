// 聊天功能JavaScript

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
    sendBtn.innerHTML = '<span class="loading"></span> 思考中...';

    // 发送请求
    fetch('/rag/chat', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'message=' + encodeURIComponent(message)
    })
        .then(response => response.text())
        .then(data => {
            // 显示AI回复
            addMessage(data, 'assistant');
        })
        .catch(error => {
            console.error('Error:', error);
            addMessage('抱歉，发生了错误：' + error.message, 'assistant');
        })
        .finally(() => {
            // 恢复发送按钮
            sendBtn.disabled = false;
            sendBtn.innerHTML = '发送';
        });
}

// 添加消息到聊天界面
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
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML.replace(/\n/g, '<br>');
}

// 页面加载完成后聚焦输入框
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('messageInput').focus();
});
