<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="sidebar">
      <div class="logo">
        <span class="logo-icon">✈️</span>
        <span class="logo-text">TravelShare</span>
      </div>
      <el-menu router :default-active="activeMenu" class="sidebar-menu">
        <el-menu-item index="/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/strategy">
          <el-icon><Document /></el-icon>
          <span>旅游攻略</span>
        </el-menu-item>
        <el-menu-item index="/route">
          <el-icon><Route /></el-icon>
          <span>路线规划</span>
        </el-menu-item>
        <el-menu-item index="/notice">
          <el-icon><Bell /></el-icon>
          <span>城市动态</span>
        </el-menu-item>
        <el-menu-item index="/partner">
          <el-icon><User /></el-icon>
          <span>旅游搭子</span>
        </el-menu-item>
        <el-menu-item index="/bill">
          <span style="margin-right:8px">💰</span>
          <span>旅游记账本</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin">
          <el-icon><Setting /></el-icon>
          <span>后台管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">TravelShare 旅游共享平台</div>
        <div class="header-right">
          <span class="username">{{ userInfo.username }}</span>
          <el-dropdown>
            <el-avatar :size="32" class="avatar">{{ userInfo.username?.charAt(0) }}</el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const userInfo = computed(() => {
  try {
    return JSON.parse(localStorage.getItem('userInfo') || '{}')
  } catch {
    return {}
  }
})

const isAdmin = computed(() => userInfo.value.role === 'ADMIN')

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
}
.sidebar {
  background: #304156;
  overflow: hidden;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.logo-icon {
  font-size: 24px;
  margin-right: 8px;
}
.sidebar-menu {
  border-right: none;
  background: #304156;
}
.sidebar-menu .el-menu-item {
  color: #bfcbd9;
}
.sidebar-menu .el-menu-item.is-active {
  background: #1890ff;
  color: #fff;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  padding: 0 20px;
}
.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  color: #666;
  font-size: 14px;
}
.avatar {
  cursor: pointer;
  background: #409eff;
  color: #fff;
}
.main-content {
  background: #f0f2f5;
  min-height: calc(100vh - 60px);
  padding: 20px;
}
</style>