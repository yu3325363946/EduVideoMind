<script setup>
import NoteChart from './NoteChart.vue'
import { getNotes,exportWord } from '../../../services/video'
import { ref, onMounted } from 'vue'
import { Download,Back } from '@element-plus/icons-vue'
import { ElLoading, ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const props = defineProps({
  videoId: String
})

// 分别存储笔记内容和目录数据
const notesData = ref([])
const directoryData = ref([])
const isDirectoryOpen = ref(false)
const globalLoading = ref(true)
//用于判断两个部分是否都加载完
const chartLoaded = ref(false)
const notesLoaded = ref(false)
const router = useRouter()

let loadingInstance = null

const goBack = () => {
  router.back()
}

onMounted(async () => {
  loadingInstance = ElLoading.service({
    lock: true,
    text: 'Loading',
    background: 'rgba(0, 0, 0, 0.7)',
  })
  try {
    console.log('笔记中拿到的videoId:',props.videoId);
    const res = await getNotes(props.videoId)
    console.log('拿到的接口的数据:',res);
    if (res.code === 200) {
      // 同时保存笔记内容和目录
      notesData.value = res.notes
      directoryData.value = res.directory
      onNotesLoaded()
    } else {
      console.error('请求返回失败', res.message)
    }
  } catch (error) {
    console.error('生成笔记错误', error)
  }
})

const exportToWord = async () => {
  let loading = null
  try {
    loading = ElLoading.service({
      lock: true,
      text: '正在生成Word文档...',
      background: 'rgba(0, 0, 0, 0.7)',
    })

    const res = await exportWord(props.videoId)

    // 确保响应是Blob类型
    const blob = res instanceof Blob
      ? res
      : new Blob([res], {
          type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'
        })

    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = `视频${props.videoId}笔记.docx`
    document.body.appendChild(a)
    a.click()

    // 清理
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)

    ElMessage.success('导出Word文档成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出Word文档失败: ' + (error.message || '未知错误'))
  } finally {
    if (loading) loading.close()
  }
}

// 滚动到指定笔记位置（支持二级标题）
const scrollToNote = (noteIndex, subIndex = null) => {
  // 构建唯一ID：一级标题用 note-1，二级标题用 note-3-1
  const targetId = `note-${noteIndex}${subIndex ? `-${subIndex}` : ''}`
  const targetNote = document.getElementById(targetId)

  if (targetNote) {
    targetNote.scrollIntoView({
      behavior: 'smooth',
      block: 'start',
    })
  }
}

// 判断是否是二级标题
const isSubItem = (item) => {
  return item.subIndex !== undefined && item.subIndex !== null
}

// 各部分加载完成回调
const onChartLoaded = () => {
  chartLoaded.value = true
  checkAllLoaded()
}
const onNotesLoaded = () => {
  notesLoaded.value = true
  checkAllLoaded()
}

const checkAllLoaded = () => {
  if (chartLoaded.value && notesLoaded.value) {
    globalLoading.value = false
    if (loadingInstance) {
      loadingInstance.close()
      loadingInstance = null
    }
  }
}
</script>

<template>
  <el-icon @click="goBack" size="24px" class="back-button"><Back /></el-icon>
  <div class="note-layout">
    <!-- 左侧知识树 -->
    <NoteChart
      :videoId="videoId"
      class="note-chart"
      @loaded="onChartLoaded"
    />

    <!-- 右侧笔记内容 -->
    <div class="notes-container">
      <!-- 目录展示 -->
      <div id="directory">
        <!-- 目录标题 -->
        <div class="directory-toggle" @click="isDirectoryOpen = !isDirectoryOpen">
          📂 目录
          <span>{{ isDirectoryOpen ? '▲' : '▼' }}</span>
        </div>

        <!-- 展开后显示目录项 -->
        <transition name="fade-slide">
          <div v-show="isDirectoryOpen" class="directory-list">
            <div
              v-for="item in directoryData"
              :key="item.label"
              class="dir-item"
              :class="{ 'sub-item': item.subIndex !== undefined }"
              @click="scrollToNote(item.noteIndex, item.subIndex)"
            >
              {{ item.label }}
            </div>
          </div>
        </transition>
      </div>

      <!-- 正文展示 -->
      <div id="notes">
        <div
          v-for="note in notesData"
          :key="`${note.noteIndex}-${note.subIndex || 0}`"
          :id="`note-${note.noteIndex}${note.subIndex ? `-${note.subIndex}` : ''}`"
          class="note-block"
          :class="{ 'sub-note': isSubItem(note) }"
        >
          <h3 v-if="isSubItem(note)">{{ note.title }}</h3>
          <h2 v-else>{{ note.title }}</h2>
          <p>{{ note.content }}</p>
        </div>

        <!-- 导出按钮 -->
        <div class="export-container">
          <el-button
            type="primary"
            @click="exportToWord"
            class="export-btn"
          >
            <el-icon><Download /></el-icon>
            导出Word文档
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>


<style scoped lang="scss">
.note-layout {
  display: flex;
  width: 100%;
  height: 100%;
  /* gap: 20px; */
}
.notes-container {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.note-chart {
flex: 1;
  min-width: 0;
  overflow: hidden;
}

/* 目录区域 */
#directory {
  background-color: #f9f9f9;
  border-bottom: 1px solid #eee;
}

/* 目录项样式 */
.dir-item {
  padding: 10px;
  cursor: pointer;
  font-size: 18px;
  transition: all 0.2s;
}

.dir-item:hover {
  background-color: #f0f0f0;
}

/* 二级目录项 */
.dir-item.sub-item {
  padding-left: 30px;
  font-size: 16px;
  color: #666;
}

/* 正文区域 */
#notes {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

/* 笔记块样式 */
.note-block {
  margin: 20px 0;
  padding: 15px;
  border-left: 3px solid #3498db;
}

/* 二级笔记块 */
.note-block.sub-note {
  border-left-color: #95a5a6;
  margin-left: 20px;
  padding-left: 25px;
}

/* 标题样式 */
.note-block h2 {
  font-size: 22px;
  margin-bottom: 10px;
  color: #2c3e50;
}

.note-block h3 {
  font-size: 19px;
  margin-bottom: 10px;
  color: #7f8c8d;
}

/* 内容样式 */
.note-block p {
  font-size: 16px;
  line-height: 1.6;
  color: #34495e;
}
/* 切换按钮区域 */
.directory-toggle {
  font-weight: bold;
  cursor: pointer;
  font-size: 18px;
  padding: 12px;
  background-color: #fff;
  border-bottom: 1px solid #ccc;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 目录列表（展开后） */
.directory-list {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px 15px;
}
/* 进入和离开动画 */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.fade-slide-enter-from,
.fade-slide-leave-to {
  max-height: 0;
  opacity: 0;
}
.fade-slide-enter-to,
.fade-slide-leave-from {
  max-height: 400px;
  opacity: 1;
}
/* 导出word */
.export-container {
  text-align: center;
  margin: 30px 0;
  padding: 20px;
  border-top: 1px solid #eee;
}

.export-btn {
  padding: 12px 24px;
  font-size: 16px;

  .el-icon {
    margin-right: 8px;
  }
}
.back-button {
  margin: 10px 0 0 18px;
  cursor: pointer;
}
</style>
