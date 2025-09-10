<script setup>

const props = defineProps({
  chapters: {
    //限制 chapters 必须是一个数组
    type: Array,
    //如果没有则渲染空数组
    default: () => [],
  },
  loading: Boolean
})

//expand 监听列表展开行为
const emit = defineEmits(['jump', 'expand'])
const jumpTo = (startTime) => {
  emit('jump', startTime)
}

const onExpand = (names) => {
  if (names.includes('sub')) {
    emit('expand')
  }
}
</script>

<template>
  <!-- []为['sub']时，默认打开面板 -->
   <n-collapse :default-expanded-names="[]" class="subtitle-panel" @update:expanded-names="onExpand">
  <n-collapse-item title="章节目录" name="sub">
    <!-- 用 n-spin 包裹内容，loading=true 时自动居中显示加载动画 -->
    <n-spin :show="loading" style="width: 100%; min-height: 600px;">
      <n-scrollbar style="max-height: 550px">
        <div
          v-for="chapter in chapters"
          :key="chapter.startTime"
          class="subtitle-item"
          @click="jumpTo(chapter.startTime)"
        >
          <div class="chapter-title">{{ chapter.titleSummary }}</div>
          <div class="chapter-time">{{ chapter.displayTime }}</div>
        </div>
      </n-scrollbar>
    </n-spin>
  </n-collapse-item>
</n-collapse>

</template>

<style lang="scss">
.subtitle-panel {
  margin-bottom: 16px;
}
.subtitle-item {
  padding: 10px;
  margin-right: 0;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.2s ease;
}
.subtitle-item:hover {
  background-color: #f9f9f9;
  transition: color 0.3s ease;
}
span {
font-size: 12px;
}
.chapter-title {
  font-size: 15px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}
.chapter-time {
  font-size: 12px;
  color: #5a97ff;
}
.right-panel {
  padding: 0 0 0 20px;
}
.n-collapse .n-collapse-item .n-collapse-item__content-wrapper .n-collapse-item__content-inner {
  padding-top: 5px;
}
.n-collapse-item {
  background-color: #fff;
  padding: 5px 0 5px 0;
  border-bottom: 1px solid #f1f2f4;
}
</style>
