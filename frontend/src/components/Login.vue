<template>
  <div class="dynamic-bg"></div>
  <div class="card login-card">
    <h2 v-if="needRegister">注册管理员账号</h2>
    <h2 v-else>登录</h2>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label>用户名</label>
        <input v-model="form.username" required autocomplete="username" />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input v-model="form.password" type="password" required autocomplete="current-password" />
      </div>
      <button class="submit-btn" type="submit">{{ needRegister ? '注册' : '登录' }}</button>
      <button class="submit-btn" type="button" @click="goHome">直接进入首页</button>
    </form>
    <transition name="fade">
      <div v-if="errorMsg" class="error-msg">{{ errorMsg }}</div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const form = ref({ username: '', password: '' })
const needRegister = ref(false)
const errorMsg = ref('')

// 模拟API请求，实际请替换为真实接口
async function checkHasAccount() {
  // 这里应请求后端接口判断是否有账号
  // 例：const res = await fetch('/api/hasAccount')
  // needRegister.value = !(await res.json()).hasAccount
  needRegister.value = false // TODO: 替换为真实逻辑
}

async function handleSubmit() {
  errorMsg.value = ''
  if (needRegister.value) {
    // 注册管理员逻辑
    // const res = await fetch('/api/register', { method: 'POST', body: JSON.stringify(form.value) })
    // if (res.ok) ...
    errorMsg.value = '注册功能待接入后端'
  } else {
    // 登录逻辑
    // const res = await fetch('/api/login', { method: 'POST', body: JSON.stringify(form.value) })
    // if (res.ok) ...
    errorMsg.value = '登录功能待接入后端'
  }
  // 无论是否已输入账号密码，转跳到首页
  goHome()
}

onMounted(checkHasAccount)

const router = useRouter()
function goHome() {
  // 使用前端路由跳转到首页，无需整页刷新
  router.push('/home')
}
</script>

<style scoped>
.login-card {
  margin-top: 10vh;
  min-width: 320px;
  min-height: 340px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}
.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 1.2rem;
}
.form-group label {
  color: #bbb;
  margin-bottom: 0.4rem;
  font-size: 1rem;
}
.form-group input {
  padding: 0.7rem 1rem;
  border-radius: 8px;
  border: none;
  background: #232526;
  color: #fff;
  font-size: 1.1rem;
  outline: none;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.10);
  transition: background 0.2s;
}
.form-group input:focus {
  background: #282828;
}
.submit-btn {
  width: 100%;
  padding: 0.8rem 0;
  border-radius: 8px;
  border: none;
  background: linear-gradient(90deg, #232526 0%, #414345 100%);
  color: #fff;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  margin-top: 0.5rem;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.10);
  transition: background 0.2s;
}
.submit-btn:hover {
  background: linear-gradient(90deg, #414345 0%, #232526 100%);
}
.error-msg {
  color: #ff6b6b;
  margin-top: 1rem;
  font-size: 1rem;
  text-align: center;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.4s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>