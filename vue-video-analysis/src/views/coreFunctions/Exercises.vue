<script setup>
import { ref,computed,watch } from 'vue'
import { transferVideoId,getPracticeList,submitResult } from '../../../services/exercises'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { Back } from '@element-plus/icons-vue'

// 题目列表
const practiceList = ref([])
const videoTitle = ref('')
const score = ref(null)
const resultData = ref([])
const showResults = ref(false) // 控制是否显示批改结果
const router = useRouter()

const goBack = () => {
  router.back()
}

const props = defineProps({
  videoId: String
})

console.log('组件 props:', props)
watch(
  () => props.videoId,  // 监听目标
  async (videoId) => {
    if (videoId) {
      console.log('收到 videoId:', videoId)
      try {
        await transferVideoId(videoId)
        const res = await getPracticeList(videoId)
        practiceList.value = res.questions.map((q, index) => ({
          id: q.questionId,
          displayId: index + 1,
          text: q.question,
          options: [q.optionA, q.optionB, q.optionC],
        }))
        videoTitle.value = res.videoTitle
      } catch (error) {
        console.error('题目加载失败:', error)
      }
    } else {
      console.warn('videoId 为空')
    }
  },
  { immediate: true }
)


const selectedOptions = ref({})

const currentIndex = ref(0)

const currentQuestion = computed(() => practiceList.value[currentIndex.value])

const isQuestionWrong = (questionId) => {
  const result = resultData.value.find(item =>
    item.questionId === questionId ||
    item.question.includes(practiceList.value.find(q => q.id === questionId)?.text // 或通过题目内容匹配
  ))
  return result && !result.correct
}

const submit = async() => {
  if (!isAllAnswered.value) {
    ElMessage.warning('请完成所有题目后再提交')
    return
  }
  try {

    const answers = Object.entries(selectedOptions.value).map(([questionId, optionIndex]) => ({
      questionId: questionId,
      selectedAnswer: String.fromCharCode(65 + optionIndex)
    }));

    // 提交答案
    const res = await submitResult(answers)
    resultData.value = res
    showResults.value = true

    // 计算得分
     const correctCount = resultData.value.filter(item => item.correct).length
    score.value = Math.round((correctCount / 12) * 100)

    ElMessage.success('提交成功')

  } catch (error) {
    console.error('提交失败:', error)
    console.log('提交失败，请重试')
  }
}

// 当前题目的批改结果
const currentResult = computed(() => {
  if (!showResults.value || !currentQuestion.value) return null
  return resultData.value.find(item =>
    item.questionId === currentQuestion.value.id ||
    item.question.includes(currentQuestion.value.text)
  )
})


const isAllAnswered = computed(() => {
  return practiceList.value.length > 0 &&
         Object.keys(selectedOptions.value).length === practiceList.value.length
})

// 切换题目
const nextQuestion = () => {
  if (currentIndex.value < practiceList.value.length - 1) {
    currentIndex.value++
  }
}
const prevQuestion = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const scrollToQuestion = (id) => {
  const index = practiceList.value.findIndex(q => q.id === id)
  if (index !== -1) {
    currentIndex.value = index
  }
}

</script>

<template>
  <el-icon @click="goBack" size="24px" class="back-button"><Back /></el-icon>

 <div style="display: flex;">
    <div class="glass-panel">
      <p>{{ videoTitle }}课后练习</p>
      <h2 :class="{ red: score !== null }">
  得分: {{ score !== null ? score + '分' : '--' }}
</h2>
      <hr>
      <div v-if="currentQuestion" class="question-area" style="margin-bottom: 20px;">
        <h3>{{ currentQuestion.displayId }}. {{ currentQuestion.text }}</h3>
        <ul>
          <li class="options" v-for="(opt, i) in currentQuestion.options" :key="i" style="margin-bottom: 10px;">
            <label :class="{
              selected: selectedOptions[currentQuestion.id] === i}">
              <input
                type="radio"
                :name="'question_' + currentQuestion.id"
                :value="i"
                v-model="selectedOptions[currentQuestion.id]"
              />
              {{ String.fromCharCode(65 + i) }}. {{ opt }}
            </label>
          </li>
        </ul>

        <div v-if="showResults && currentResult" class="answer-analysis">
          <p><strong>你的答案:</strong> {{ currentResult.yourAnswer }}</p>
          <p v-if="!currentResult.correct" style="color: #d32f2f;">
            <strong>正确答案:</strong> {{ currentResult.correctAnswer }}
          </p>
          <p><strong>解析:</strong> {{ currentResult.explanation }}</p>
        </div>
      </div>
      <div v-else>
      加载中...
      </div>

     <div class="btn-container">
  <div class="left-btns">
    <button @click="prevQuestion" :disabled="currentIndex === 0">上一题</button>
    <button @click="nextQuestion" :disabled="currentIndex === practiceList.length - 1">下一题</button>
  </div>
  <div class="right-btns">
    <button @click="submit">提交</button>
  </div>
</div>
    </div>

    <div class="item-num-panel">
      <h3>题号</h3>
  <div class="question-nav">
    <button
      v-for="q in practiceList"
      :key="q.displayId"
      :class="{
        selected: selectedOptions[q.id] !== undefined,
        wrong: showResults && isQuestionWrong(q.id)
      }"
      @click="scrollToQuestion(q.id)"
    >
      {{ q.displayId }}
    </button>
  </div>
    </div>
  </div>
</template>

<style scoped>
.glass-panel,
.item-num-panel {
  height: 86vh;
  margin-left: 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.15);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}
.glass-panel {
  position: relative;
  width: 70%;
  padding: 3rem;
  backdrop-filter: blur(20px);
  background-color: #fff;
}
.glass-panel p {
  font-size: 24px;
}
.item-num-panel {
  width: 30%;
  padding: 45px;
  background-color: #fff;
}
hr {
  margin: 10px 0 14px 0;
  border: 1px solid #cde8f2;
}
h3 {
  margin-block-end: 10px;
}
.red {
  color: #d32f2f;
  transition: color 0.3s ease;
}
.btn-container {
  position: absolute;
  bottom: 30px;
  left: 3rem;
  right: 3rem;
  display: flex;
  justify-content: space-between;
}

.left-btns button,
.right-btns button {
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  background-color: #97d9e6;
  color: white;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.left-btns button{
  margin-right: 20px;
}

.left-btns button:hover,
.right-btns button:hover {
  background-color: #4692b9;
}

.left-btns button:disabled {
  background-color: #cbd5e1;
  cursor: not-allowed;
}

/* 选中后的选项颜色 */
label.selected {
  color: #3697ab;
}
input[type="radio"] {
  accent-color: #3697ab;
}
.options {
  font-size: 15px;
}
/* 右侧题号 */
.item-num-panel {
  height: 86vh;
  width: 30%;
  overflow-y: auto;
}

.question-nav {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}
/* 未选择选项的题号 */
.question-nav button {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  border: 1.5px solid skyblue;
  background-color: rgba(255, 255, 255, 0.15);
  color: skyblue;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
}
/* 选择后的题号 */
.question-nav button.selected {
  background-color: #d9f7be;
  color: #52c41a;
  border: 1.5px solid #52c41a;
}

.question-nav button:hover {
  background-color: #97d9e6;
  color: #fff;
}
/* 错误题目的样式 */
.question-nav button.wrong {
  background-color: #ffebee;
  color: #f44336;
  border: 1.5px solid #f44336;
}

/* 选中且错误的题目 */
.question-nav button.selected.wrong {
  background-color: #ffcdd2;
  color: #d32f2f;
  border: 1.5px solid #d32f2f;
}
/* 解析 */
.answer-analysis {
  margin-top: 20px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.answer-analysis p {
  margin: 5px 0;
  font-size: 16px;
}
.back-button {
  margin: 10px 0 0 18px;
  cursor: pointer;
}
</style>

