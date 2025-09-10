<script setup>
import 'boxicons/css/boxicons.min.css'
import { HomeFilled, VideoPlay, Upload,Cloudy } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { ref, watch, onMounted,computed } from 'vue'
import ChatAi from '@/views/coreFunctions/ChatAi.vue'
import { getBiliVideo } from '../../../services/video'
import { ElMessage, ElLoading } from 'element-plus'

const route = useRoute()
const router = useRouter()
const darkMode = ref(true)
const bvid = ref('')

const hasVideoId = computed(() => {
  return /^\/core\/video-details\/\w+/.test(route.path)
})
// 判断当前路由是否匹配
const isActive = (path) => {
  return route.path.startsWith(path)
}

const toUpload = () => {
  if (!hasVideoId.value) {
    ElMessage.warning('当前没有加载视频，无法跳转播放页面')
    return
  }
  router.push(route.path)
}
const toHome = () => {
  router.push('/home/home-page')
}

// 浅色/深色模式切换
watch(darkMode, (val) => {
  if (val) {
    document.documentElement.classList.add('dark-theme')
    localStorage.setItem('theme', 'dark')
  } else {
    document.documentElement.classList.remove('dark-theme')
    localStorage.setItem('theme', 'light')
  }
})

const toggleTheme = (isDark) => {
  darkMode.value = isDark !== undefined ? isDark : !darkMode.value
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    darkMode.value = true
    document.documentElement.classList.add('dark-theme')
  }
})

// 用户提交BV号
const handleSubmit = async () => {
  if (!bvid.value) {
    alert('请输入正确的BV号')
  }
  //加载动画
  const loading = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  try {
    const res = await getBiliVideo(bvid.value)
    const videoId = res.id
    bvid.value = ''
    router.push(`/core/video-details/${videoId}`)
  } catch (error) {
    console.log('获取视频失败，请检查BV号是否正确')
    return
  } finally {
    loading.close()
  }
}
</script>

<template>
  <div class="common-layout">
    <el-container>
      <!-- 侧边栏 -->
       <el-aside class="aside">
        <div class="top-icon">
          <a href="#">
            <ChatAi></ChatAi>
          </a>
        </div>
        <div class="bottom-icons">
          <div class="home" @click="toHome">
            <div class="nav-item" :class="{ active: isActive('/home') }">
              <el-icon size="20">
                <HomeFilled />
              </el-icon>
              <span class="nav-text">首页</span>
            </div>
          </div>
          <div class="upload" @click="toUpload">
            <div class="nav-item" :class="{ active: isActive('/core/video-details') }">
              <el-icon size="20">
                <VideoPlay />
              </el-icon>
              <span class="nav-text">播放</span>
            </div>
          </div>
        </div>
      </el-aside>
      <el-container class="content-container">
        <!-- 顶栏 -->
        <el-header class="header">
          <el-icon size="36"><Cloudy /></el-icon>
          <span class="cloudy">智课云</span>
          <!-- 搜索框 -->
          <div class="search-wrapper">
            <div class="search-container">
              <input
                class="search"
                v-model="bvid"
                @keyup.enter="handleSubmit"
                placeholder="请输入BV号"
                type="text"
              />
              <el-icon class="search-icon" @click="handleSubmit"><Upload /></el-icon>
            </div>
          </div>

          <!-- 主题切换和用户头像 -->
          <div class="right-section">
            <div class="theme-toggle">
              <i
                class="bx"
                :class="darkMode ? 'bx-sun' : 'bx-moon'"
                @click="toggleTheme()"
              ></i>
            </div>
            <img src="/src/assets/images/default.png" alt="user" />
          </div>
        </el-header>


        <!-- 中心内容 -->
        <el-main class="main"><router-view></router-view></el-main>
      </el-container>
    </el-container>
  </div>
</template>

<style lang="scss" scoped>
.common-layout {
  height: 100vh;
  overflow: hidden;
}

// 顶栏
.header {
  height: 46px;
  background-color: var(--top-bg-color);
  display: flex;
  align-items: center;
  // justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
  z-index: 10;
  position: relative;

  > div {
    position: relative;
    display: flex;
    align-items: center;
  }
}

.cloudy {
  font-weight: 500;
  font-size: 18px;
  margin-left: 4px;
}

.top-icon a{
  padding-left: 17px;
}

.search {
  width: 320px;
  height: 29px;
  margin-left: 125px;
  border: 2px solid var(--search-border-color);
  border-radius: 8px;
  background-color: var(--search-bg-color);
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease-in-out;
  padding: 0 30px 0 10px;

  &:hover:not(:focus) {
    border: 2px solid #bababa;
  }

  &:focus {
    background-color: #fff;
    border-color: #89b4ff;

    ~ .search-icon {
      color: #89b4ff;
    }
  }
}

.right-section {
  margin-left: 253px;
}

.theme-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-left: 0;

  i {
    font-size: 22px;  // 增大图标尺寸
    cursor: pointer;
    transition: all 0.3s ease;
    color: var(--icon-color);  // 使用主题变量控制颜色

    &:hover {
      opacity: 0.8;
      transform: scale(1.1);  // 悬停时轻微放大
      color: #ffc107;
    }

    &.bx-sun {
      color: #ffc107;  // 太阳图标使用黄色
    }

    &.bx-moon {
      color: #495057;  // 月亮图标使用深灰色
    }
  }
}

.search-wrapper {
  position: relative;
  width: 100%;
  max-width: 400px; // 可根据需要调整
  margin-left: 290px;
  margin-right: 240px;
}

.search-container {
  position: relative;
  display: flex;
  align-items: center;
}

.search-icon {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: #4a8bd4e2;
  cursor: pointer;
  transition: color 0.3s ease;
  font-size: 18px;
}

img {
  width: 30px;
  height: 30px;
  margin-left: 20px;
  cursor: pointer;
}

// 侧边栏
.aside {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  width: 98px;
  background-color: var(--aside-bg-color);
  border-right: 2px solid #f1f2f4;
  padding: 20px 10px;
  display: flex;
  flex-direction: column;
  z-index: 100;
}

.bottom-icons {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.nav-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  padding-left: 5px;
  padding: 6px;
  border-radius: 6px;
  transition: all 0.3s ease;

  .el-icon {
    color: #454648;
    margin-right: 6px;
    font-size: 22px;
    transition: color 0.3s ease;
  }

  .nav-text {
    color: #454648;
    font-size: 14px;
    white-space: nowrap;
    transition: color 0.3s ease;
    font-weight: 500;
  }

  &:hover {
    .el-icon, .nav-text {
      color: #4a8bd4e2;
      font-weight: 500;
    }
  }

  &.active {
    background-color: #dae8ff;
    .el-icon, .nav-text {
      color: #4a8bd4e2;
      font-weight: 500;
    }
  }
}

// 右侧内容容器
.content-container {
  margin-left: 98px; // 等于侧边栏宽度
  height: 100vh;
  display: flex;
  flex-direction: column;
}

// 主要区域
.main {
  background-color: var(--main-bg-color);
  overflow: auto;
  flex-grow: 1;
}
</style>
