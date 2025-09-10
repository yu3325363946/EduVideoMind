// services/video.js

import request from '../utils/request'

// 上传视频接口：返回后端 JSON 对象 { file, title }
export const uploadVideo = async (file, title) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('title', title)

  // 不再 .data，否则 undefined
  const res = await request.post('/api/video/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return res
}

// 获取首页默认视频
export const getVideoList = () => {
  return request.get('/api/static/all')
}

//获取字幕并播放视频
//这里的prams：用 GET 方式通过 URL 传 videoId 参数给后端
export const playVideo = (videoId) => {
  return request.get('/api/video/detail', { params: { videoId } })
}

//用户输入bv号播放视频
export const getBiliVideo = (bvid) => {
  return request.post('/api/bilibili/analyze',null,{params: { bvid } })
}

//获取视频章节内容
//videoId 是用来告诉后端你要哪个视频的章节数据
export const generateChapters = (videoId) => {
  return request.post('/api/chapter/generate', null, { params: { videoId } })
}

//获取视频总结
export const getVideoSummarize = (videoId) => {
  return request.get('/api/chapter/summary', { params: { videoId } })
  .then(res => {
    console.log('接口层响应:', res); // 验证数据是否到达前端
    return res; // 确保返回原始数据
  });
}

//视频总结表格
export const getVideoTable = (videoId) => {
  return request.get('/api/ai/outline', { params: { videoId } })
}

//默认视频资源
export const getDefaultVideoList = (videoId) => {
  return request.get('/api/static/all', { params: { videoId } })
}

//ai问答 axios会默认自带Content-Type: application/json请求头
export const getAiAssistant = (question) => {
  console.log('请求发送到接口，问题是：', question);
  return request.post('/api/ai-assistant/text', { question });
};

//笔记
export const getNotes = (videoId) => {
  return request.post('/api/notes/generate', null, { params: { videoId } })
}

//知识树
export const getKnowledgeTree = (videoId) => {
  return request.post('/api/tree/generate', null, { params: { videoId } })
}

//导出word文档
// 导出word文档
export const exportWord = (videoId) => {
  return request.post('/api/export/notes', null, {
    params: { videoId },
    responseType: 'blob' // 确保设置responseType为blob
  })
}