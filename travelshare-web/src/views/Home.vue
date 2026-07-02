<template>
  <div class="home-page">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background:#409eff">📝</div>
          <div class="stat-value">{{ stats.strategyCount || 0 }}</div>
          <div class="stat-label">攻略数量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background:#67c23a">🛤️</div>
          <div class="stat-value">{{ stats.routeCount || 0 }}</div>
          <div class="stat-label">路线数量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background:#e6a23c">🔔</div>
          <div class="stat-value">{{ stats.noticeCount || 0 }}</div>
          <div class="stat-label">动态数量</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" style="background:#f56c6c">👥</div>
          <div class="stat-value">{{ stats.partnerCount || 0 }}</div>
          <div class="stat-label">搭子数量</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 智能预警提醒 -->
    <el-alert v-if="warnings.length > 0" type="warning" :closable="false" class="warning-alert">
      <template #title>
        ⚠️ 当前存在 {{ warnings.length }} 条实时预警动态，出行请注意！
      </template>
      <div v-for="w in warnings.slice(0,3)" :key="w.id" class="warning-item">
        {{ w.city }} - {{ w.title }}
      </div>
    </el-alert>

    <!-- 平台介绍 -->
    <el-card class="intro-card" shadow="hover">
      <h3>🌍 TravelShare 旅游共享平台</h3>
      <p class="intro-text">
        TravelShare 致力于解决旅游攻略时效性差和路线规划困难两大痛点。我们整合攻略分享、路线规划、实时旅游动态和智能预警，帮助游客出发前看攻略、出发中看实时动态、规划路线、降低踩坑风险。
      </p>
      <el-row :gutter="20" class="feature-row">
        <el-col :span="6">
          <div class="feature-item">
            <div class="feature-icon">📖</div>
            <div class="feature-title">攻略分享</div>
            <div class="feature-desc">真实用户旅游攻略</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-item">
            <div class="feature-icon">🗺️</div>
            <div class="feature-title">路线规划</div>
            <div class="feature-desc">多城市智能路线推荐</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-item">
            <div class="feature-icon">⚡</div>
            <div class="feature-title">实时动态</div>
            <div class="feature-desc">景区最新动态速递</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="feature-item">
            <div class="feature-icon">🚨</div>
            <div class="feature-title">智能预警</div>
            <div class="feature-desc">搜索即提醒，避免踩坑</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最新攻略 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>📝 最新攻略</span>
          <el-button text type="primary" @click="$router.push('/strategy')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="s in latestStrategies.slice(0,3)" :key="s.id">
          <el-card shadow="hover" class="preview-card" @click="$router.push('/strategy')">
            <div v-if="s.images" class="preview-img">
              <img :src="s.images.split(',')[0]" alt="" />
            </div>
            <h4>{{ s.title }}</h4>
            <el-tag size="small">{{ s.city }}</el-tag>
            <p class="preview-content">{{ (s.content || '').substring(0, 80) }}...</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 热门路线 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>🛤️ 热门路线</span>
          <el-button text type="primary" @click="$router.push('/route')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="r in latestRoutes.slice(0,3)" :key="r.id">
          <el-card shadow="hover" class="route-preview" @click="$router.push('/route')">
            <div class="route-path">
              <span class="city-node">{{ r.startCity }}</span>
              <span class="arrow">→</span>
              <span class="city-node" v-if="r.middleCity">{{ r.middleCity }}</span>
              <span class="arrow" v-if="r.middleCity">→</span>
              <span class="city-node">{{ r.endCity }}</span>
            </div>
            <div class="route-meta">
              <el-tag type="primary" size="small">{{ r.transportType }}</el-tag>
              <span class="meta-item">⏱ {{ r.duration }}</span>
              <span class="meta-item">💰 ¥{{ r.price }}</span>
            </div>
            <p class="preview-content">{{ (r.description || '').substring(0, 60) }}</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最新动态 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>⚡ 最新动态</span>
          <el-button text type="primary" @click="$router.push('/notice')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="n in latestNotices.slice(0,3)" :key="n.id">
          <el-card shadow="hover" class="preview-card">
            <div class="notice-category">
              <el-tag :type="getCategoryType(n.category)" size="small">{{ n.category }}</el-tag>
            </div>
            <h4>{{ n.title }}</h4>
            <p class="preview-content">{{ (n.content || '').substring(0, 80) }}...</p>
            <el-tag size="small" type="info">{{ n.city }}</el-tag>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <!-- 最新搭子 -->
    <el-card class="section-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>👥 最新搭子</span>
          <el-button text type="primary" @click="$router.push('/partner')">查看更多</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :span="8" v-for="p in latestPartners.slice(0,3)" :key="p.id">
          <el-card shadow="hover" class="preview-card" @click="$router.push('/partner')">
            <h4>{{ p.title }}</h4>
            <div class="partner-route">
              <span>{{ p.startCity }}</span>
              <span class="arrow">→</span>
              <span>{{ p.targetCity }}</span>
            </div>
            <div class="partner-meta">
              <span>📅 {{ p.departureDate }}</span>
              <span>👥 {{ p.currentPeople }}/{{ p.requiredPeople }}人</span>
            </div>
            <p class="preview-content">{{ (p.description || '').substring(0, 60) }}...</p>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getHomeStats } from '../api/admin'
import { getStrategyList } from '../api/strategy'
import { getNoticeList } from '../api/notice'
import { getRouteList } from '../api/route'
import { getPartnerList } from '../api/partner'

const stats = ref({})
const latestStrategies = ref([])
const latestNotices = ref([])
const latestRoutes = ref([])
const latestPartners = ref([])
const warnings = ref([])

const getCategoryType = (category) => {
  const map = { '景区关闭': 'danger', '交通异常': 'warning', '游客拥堵': 'warning', '临时活动': 'success' }
  return map[category] || 'info'
}

onMounted(async () => {
  try {
    const res = await getHomeStats()
    stats.value = res.data
  } catch (e) { console.error('加载统计数据失败', e) }

  try {
    const res = await getStrategyList()
    latestStrategies.value = (res.data || []).slice(0, 6)
  } catch (e) { console.error('加载攻略失败', e) }

  try {
    const res = await getRouteList()
    latestRoutes.value = (res.data || []).slice(0, 6)
  } catch (e) { console.error('加载路线失败', e) }

  try {
    const res = await getNoticeList()
    latestNotices.value = (res.data || []).slice(0, 6)
    warnings.value = (res.data || []).filter(n => n.category !== '临时活动').slice(0, 5)
  } catch (e) { console.error('加载动态失败', e) }

  try {
    const res = await getPartnerList()
    latestPartners.value = (res.data || []).slice(0, 6)
  } catch (e) { console.error('加载搭子失败', e) }
})
</script>

<style scoped>
.home-page {
  max-width: 1200px;
}
.stats-row {
  margin-bottom: 20px;
}
.stat-card {
  text-align: center;
  padding: 10px;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 8px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}
.stat-label {
  font-size: 13px;
  color: #999;
}
.warning-alert {
  margin-bottom: 20px;
}
.warning-item {
  font-size: 13px;
  color: #666;
  margin-top: 4px;
}
.intro-card {
  margin-bottom: 20px;
}
.intro-card h3 {
  margin-bottom: 12px;
}
.intro-text {
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
}
.feature-row {
  margin-top: 10px;
}
.feature-item {
  text-align: center;
  padding: 20px;
}
.feature-icon {
  font-size: 36px;
  margin-bottom: 8px;
}
.feature-title {
  font-weight: bold;
  margin-bottom: 4px;
}
.feature-desc {
  color: #999;
  font-size: 13px;
}
.section-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.preview-card {
  cursor: pointer;
  margin-bottom: 10px;
  transition: transform 0.2s;
}
.preview-card:hover {
  transform: translateY(-4px);
}
.preview-img {
  width: 100%;
  height: 140px;
  overflow: hidden;
  border-radius: 6px;
  margin-bottom: 8px;
}
.preview-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.preview-content {
  color: #999;
  font-size: 13px;
  margin-top: 8px;
}
.notice-category {
  margin-bottom: 8px;
}
.route-preview {
  cursor: pointer;
  margin-bottom: 10px;
  transition: transform 0.2s;
}
.route-preview:hover {
  transform: translateY(-4px);
}
.route-path {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  margin-bottom: 10px;
}
.city-node {
  background: #409eff;
  color: #fff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 14px;
}
.arrow {
  color: #999;
}
.route-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.meta-item {
  color: #666;
  font-size: 13px;
}
.partner-route {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}
.partner-meta {
  display: flex;
  gap: 16px;
  color: #666;
  font-size: 13px;
  margin-bottom: 4px;
}
</style>
