<template>
  <video ref="videoEl" class="video-js vjs-default-skin" controls></video>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import zhCN from 'video.js/dist/lang/zh-CN.json'

const videoEl = ref(null)
let player = null

onMounted(async () => {
  // 注册中文语言包
  videojs.addLanguage('zh-CN', zhCN)

  await nextTick()

  player = videojs(
    videoEl.value,
    {
      controls: true,
      language: 'zh-CN',
      // 你可以继续传入其他 options，如 sources、autoplay 等
    },
    () => {
      console.log('Player 已准备就绪')
    },
  )
})

onBeforeUnmount(() => {
  if (player) player.dispose()
})
</script>
