import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      component: () => import('@/views/login/LoginPage.vue'),
    },
    {
      path: '/',
      component: () => import('@/views/layout/LayoutShelves.vue'),
      redirect: '/home/home-page',
      children: [
        {
          path: 'home/home-page',
          component: () => import('@/views/home/HomePage.vue'),
        },
        {
          path: 'core/video-details/:videoId?', // 动态视频播放页
          name: 'video', // 必须加 name 才能支持跳转
          component: () => import('@/views/coreFunctions/VideoDetails.vue'),
          props: true, // 让 videoId 可以作为 props 传进去
        },
        {
          // 注意video前面不要加 / 因为是children
          path: 'video/exercises/:videoId',
          component: () => import('@/views/coreFunctions/Exercises.vue'),
          props: true,
        },
        {
          path: 'video/notes/:videoId',
          component: () => import('@/views/coreFunctions/LectureNotes.vue'),
          props: true,
        }
      ],
    },
  ],
})

export default router
