/* ========================================
   全局 JavaScript - SSE连接 + 通用函数
   ======================================== */

/**
 * 连接 SSE 消息日志流
 * @param {string} pattern - 模式名称（如 simple, work, fanout 等）
 * @param {string} containerId - 日志容器 DOM ID
 */
function connectSSE(pattern, containerId) {
  const container = document.getElementById(containerId);
  if (!container) return;

  const source = new EventSource("/api/sse/" + pattern);
  source.addEventListener("message-log", function (e) {
    const log = JSON.parse(e.data);
    appendLog(container, log);
    animateFlow(log.direction);
  });
  source.onerror = function () {
    console.warn("SSE 连接断开，3秒后重连...");
    source.close();
    setTimeout(() => connectSSE(pattern, containerId), 3000);
  };
}

/**
 * 追加日志到容器
 */
function appendLog(container, log) {
  // 清除空提示
  const empty = container.querySelector(".log-empty");
  if (empty) empty.remove();

  const item = document.createElement("div");
  item.className =
    "log-item " + (log.direction === "SENT" ? "sent" : "received");
  item.innerHTML = `
        <span class="log-badge ${log.direction === "SENT" ? "sent" : "received"}">
            ${log.direction === "SENT" ? "⬆ 发送" : "⬇ 接收"}
        </span>
        <span class="log-time">${log.timestamp}</span>
        ${log.source !== "Producer" ? '<span class="log-source">[' + log.source + "]</span>" : ""}
        <span class="log-text">${log.content}</span>
    `;
  container.appendChild(item);
  container.scrollTop = container.scrollHeight;
}

/**
 * 触发消息流向动画
 */
function animateFlow(direction) {
  const nodes = document.querySelectorAll(".flow-node");
  const arrows = document.querySelectorAll(".flow-arrow");

  if (direction === "SENT") {
    // 激活生产者
    nodes.forEach((n) => n.classList.remove("active"));
    arrows.forEach((a) => a.classList.remove("active"));

    const producer = document.querySelector(".flow-node.producer");
    if (producer) producer.classList.add("active");

    setTimeout(() => {
      arrows.forEach((a) => a.classList.add("active"));
      document
        .querySelectorAll(".flow-node.exchange, .flow-node.queue")
        .forEach((n) => n.classList.add("active"));
    }, 300);
  }
  if (direction === "RECEIVED") {
    document
      .querySelectorAll(".flow-node.consumer")
      .forEach((n) => n.classList.add("active"));
    setTimeout(() => {
      nodes.forEach((n) => n.classList.remove("active"));
      arrows.forEach((a) => a.classList.remove("active"));
    }, 2000);
  }
}

/**
 * 发送消息通用函数
 */
async function sendMsg(url, params) {
  const form = new URLSearchParams();
  Object.entries(params).forEach(([k, v]) => form.append(k, v));
  try {
    const res = await fetch(url, { method: "POST", body: form });
    if (res.ok) {
      showToast("消息发送成功", "success");
    } else {
      showToast("发送失败: " + res.statusText, "error");
    }
  } catch (e) {
    showToast("网络错误", "error");
  }
}

/**
 * 显示通知
 */
function showToast(message, type) {
  const toast = document.createElement("div");
  toast.className = "toast-notification " + type;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 2500);
}

/**
 * 清空日志
 */
function clearLogs(containerId) {
  const c = document.getElementById(containerId);
  if (c)
    c.innerHTML =
      '<div class="log-empty"><i class="fas fa-inbox"></i> 暂无消息，发送一条试试吧</div>';
}
