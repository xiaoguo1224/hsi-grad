<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6" v-for="item in stats" :key="item.title">
        <el-card shadow="hover" class="stat-card">
          <el-statistic :title="item.title" :value="item.value" :precision="item.precision || 0">
            <template #suffix>
              <span :style="{ color: item.color }">{{ item.suffix }}</span>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="12">
        <el-card header="算法性能指标分析 (mAP@0.5 & F1)">
          <div ref="perfChart" style="height: 350px;"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card header="计算节点资源实时负载">
          <el-row :gutter="10">
            <el-col :span="12">
              <div class="monitor-item">
                <el-progress type="dashboard" :percentage="resources.gpuUsage" color="#67c23a">
                  <template #default>
                    <span class="p-value">{{ resources.gpuUsage }}%</span>
                    <span class="p-label">GPU 负载</span>
                  </template>
                </el-progress>
                <p class="res-info">显存占用: {{ resources.vram }} / 12GB</p>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="monitor-item">
                <el-progress type="dashboard" :percentage="resources.ramUsage" color="#409EFF">
                  <template #default>
                    <span class="p-value">{{ resources.ramUsage }}%</span>
                    <span class="p-label">系统内存</span>
                  </template>
                </el-progress>
                <p class="res-info">内存占用: {{ resources.ram }} / 32GB</p>
              </div>
            </el-col>
          </el-row>
          <el-divider content-position="left">存储与 IO 状态</el-divider>
          <div class="disk-status">
            <div class="disk-info">
              <span>HDF5 数据存储 (SSD)</span>
              <el-progress :percentage="82" status="warning"/>
            </div>
            <div class="disk-info">
              <span>系统响应延时 (Redis Cache)</span>
              <el-tag size="small" type="success">{{ resources.latency }}ms</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="mt-20">
      <el-col :span="24">
        <el-card header="高光谱数据推理任务队列">
          <el-table :data="taskQueue" stripe size="small">
            <el-table-column prop="id" label="任务ID" width="150"/>
            <el-table-column prop="name" label="数据源 (.mat)"/>
            <el-table-column prop="progress" label="推理进度">
              <template #default="scope">
                <el-progress :percentage="scope.row.progress"/>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === '完成' ? 'success' : 'primary'">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import {ref, onMounted, reactive} from 'vue';
import * as echarts from 'echarts';
import {getDashboardOverview, getSystemResources} from '@/api/dashboard';
import {ElMessage} from 'element-plus';

// 核心业务指标
const stats = ref([
  {title: 'HSOD-BIT 数据集立方体', value: 0, suffix: 'Cubes', color: '#409EFF', precision: 0},
  {title: 'ACEN 验证集 mAP@0.5', value: 0, precision: 1, suffix: '%', color: '#67C23A'},
  {title: 'F1-Score 目标识别率', value: 0, precision: 2, suffix: '', color: '#E6A23C'},
  {title: '单幅平均响应时间', value: 0, precision: 1, suffix: 's', color: '#F56C6C'}
]);

// 服务器资源实时数据
const resources = reactive({
  gpuUsage: 0,
  vram: '0GB',
  ramUsage: 0,
  ram: '0GB',
  latency: 0
});

// 任务队列
const taskQueue = ref([]);

const perfChart = ref(null);
let myChart = null;
let resourceTimer = null;

// 加载Dashboard数据
const loadDashboardData = async () => {
  try {
    const res = await getDashboardOverview();
    if (res.data) {
      const {stats: statsData, resources: resourcesData, performance: perfData, tasks} = res.data;

      // 更新统计数据
      if (statsData) {
        stats.value[0].value = statsData.totalCubes || 0;
        stats.value[1].value = statsData.mapAt05 || 0;
        stats.value[2].value = statsData.f1Score || 0;
        stats.value[3].value = statsData.avgResponseTime || 0;
      }

      // 更新资源数据
      if (resourcesData) {
        resources.gpuUsage = resourcesData.gpuUsage || 0;
        resources.vram = resourcesData.vram || '0GB';
        resources.ramUsage = resourcesData.ramUsage || 0;
        resources.ram = resourcesData.ram || '0GB';
        resources.latency = resourcesData.latency || 0;
      }

      // 更新性能图表
      if (perfData && myChart) {
        myChart.setOption({
          xAxis: {type: 'category', data: perfData.algorithms || []},
          series: [
            {name: 'mAP@0.5', type: 'bar', data: perfData.mapValues || [], color: '#67C23A'},
            {name: '推理延迟(s)', type: 'line', yAxisIndex: 1, data: perfData.latencyValues || [], color: '#F56C6C'}
          ]
        });
      }

      // 更新任务队列
      if (tasks && Array.isArray(tasks)) {
        taskQueue.value = tasks.map(task => {
          let status = '排队中';
          let progress = 0;
          if (task.status === 2) {
            status = '完成';
            progress = 100;
          } else if (task.status === 1) {
            status = '推理中';
            progress = 50;
          }
          return {
            id: task.taskId ? 'T' + task.taskId : 'T--',
            name: task.matFile?.fileName || task.taskName || '未知文件',
            progress: progress,
            status: status
          };
        });
      }
    }
  } catch (error) {
    console.error('加载Dashboard数据失败:', error);
    ElMessage.warning('使用本地模拟数据');
    loadFallbackData();
  }
};

// 加载后备模拟数据
const loadFallbackData = () => {
  stats.value = [
    {title: 'HSOD-BIT 数据集立方体', value: 319, suffix: 'Cubes', color: '#409EFF', precision: 0},
    {title: 'ACEN 验证集 mAP@0.5', value: 78.5, precision: 1, suffix: '%', color: '#67C23A'},
    {title: 'F1-Score 目标识别率', value: 0.72, precision: 2, suffix: '', color: '#E6A23C'},
    {title: '单幅平均响应时间', value: 2.8, precision: 1, suffix: 's', color: '#F56C6C'}
  ];

  Object.assign(resources, {
    gpuUsage: 45,
    vram: '5.4GB',
    ramUsage: 32,
    ram: '10.2GB',
    latency: 12
  });

  taskQueue.value = [
    {id: 'T2026020501', name: 'lake_scene_001.mat', progress: 100, status: '完成'},
    {id: 'T2026020502', name: 'industrial_bg_04.mat', progress: 45, status: '推理中'},
    {id: 'T2026020503', name: 'urban_exposure_12.mat', progress: 0, status: '排队中'}
  ];
};

// 定时刷新资源数据
const refreshResources = async () => {
  try {
    const res = await getSystemResources();
    if (res.data) {
      resources.gpuUsage = res.data.gpuUsage;
      resources.vram = res.data.vram;
      resources.ramUsage = res.data.ramUsage;
      resources.ram = res.data.ram;
      resources.latency = res.data.latency;
    }
  } catch (error) {
    resources.gpuUsage = Math.floor(Math.random() * 20) + 40;
    resources.latency = Math.floor(Math.random() * 5) + 10;
  }
};

onMounted(() => {
  // 初始化图表
  myChart = echarts.init(perfChart.value);
  myChart.setOption({
    tooltip: {trigger: 'axis'},
    legend: {data: ['mAP@0.5', '推理延迟(s)']},
    xAxis: {type: 'category', data: ['RX (基线)', '3D-CNN', 'ACEN (蒸馏)', 'YOLOv8s-Spe']},
    yAxis: [
      {type: 'value', name: '精度', max: 1},
      {type: 'value', name: '延迟', axisLabel: {formatter: '{value} s'}}
    ],
    series: [
      {name: 'mAP@0.5', type: 'bar', data: [0.55, 0.72, 0.79, 0.76], color: '#67C23A'},
      {name: '推理延迟(s)', type: 'line', yAxisIndex: 1, data: [0.5, 4.2, 2.8, 1.2], color: '#F56C6C'}
    ]
  });

  // 加载数据
  loadDashboardData();

  // 定时刷新资源（每3秒）
  resourceTimer = setInterval(refreshResources, 3000);
});

// 清理定时器
import {onUnmounted} from 'vue';
onUnmounted(() => {
  if (resourceTimer) {
    clearInterval(resourceTimer);
  }
  if (myChart) {
    myChart.dispose();
  }
});
</script>

<style scoped>
.dashboard {
  padding: 20px;
  background-color: #f5f7fa;
}

.mt-20 {
  margin-top: 20px;
}

.monitor-item {
  text-align: center;
}

.p-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
}

.p-label {
  font-size: 12px;
  color: #909399;
}

.res-info {
  font-size: 13px;
  color: #606266;
  margin-top: 5px;
}

.disk-status {
  padding: 10px;
}

.disk-info {
  margin-bottom: 10px;
  font-size: 14px;
}
</style>