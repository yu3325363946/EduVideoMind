<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import videojs from 'video.js'
import 'video.js/dist/video-js.css'
import { playVideo, generateChapters,getVideoSummarize } from '../../../services/video'
import ChapterList from './ChapterList.vue'
import VideoTab from './VideoTab.vue'
import zhCN from 'video.js/dist/lang/zh-CN.json'  // 这一行引入语言包
videojs.addLanguage('zh-CN', zhCN)

const route = useRoute()
const videoId = route.params.videoId
const videoRef = ref(null)
const chapters = ref([])
let player = null
let subtitleTrack = null // 字幕轨道对象
// 用来记录“章节数据是否已经加载过”
const loadedChapters = ref(false)
const videoTitle = ref('')
const summaryData = ref([])
const loadingChapters = ref(false)
const STORAGE_KEY = `video-${videoId}-last-time`  // 用 videoId 保证唯一性

console.log('🚀 路由参数:', videoId)
//在组件的 DOM 挂载完成后 执行

onMounted(async () => {
  const res = await playVideo(videoId)
  videoTitle.value = res.video.title

  player = videojs(videoRef.value, {
    language: 'zh-CN', // 设置为中文语言
  }, () => {
    player.src({ type: 'video/mp4', src: res.video.filePath })

    const savedTime = localStorage.getItem(STORAGE_KEY)
    if (savedTime) {
      player.currentTime(parseFloat(savedTime))
    }

    //添加一个新的字幕轨道，并默认隐藏
    subtitleTrack = player.addTextTrack('subtitles', '中文（自动）', 'zh')
    if (subtitleTrack) {
      subtitleTrack.mode = 'hidden'
      res.subtitles.forEach((s) => {
        const cue = new VTTCue(s.startTime, s.endTime, s.content)
        subtitleTrack.addCue(cue)
      })
    } else {
      console.warn('字幕轨创建失败')
    }

    // 注册并添加按钮（必须在字幕加载后）
  const Button = videojs.getComponent('Button');
  const SubtitleToggleButton = videojs.extend(Button, {
    constructor: function (player, options) {
      Button.apply(this, arguments);
      this.controlText('切换字幕');
    },
    handleClick: function () {
      if (!subtitleTrack) return;
      subtitleTrack.mode = subtitleTrack.mode === 'showing' ? 'hidden' : 'showing';
      this.controlText(subtitleTrack.mode === 'showing' ? '隐藏字幕' : '显示字幕');
    },
  });

  videojs.registerComponent('SubtitleToggleButton', SubtitleToggleButton);
  player.getChild('controlBar').addChild('SubtitleToggleButton', {}, player.controlBar.children().length - 1);

  // 添加 timeupdate 事件监听，记录播放进度
    player.on('timeupdate', () => {
      const currentTime = player.currentTime()
      localStorage.setItem(STORAGE_KEY, currentTime)
    })
  })
  })

  //清理播放器（组件卸载）
  onBeforeUnmount(() => {
  if (player) player.dispose()
})

//点击后获取章节列表
const loadChapters = async () => {
  if (loadedChapters.value) return
  loadingChapters.value = true
  try {
    const res = await generateChapters(videoId)
    if (res.code === 200) {
      chapters.value = res.chapters
      loadedChapters.value = true
    }
  } finally {
    loadingChapters.value = false
  }
}

//点击章节跳转
const handleJump = (time) => {
  console.log('跳转时间：', time)
  if (player) {
    //video.js提供的控制视频位置的方法
    player.currentTime(time)
    player.play() //跳转后自动播放
  }
}

//tab - 总结
const loadSummary = async () => {
  console.log('还没发总结请求');
  //如果之前请求过（已经有内容），就不再重复请求
  if(summaryData.value.length > 0) return
  console.log('summaryData.value.length:',summaryData.value.length);
try {
  const res = await getVideoSummarize(videoId)
  console.log('已经发了总结请求,并且拿到数据：',res);
  if(res.code === 200){
  summaryData.value = res.summary || [];
  console.log('处理后的数组数据:', summaryData.value);
  }}
  catch(err){
  console.error('请求失败:', err);
  summaryData.value = []
}
}

</script>

<template>
  <div>
      <h2>{{ videoTitle }}</h2>
      </div>
  <div class="video-box">
    <div class="video-container">
    <video
      ref="videoRef"
      class="video-js vjs-default-skin"
      controls
      preload="auto"
      width="854"
      height="480"
    >
      <!-- 多语言字幕 -->
      <track
        v-for="(track, index) in tracks"
        :key="index"
        kind="subtitles"
        :src="track.src"
        :srclang="track.lang"
        :label="track.label"
        :default="track.default || false"
      />
    </video>
    </div>
    <div class="right-panel">
      <!-- 右侧章节划分 -->
      <ChapterList :chapters="chapters" :loading="loadingChapters" @jump="handleJump" @expand="loadChapters"></ChapterList>
    </div>
  </div>

  <!-- tab栏 -->
  <VideoTab :videoId="videoId" :loading="loading" :summary="summaryData" @summary-tab-click="loadSummary" ></VideoTab>
</template>

<style lang="scss">
.video-box {
  display: flex;
}

.video-container {
  position: relative;
  aspect-ratio: 16 / 9;
  width: 100%;
}

.right-panel {
  display: flex;
  flex-direction: column;
  width: 300px;
  padding: 12px;
}

.video-list {
  background-color: #f9f9f9;
  padding: 8px;
}

.video-item {
  padding: 6px 0;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
}
.video-js {
  width: 100%;
  height: 100%;
}

h2 {
  font-weight: 500;
}
.video-js .vjs-big-play-button {
  top: 50% !important;
  left: 50% !important;
  transform: translate(-50%, -50%) !important;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.6);
  border: none;
  z-index: 10;

  background-image: url('data:image/svg+xml;utf8,<svg fill="white" viewBox="0 0 60 60" xmlns="http://www.w3.org/2000/svg"><polygon points="20,15 45,30 20,45"/></svg>');
  background-repeat: no-repeat;
  background-position: center;
  background-size: 50%;
}

// 关键伪元素完全禁用
.video-js .vjs-big-play-button::before {
  content: '' !important;
  font-family: none !important;
  display: none !important;
}

// 额外禁用内置图标 DOM
.video-js .vjs-big-play-button .vjs-icon-placeholder {
  display: none !important;
}


</style>
