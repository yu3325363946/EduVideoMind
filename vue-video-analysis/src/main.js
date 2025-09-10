import { createApp } from 'vue'
import { createPinia } from 'pinia'
//导入pinia持久化
import persist from 'pinia-plugin-persistedstate'
import 'boxicons/css/boxicons.min.css'
//导入全局样式
import 'element-plus/dist/index.css'
import '@/assets/styles/overall.css'
import TDesign from 'tdesign-vue-next'
import TDesignChat from '@tdesign-vue-next/chat'; // 引入chat组件
import naive from 'naive-ui'

import App from './App.vue'
import router from './router'
import '@/assets/main.scss'

const app = createApp(App)
const pinia = createPinia()

app.use(router)
pinia.use(persist)
app.use(pinia)
app.use(naive)
app.use(TDesign).use(TDesignChat);
// 全局注册 Phosphor Icons 组件
app.config.globalProperties.$ph = window.PhosphorIcons

//最后挂载应用
app.mount('#app')
