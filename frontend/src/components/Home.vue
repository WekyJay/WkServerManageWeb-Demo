<template>
  <div class="page">
    <el-card class="query-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>服务器状态查询</span>
          <el-button type="primary" @click="fetchInfo" :loading="loading">{{ loading ? '查询中' : '刷新状态' }}</el-button>
        </div>
      </template>

      <el-form :inline="true" label-width="140px" @submit.prevent>
        <el-form-item label="Host">
          <el-input v-model="host" placeholder="127.0.0.1" style="width: 220px" />
        </el-form-item>
        <el-form-item label="Port">
          <el-input-number v-model="port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="Connect Timeout (ms)">
          <el-input-number v-model="connTimeoutMs" :min="100" />
        </el-form-item>
        <el-form-item label="Read Timeout (ms)">
          <el-input-number v-model="readTimeoutMs" :min="100" />
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="status-card" shadow="always">
      <template #header>
        <div class="card-header">
          <span>状态详情</span>
          <el-tag v-if="serverInfo" :type="serverInfo.status === 'ok' ? 'success' : 'warning'" effect="dark">
            {{ serverInfo.status || '未知' }}
          </el-tag>
        </div>
      </template>

      <el-alert v-if="error" :title="error" type="error" show-icon closable class="mb12" />

      <el-empty v-if="!serverInfo && !error" description="暂无数据" />

      <div v-else-if="serverInfo" class="details">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="JSON">{{ serverInfo.JSON }}</el-descriptions-item>
          <el-descriptions-item label="在线人数">{{ serverInfo.online }}</el-descriptions-item>
          <el-descriptions-item label="最大人数">{{ serverInfo.maxPlayers }}</el-descriptions-item>
          <el-descriptions-item label="TPS">
            <el-tag :type="tpsTagType(serverInfo.tps)">{{ formatTps(serverInfo.tps) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="MOTD">{{ serverInfo.motd }}</el-descriptions-item>
          <el-descriptions-item label="版本">{{ serverInfo.version }}</el-descriptions-item>
          <el-descriptions-item label="服务端">{{ serverInfo.server }}</el-descriptions-item>
          <el-descriptions-item label="时间">{{ serverInfo.time ? formatTs(serverInfo.time) : '—' }}</el-descriptions-item>
        </el-descriptions>

        <div class="update-time">
          <span>最后更新时间：</span>
          <el-tag>{{ lastUpdated ? formatTs(lastUpdated) : '—' }}</el-tag>
        </div>
      </div>
    </el-card>

    <el-card class="exec-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>命令执行</span>
          <el-button type="success" @click="sendCommand" :loading="execLoading" :disabled="!cmdPayload">
            {{ execLoading ? '发送中' : '发送命令' }}
          </el-button>
        </div>
      </template>

      <el-form label-width="120px" @submit.prevent>
        <el-form-item label="命令内容">
          <el-input
            v-model="cmdPayload"
            type="textarea"
            :rows="3"
            placeholder="请输入要执行的命令，如：say hello"
          />
        </el-form-item>
      </el-form>

      <el-alert v-if="execError" :title="execError" type="error" show-icon closable class="mb12" />

      <div v-if="execResult" class="exec-result">
        <div class="result-header">
          <span>执行结果</span>
          <el-tag type="info">来自 /api/server/exec</el-tag>
        </div>
        <pre class="json-view">{{ toJson(execResult) }}</pre>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const host = ref('127.0.0.1')
const port = ref(5380)
const connTimeoutMs = ref(2000)
const readTimeoutMs = ref(5000)

const loading = ref(false)
const error = ref('')
const serverInfo = ref(null)
const lastUpdated = ref(null)
const cmdPayload = ref('')
const execLoading = ref(false)
const execError = ref('')
const execResult = ref(null)

function formatTs(ts) {
  try {
    const d = typeof ts === 'number' ? new Date(ts) : ts
    return d.toLocaleString()
  } catch (e) {
    return String(ts)
  }
}

async function fetchInfo() {
  loading.value = true
  error.value = ''
  serverInfo.value = null
  try {
    const q = new URLSearchParams({
      host: host.value,
      port: String(port.value),
      connTimeoutMs: String(connTimeoutMs.value),
      readTimeoutMs: String(readTimeoutMs.value),
    })
    const res = await fetch(`/api/server/info?${q.toString()}`)
    const json = await res.json()
    if (json && json.success) {
      serverInfo.value = json.data || null
      lastUpdated.value = Date.now()
    } else {
      error.value = (json && json.message) || '查询失败'
    }
  } catch (e) {
    error.value = `网络或服务错误：${e.message || e}`
  } finally {
    loading.value = false
  }
}

onMounted(fetchInfo)

function formatTps(tps) {
  if (tps == null) return '—'
  try { return Number(tps).toFixed(3) } catch { return String(tps) }
}

function tpsTagType(tps) {
  const n = Number(tps)
  if (Number.isNaN(n)) return 'info'
  if (n >= 19.5) return 'success'
  if (n >= 18) return 'warning'
  return 'danger'
}

async function sendCommand() {
  execLoading.value = true
  execError.value = ''
  execResult.value = null
  try {
    const q = new URLSearchParams({
      host: host.value,
      port: String(port.value),
      connTimeoutMs: String(connTimeoutMs.value),
      readTimeoutMs: String(readTimeoutMs.value),
    })
    const res = await fetch(`/api/server/exec?${q.toString()}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ payload: cmdPayload.value })
    })
    const json = await res.json()
    if (json && json.success) {
      execResult.value = json.data ?? null
    } else {
      execError.value = (json && json.message) || '执行失败'
    }
  } catch (e) {
    execError.value = `网络或服务错误：${e.message || e}`
  } finally {
    execLoading.value = false
  }
}

function toJson(obj) {
  try {
    return JSON.stringify(obj, null, 2)
  } catch (e) {
    return String(obj)
  }
}
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mb12 { margin-bottom: 12px; }
.update-time {
  margin-top: 12px;
}
.query-card, .status-card { min-width: 360px; }
.exec-card { min-width: 360px; }
.exec-result { margin-top: 8px; }
.result-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 6px; }
.json-view { background: #1e1f21; color: #eaeaea; padding: 12px; border-radius: 6px; overflow: auto; }
</style>