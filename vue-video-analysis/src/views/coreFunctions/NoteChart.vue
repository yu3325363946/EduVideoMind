<script setup>
import { onMounted, ref, onUnmounted } from 'vue'
import MindElixir from 'mind-elixir'
import 'mind-elixir/style'
import { getKnowledgeTree } from '../../../services/video'

const emit = defineEmits(['loaded'])
const props = defineProps({
  videoId: String
})

const mindInstance = ref(null)
const loading = ref(true)
const error = ref(null)

onMounted(async () => {
  try {
    const res = await getKnowledgeTree(props.videoId)
    console.log('拿到的知识树的数据:', res)

    if (!res || res.code !== 200 || !res.nodeData) {
      throw new Error('获取知识树数据失败')
    }

    const mind = new MindElixir({
      el: '#map',
      direction: MindElixir.LEFT,
      draggable: true,
      contextMenu: true,
      toolBar: true,
      nodeMenu: true,
      keypress: true,
      initScale: 0.5,
      nodeStyle: {
        icon: {
          margin: '0 4px'
        }
      }
    })

    mindInstance.value = mind

    mind.init({
      nodeData: res.nodeData,
      linkData: {}
    })

    //  正确监听初始化布局完成
    mind.bus.addListener('layout', () => {
      adjustMindMap()
    })

    window.addEventListener('resize', handleResize)
    emit('loaded') // 告诉父组件思维导图加载完了
  } catch (err) {
    console.error('初始化思维导图失败:', err)
    error.value = err.message
  } finally {
    loading.value = false
  }
})


// 统一调整函数
const adjustMindMap = () => {
  if (!mindInstance.value) return

  try {
    mindInstance.value.fit()
    mindInstance.value.scale(0.7)

    const container = document.getElementById('map')
    if (container) {
      const containerHeight = container.clientHeight
      const contentHeight = mindInstance.value.viewBox.height * 0.7
      const top = Math.max(20, (containerHeight - contentHeight) / 2)
      mindInstance.value.relocate(20, top)
    }
  } catch (error) {
    console.error('调整思维导图时出错:', error)
  }
}

// 防抖的resize处理
let resizeTimer = null
const handleResize = () => {
  clearTimeout(resizeTimer)
  resizeTimer = setTimeout(() => {
    adjustMindMap()
  }, 200)
}

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (resizeTimer) clearTimeout(resizeTimer)
})
</script>

<template>
  <div class="mindmap-container">
    <!-- 思维导图容器 -->
    <div id="map" />
  </div>
</template>

<style scoped>
.mindmap-container {
  display: flex;
  justify-content: flex-start;
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  position: relative;
}

#map {
  width: 100%;
  height: 100%;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  border-radius: 12px;
  border: 1px solid #eee;
  overflow: hidden;
}

.error {
  color: #ff4d4f;
}

.error button {
  margin-top: 10px;
  padding: 5px 15px;
  background: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>

<style>
/* 全局思维导图样式优化 */
.mind-elixir .map-container {
  transform-origin: left top !important;
}

.mind-elixir .node .topic {
  white-space: nowrap;
  overflow: visible;
  font-size: 14px;
  padding: 4px 8px;
}

.mind-elixir .root-node .topic {
  font-size: 18px !important;
  padding: 8px 15px;
}
</style>
