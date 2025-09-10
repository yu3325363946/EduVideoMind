<script setup>
import { onMounted, ref, nextTick } from 'vue'
import { UploadFilled,Refresh } from '@element-plus/icons-vue'
import { uploadVideo, getVideoList } from '../../../services/video'
import { ElMessage, ElLoading } from 'element-plus'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'

import banner1 from '@/assets/images/img-1.jpg'
import banner2 from '@/assets/images/img-2.jpg'
import banner3 from '@/assets/images/img-3.jpg'
// import banner4 from '@/assets/images/img-4.jpg'

const router = useRouter()
const showTitleDialog = ref(false)
const selectedFile = ref(null)
const videoTitle = ref('')
const videoList = ref([])
// 图表
const pieChartRef = ref(null)
const lineChartRef = ref(null)
// 幸运签和对应备忘录数据
const currentDate = ref('')
const memoSignData = ref([
  {
    sign: "大吉",
    memos: [
      "好奇心和尝试是青春最好的燃料，点燃它们，去探索、去跌倒、然后笑着站起来继续前进。",
      "今天的好运就像清晨的阳光，温暖而不刺眼，把握机会，让美好发生。"
    ]
  },
  {
    sign: "中吉",
    memos: [
      "你不是在追赶别人的脚步，而是在编织自己独一无二的故事——每一章都值得用心书写。",
      "平稳的日子也有惊喜，保持开放的心态，幸运自会降临。"
    ]
  },
  {
    sign: "吉",
    memos: [
      "愿你活成一颗种子，静默地积蓄力量有朝一日破土而出，惊艳时光",
      "“失之东隅,收之桑榆”回头看，轻舟已过万重山；向前看，前路漫漫亦灿灿"
    ]
  }
])

const updateDate = () => {
  const now = new Date()
  currentDate.value = `${now.getMonth() + 1}月${now.getDate()}日`
}

// 当前显示的签和备忘录
const currentSign = ref(memoSignData.value[0].sign)
const currentMemo = ref(memoSignData.value[0].memos[0])

// 随机切换签和备忘录
const randomMemo = () => {
  const currentIndex = memoSignData.value.findIndex(item => item.sign === currentSign.value)
  let randomIndex

  // 确保不重复当前签
  do {
    randomIndex = Math.floor(Math.random() * memoSignData.value.length)
  } while (randomIndex === currentIndex && memoSignData.value.length > 1)

  const selectedSign = memoSignData.value[randomIndex]
  currentSign.value = selectedSign.sign

  // 随机选择该签对应的两条备忘录之一
  const memoIndex = Math.floor(Math.random() * selectedSign.memos.length)
  currentMemo.value = selectedSign.memos[memoIndex]
}

// 直接初始化饼图（不需要等待DOM挂载）
const initChart = () => {
  const myChart = echarts.init(pieChartRef.value)
  myChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)' // 显示百分比
    },
    legend: {
      // 图例
      show: false,
    },
    series: [{
      name: '能力分布',
      type: 'pie',
      radius: ['30%', '65%'],
      center: ['50%', '45%'],
      padAngle: 5,
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 7,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: true, // 显示标签
        formatter: '{b|{b}}\n{d|{d}%}', // 自定义格式
        rich: {
          b: {
            fontSize: 14,
            lineHeight: 20
          },
          d: {
            fontSize: 16,
            color: '#999',
            padding: [0, 0, 5, 0]
          }
        },
        position: 'outside', // 标签显示在外部
        alignTo: 'labelLine', // 对齐引导线
        edgeDistance: '10%'
      },
      labelLine: {
        show: true, // 显示引导线
        length: 10, // 第一段线长度
        length2: 20, // 第二段线长度
        smooth: 0.2 // 平滑度
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 18,
          fontWeight: 'bold'
        }
      },
      data: [
        { value: 20, name: '思考' },
        { value: 35, name: '经验' },
        { value: 35, name: '知识' },
        { value: 10, name: '好奇心' }
      ]
    }]
  })
  window.addEventListener('resize', () => myChart.resize())
}

// 初始化折线图
const initLineChart = () => {
  const myChart = echarts.init(lineChartRef.value)
  myChart.setOption({
    grid: {
      top: '15%',     // 距离容器顶部 15%
      right: '5%',    // 距离容器右侧 5%
      bottom: '15%',  // 距离容器底部 20%（调整这个值可以增加/减少图表高度）
      left: '10%',    // 距离容器左侧 10%
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: [1, 3, 2.5, 1.5, 2, 4, 4.5],
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      }
    }]
  })
  window.addEventListener('resize', () => myChart.resize())
}



const carouselItems = ref([
  {
    id: 1,
    imageUrl: banner1,
  },
  {
    id: 2,
    imageUrl: banner2,
  },
  {
    id: 3,
    imageUrl: banner3,
  },
  // {
  //   id: 4,
  //   imageUrl: banner4,
  //   title: '轮播图标题4'
  // }
])


// 点击视频跳转
const handleVideoClick = (videoId) => {
  console.log('点击到的视频id:',videoId);
  router.push(`/core/video-details/${videoId}`)
}

// 选中文件时保存到 selectedFile
const beforeUpload = (file) => {
  selectedFile.value = file
  showTitleDialog.value = true
  return false // 阻止 el-upload 自动上传
}

// 获取首页默认视频列表
onMounted(async () => {
  initChart()
  initLineChart()
  updateDate()
  const res = await getVideoList()
  console.log('存入的数据：', res)
  videoList.value = res
})

//上传视频
const confirmUpload = async () => {
  if (!videoTitle.value.trim()) {
    ElMessage.warning('请输入标题')
    return
  }

  //加载动画
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })

  try {
    const data = await uploadVideo(selectedFile.value, videoTitle.value)
    console.log('[DEBUG] data = ', data)

    if (data.code === 200) {
      ElMessage.success(`上传成功，ID=${data.videoId}`)
      // 延迟关闭 loading 并跳转，避免 loading 一闪而过
        router.push(`/core/video-details/${data.videoId}`)
    } else {
      ElMessage.error(`上传失败：${data.msg}`)
    }
  } catch (err) {
    // 只有真正的网络错误才进来
    console.error('上传异常', err)
    // ElMessage.error('上传失败，请检查网络或服务器状态')
  } finally {
    loading.close()
    showTitleDialog.value = false
    videoTitle.value = ''
    selectedFile.value = null
  }
}
</script>

<template>
  <div class="video-container">
    <!-- 轮播图和视频列表区域 -->
    <div class="main-content">
      <!-- 轮播图 -->
<el-carousel trigger="click" height="450px" class="custom-carousel">
  <el-carousel-item
    v-for="item in carouselItems"
    :key="item.id"
    class="carousel-item"
  >
    <el-image
      :src="item.imageUrl"
      fit="cover"
      style="width: 100%; height: 100%"
    />
  </el-carousel-item>
</el-carousel>

      <!-- 右侧三个视频 -->
      <div class="video-grid">
        <el-row :gutter="20" class="video-list">
          <el-col v-for="(video, index) in videoList.slice(0, 3)" :key="video.videoId" :span="12">
            <el-card
              :body-style="{ padding: '0px', cursor: 'pointer' }"
              shadow="hover"
              @click="handleVideoClick(video.videoId)"
            >
              <!-- 封面图 -->
              <div class="cover-container">
                <el-image :src="video.coverUrl" fit="cover" style="width: 300px; height: 160px" />
                <!-- 播放时长 -->
                <div class="cover-info">
                  <span class="duration">{{ video.duration }}</span>
                </div>
              </div>

              <!-- 视频信息 -->
              <div style="padding: 10px">
                <div class="video-title" :title="video.title">{{ video.title }}</div>
              </div>
            </el-card>
          </el-col>
          <!-- 拖拽上传 -->
        <el-col :span="6">
          <el-upload
            class="upload-demo"
            accept="video/*"
            limit="1"
            :before-upload="beforeUpload"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">拖拽视频到此处<em>点击上传</em></div>
          </el-upload>
        </el-col>
        </el-row>
      </div>
    </div>

    <!-- 其他视频列表 -->
    <div class="other-videos">
        <el-col v-for="video in videoList.slice(3)" :key="video.videoId" :span="6">
          <el-card
            :body-style="{ padding: '0px', cursor: 'pointer' }"
            shadow="hover"
            @click="handleVideoClick(video.videoId)"
          >
            <!-- 封面图 -->
            <div class="cover-container">
              <el-image :src="video.coverUrl" fit="cover" style="width: 100%; height: 160px" />
              <!-- 播放时长 -->
              <div class="cover-info">
                <span class="duration">{{ video.duration }}</span>
              </div>
            </div>

            <!-- 视频信息 -->
            <div style="padding: 10px">
              <div class="video-title" :title="video.title">{{ video.title }}</div>
            </div>
          </el-card>
        </el-col>
    </div>

    <!-- 图表 -->
  <div class="charts-container">
  <div class="chart-container">
    <h2>智慧:</h2>
    <div ref="pieChartRef"></div>
  </div>
  <div class="chart-container">
    <div ref="lineChartRef"></div>
    <p>您今天比昨天多学习了<span>30分钟</span></p>
  </div>
<!-- 幸运签 -->
<div class="memo-container">
  <el-card shadow="hover">
    <span class="sign-title">幸运签</span>
     <span class="sign-date">{{ currentDate }}</span>
    <hr>
    <div class="memo-content">
      <p style="font-size: 20px">今日:</p>
      <span class="memo-title">{{ currentSign }}</span>
      <div class="memo-text">{{ currentMemo }}</div>
      <el-button
        type="primary"
        circle
        @click="randomMemo"
        class="refresh-btn"
      >
        <el-icon><Refresh /></el-icon>
      </el-button>
    </div>
  </el-card>
</div>
</div>
</div>
  <!-- 对话框 -->
  <el-dialog v-model="showTitleDialog" title="请输入视频标题" width="400px">
    <el-input v-model="videoTitle" placeholder="请输入视频标题" />
    <template #footer>
      <el-button @click="showTitleDialog = false">取消</el-button>
      <el-button type="primary" @click="confirmUpload">确认上传</el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.el-loading-mask {
  z-index: 3000 !important;
}

.video-container {
  margin: 0 auto;

.main-content {
  display: flex;
  margin-bottom: 30px;
  gap: 20px;
  width: 100%;
}

  .carousel-section {
    width: 600px;
    flex-shrink: 0;
  }

  .video-grid {
    flex: 1;
  }

  .other-videos {
    width: 100%;
  }

  .video-list {
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .cover-container {
    position: relative;
    border-radius: 8px 8px 0 0;
    overflow: hidden;

    .cover-info {
      position: absolute;
      bottom: 5px;
      left: 0;
      right: 0;
      padding: 0 10px;
      color: white;
      font-size: 12px;
      background: linear-gradient(transparent, rgba(0, 0, 0, 0.5));

      .duration {
        display: flex;
        float: right;
      }
    }
  }

  .video-title {
    font-size: 14px;
    color: #333;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    line-height: 1.4;
  }

  :deep(.el-upload-dragger) {
    width: 306px;
    height: 216px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    border-radius: 9px;
  }

  :deep(.el-card) {
    border-radius: 9px;
    overflow: hidden;
    margin-bottom: 20px;
  }
}

// 轮播图
.custom-carousel {
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
}

.carousel-item {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}
// 图表
.chart-container {
  width: 450px;
  height: 350px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

  // 确保容器有明确高度
  & > div {
    width: 100%;
    height: 290px;
    min-height: 300px;
  }
  p {
    margin-left: 114px;
  }
  span {
    font-size: 22px;
    font-weight: 500;
    color: #409EFF;
  }
}
h2 {
  margin-left: 20px;
  padding-top: 20px;
}
.charts-container {
  display: flex;
  gap: 20px;
  margin: 0 auto;
}
// 备忘录样式
.memo-container {
  width: 450px;
  .sign-title {
    font-size: 28px;
    margin-right: 253px;
  }
  hr {
    margin-top: 4px;
  }
  .memo-content {
    height: 262px;
    align-items: center;
    justify-content: space-between;
    padding: 20px 20px 0 0;

    span {
      font-size: 36px;
      font-weight: 500;
    }
    .memo-text {
      flex: 1;
      margin-top: 20px;
      font-size: 18px;
      line-height: 1.6;
      color: #333;
    }
    .refresh-btn {
      flex-shrink: 0;
      width: 40px;
      height: 40px;
      font-size: 24px;
      margin-top: 45px;
      display: flex;
      float: right;
      margin-right: -18px;
    }
  }
}
.sign-date {
    font-size: 16px;
  }
</style>
