<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <!-- 个人信息 -->
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="profile-header">
            <el-avatar :size="64" class="profile-avatar">{{ userInfo.username?.charAt(0) }}</el-avatar>
            <h3>{{ userInfo.username }}</h3>
            <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'success'">{{ userInfo.role }}</el-tag>
          </div>
          <el-descriptions :column="1" border style="margin-top:16px">
            <el-descriptions-item label="ID">{{ userInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ userInfo.role }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 我的内容 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <el-tabs v-model="activeTab">
            <!-- 我的攻略 -->
            <el-tab-pane label="📝 我的攻略" name="strategies">
              <el-table :data="myStrategies" stripe>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="city" label="城市" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
                      {{ statusMap[row.status] || row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160" />
              </el-table>
              <el-empty v-if="myStrategies.length === 0" description="暂无攻略" />
            </el-tab-pane>

            <!-- 我的路线 -->
            <el-tab-pane label="🛤️ 我的路线" name="routes">
              <el-table :data="myRoutes" stripe>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column label="路线" width="200">
                  <template #default="{ row }">
                    {{ row.startCity }} → {{ row.middleCity ? row.middleCity + ' → ' : '' }}{{ row.endCity }}
                  </template>
                </el-table-column>
                <el-table-column prop="transportType" label="交通方式" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
                      {{ statusMap[row.status] || row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160" />
              </el-table>
              <el-empty v-if="myRoutes.length === 0" description="暂无路线" />
            </el-tab-pane>

            <!-- 我的动态 -->
            <el-tab-pane label="⚡ 我的动态" name="notices">
              <el-table :data="myNotices" stripe>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" />
                <el-table-column prop="city" label="城市" width="100" />
                <el-table-column prop="category" label="分类" width="100" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
                      {{ statusMap[row.status] || row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160" />
              </el-table>
              <el-empty v-if="myNotices.length === 0" description="暂无动态" />
            </el-tab-pane>

            <!-- 我的搭子 -->
            <el-tab-pane label="👥 我的搭子" name="partners">
              <el-table :data="myPartners" stripe>
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="title" label="标题" />
                <el-table-column label="路线" width="160">
                  <template #default="{ row }">
                    {{ row.startCity }} → {{ row.targetCity }}
                  </template>
                </el-table-column>
                <el-table-column prop="departureDate" label="出发日期" width="120" />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'APPROVED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'">
                      {{ statusMap[row.status] || row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="createTime" label="发布时间" width="160" />
              </el-table>
              <el-empty v-if="myPartners.length === 0" description="暂无搭子" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getMyStrategies } from '../api/strategy'
import { getMyRoutes } from '../api/route'
import { getMyNotices } from '../api/notice'
import { getMyPartners } from '../api/partner'

const userInfo = computed(() => {
  try { return JSON.parse(localStorage.getItem('userInfo') || '{}') } catch { return {} }
})

const statusMap = { PENDING: '待审核', APPROVED: '已通过', REJECTED: '已拒绝' }
const activeTab = ref('strategies')
const myStrategies = ref([])
const myRoutes = ref([])
const myNotices = ref([])
const myPartners = ref([])

onMounted(async () => {
  if (!userInfo.value.id) return

  try {
    const res = await getMyStrategies(userInfo.value.id)
    myStrategies.value = res.data || []
  } catch (e) { console.error('加载我的攻略失败', e) }

  try {
    const res = await getMyRoutes(userInfo.value.id)
    myRoutes.value = res.data || []
  } catch (e) { console.error('加载我的路线失败', e) }

  try {
    const res = await getMyNotices(userInfo.value.id)
    myNotices.value = res.data || []
  } catch (e) { console.error('加载我的动态失败', e) }

  try {
    const res = await getMyPartners(userInfo.value.id)
    myPartners.value = res.data || []
  } catch (e) { console.error('加载我的搭子失败', e) }
})
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
}
.profile-header {
  text-align: center;
  padding: 20px;
}
.profile-avatar {
  background: #409eff;
  color: #fff;
  font-size: 24px;
  margin-bottom: 12px;
}
</style>
