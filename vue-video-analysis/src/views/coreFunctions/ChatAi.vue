<script setup>
import { ref } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const videoTitle = route.query.title || '当前视频';

const visibleModelessDrag = ref(false);
const loading = ref(false);
const isStreamLoad = ref(false);
import userAvatar from '@/assets/images/head.jpg'

const chatList = ref([
  {
    avatar: 'https://tdesign.gtimg.com/site/chat-avatar.png',
    name: '小慧',
    datetime: new Date().toLocaleString(),
    content: `✨你好，我是你的智能助手小慧，有什么关于《${videoTitle}》的问题尽管问我吧～`,
    role: 'assistant',
    type: 'text',
  },
]);

// 输入框绑定内容
const inputValue = ref('');

// 缓存粘贴的图片文件
const pastedImageFile = ref(null);
const pastedImagePreview = ref(''); // 本地图片预览URL

const handleOperation = (type, options) => {
  console.log('handleOperation', type, options);
};

const operation = (type, options) => {
  console.log(type, options);
};

const clearConfirm = () => {
  chatList.value = [];
};

// 粘贴事件捕获图片，但不自动发送，只缓存
const onPaste = (event) => {
  if (isStreamLoad.value) return;
  const items = event.clipboardData?.items;
  if (!items) return;

  for (let i = 0; i < items.length; i++) {
    const item = items[i];
    if (item.type.indexOf('image') !== -1) {
      const file = item.getAsFile();
      if (file) {
        pastedImageFile.value = file;
        pastedImagePreview.value = URL.createObjectURL(file);
        // 阻止默认粘贴行为，防止图片插入输入框
        event.preventDefault();
        break; // 只取第一张图片
      }
    }
  }
};

// 发送按钮点击
const inputEnter = async () => {
  if (isStreamLoad.value) return;
  const text = inputValue.value.trim();
  if (!text && !pastedImageFile.value) {
    // 文字和图片都为空，不发送
    return;
  }

  // 先把用户消息推到聊天框
  if (pastedImageFile.value) {
    chatList.value.unshift({
      avatar: userAvatar,
      datetime: new Date().toLocaleString(),
      content: text,
      imageUrl: pastedImagePreview.value,
      role: 'user',
      type: 'image',
    });
  } else {
    chatList.value.unshift({
      avatar: userAvatar,
      datetime: new Date().toLocaleString(),
      content: text,
      role: 'user',
      type: 'text',
    });
  }

  // AI 回复占位
  chatList.value.unshift({
    avatar: 'https://tdesign.gtimg.com/site/chat-avatar.png',
    name: '小慧',
    datetime: new Date().toLocaleString(),
    content: '',
    role: 'assistant',
    type: 'text',
  });

  loading.value = true;
  isStreamLoad.value = true;

  try {
    let answer = '';

    if (pastedImageFile.value) {
      // 图片+文字一起发图片接口
      const form = new FormData();
      form.append('image', pastedImageFile.value);
      form.append('question', text);
      const res = await fetch('http://localhost:8080/api/ai-assistant/image', {
        method: 'POST',
        body: form,
      });
      const json = await res.json();
      answer = json.data?.answer || '';
    } else {
      // 纯文字发送文字接口
      const res = await fetch('http://localhost:8080/api/ai-assistant/text', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question: text }),
      });
      const json = await res.json();
      answer = json.data?.answer || '';
    }

    // 更新 AI 占位消息
    chatList.value[0].content = answer || 'AI 没有返回答案';
    chatList.value[0].role = answer ? 'assistant' : 'error';
  } catch (err) {
    chatList.value[0].content = '请求失败，请稍后重试';
    chatList.value[0].role = 'error';
  } finally {
    loading.value = false;
    isStreamLoad.value = false;
    // 清空输入框和图片缓存
    inputValue.value = '';
    pastedImageFile.value = null;
    pastedImagePreview.value = '';
  }
};
</script>

<template>
  <t-space align="center">
    <div @click="visibleModelessDrag = true">
      <img src="https://tdesign.gtimg.com/site/chat-avatar.png" class="avatar" />
    </div>
  </t-space>

  <t-dialog
    v-model:visible="visibleModelessDrag"
    :footer="false"
    header="AI助手"
    mode="modeless"
    draggable
    :on-confirm="() => (visibleModelessDrag = false)"
  >
    <template #body>
      <t-chat
        layout="both"
        class="chat-box"
        :data="chatList"
        :clear-history="chatList.length > 0 && !isStreamLoad"
        :text-loading="loading"
        :is-stream-load="isStreamLoad"
        @on-action="operation"
        @clear="clearConfirm"
      >
        <template #actions="{ item }">
          <t-chat-action
            v-if="item.type === 'text'"
            :content="item.content"
            :operation-btn="['good', 'bad', 'replay', 'copy']"
            @operation="handleOperation"
          />
          <template v-else-if="item.type === 'image'">
            <img
              :src="item.imageUrl"
              alt="用户粘贴的图片"
            />
            <div v-if="item.content" style="white-space: pre-wrap; margin-top: 4px;">{{ item.content }}</div>
          </template>
        </template>

        <template #footer>
          <t-chat-input
            v-model="inputValue"
             class="custom-chat-input"
            :stop-disabled="isStreamLoad"
            @send="inputEnter"
            @stop="onStop"
            @paste.native.prevent="onPaste"
            placeholder="请输入文字或粘贴图片"
            style="min-height: 80px;"
          />
          <!-- 如果有图片预览，显示缩略图提示用户 -->
          <div v-if="pastedImagePreview" style="margin-top: 6px;">
            <span>已粘贴图片：</span>
            <img
              :src="pastedImagePreview"
              alt="预览图"
              style="max-width: 120px; border-radius: 6px; vertical-align: middle;"
            />
            <el-button size="mini" type="text" @click="() => { pastedImageFile = null; pastedImagePreview = '' }">删除</el-button>
          </div>
        </template>
      </t-chat>
    </template>
  </t-dialog>
</template>


<style lang="less">
::-webkit-scrollbar-thumb {
  background-color: var(--td-scrollbar-color);
}
::-webkit-scrollbar-thumb:horizontal:hover {
  background-color: var(--td-scrollbar-hover-color);
}
::-webkit-scrollbar-track {
  background-color: var(--td-scroll-track-color);
}
.t-dialog {
  width: 430px !important;
  height: 650px !important;
  border-radius: 12px;

  .t-dialog__body {
    height: 100%;
    display: flex;
    flex-direction: column;

    .chat-box {
      flex: 1;
      min-height: 0; // 💡 关键：允许子元素撑满剩余空间
    }
  }
}
// 输入框
.custom-chat-input {
  align-items: center;
  min-height: 80px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  margin-left: 6px;
  margin-top: -4px;
  cursor: pointer;
}
</style>
