import request from '../utils/request'

//给后端传videoId
export const transferVideoId = (videoId) => {
  return request.post('/api/practice/generate', null, { params: { videoId } })
}

//拿到题目和选项
export const getPracticeList = (videoId) => {
  return request.get('/api/practice/list', { params: { videoId } })
}

//将用户答题结果返回给后端
export const submitResult = (answers) => {
  return request.post(
    '/api/practice/submitAll',  // URL
    answers,                   // 请求体数据
    {                          // 配置对象
      headers: {
        'Content-Type': 'application/json'
      }
    }
  )
}