<script setup>
import { ref, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import * as VTable from '@visactor/vtable'
import { getVideoTable } from '../../../services/video'

const router = useRouter()
const showTable = ref(false) // 控制是否显示表格

const props = defineProps({
  summary: {
    type: Array,
    default: () => [],
  },
  videoId: String,
  videoTitle: String // 新增 videoTitle（用于 AI 问答）
})

// 向父组件发送事件
const emit = defineEmits(['summary-tab-click'])
const handleClick = () => {
  emit('summary-tab-click')
  showTable.value = true
  console.log('已点击按钮，通知父组件加载数据')
}

// 跳转课后练习
const toExercises = () => {
  router.push(`/video/exercises/${props.videoId}`)
}

// 跳转课堂笔记
const toNote = () => {
  router.push(`/video/notes/${props.videoId}`)
}

// 监听显示表格
watch(showTable, async (visible) => {
  if (!visible || !props.videoId) return
  await nextTick()
  const container = document.getElementById('tableContainer')
  if (!container) return

  try {
    const res = await getVideoTable(props.videoId)
    if (res.code === 200) {
      const chartOption = res.chartOption
      new VTable.ListTable(container, {
        ...chartOption,
        widthMode: 'adaptive',
        heightMode: 'adaptive',
        animationAppear: {
          duration: 300,
          delay: 50,
          type: 'one-by-one',
          direction: 'row'
        },
        theme: 'default'  // ✅ 设置主题
      })
    }
  } catch (err) {
    console.error('加载表格失败:', err)
  }
})
</script>

<template>
  <el-button class="sum-btn" @click="handleClick" type="primary" round>视频总结</el-button>
  <el-button class="sum-btn" @click="toNote" type="primary" round>课堂笔记</el-button>
  <el-button class="sum-btn" @click="toExercises" type="primary" round>课后练习</el-button>
  <hr />
    <div v-for="(item, index) in summary" :key="index">
      <h3>{{ item.chapterTitle }}</h3>
      <p>{{ item.summary }}</p>
    </div>

  <!-- 表格 -->
  <div v-if="showTable" id="tableContainer" class="table-wrapper"></div>
</template>

<style scoped>
.table-wrapper {
  width: 1700px;
  height: 330px;
  transform: scale(0.8);
  transform-origin: top left;
}

.sum-btn {
  margin: 20px 0 20px 0;
  height: 30px;
  width: 76px;
  font-size: 14px;
  background-color: #649dff;
  border: #649dff;
  margin-right: 16px;
}

hr {
  border: 1px solid var(--video-tab-color);
}
h3 {
  margin: 5px 0 5px 0;
}
</style>
