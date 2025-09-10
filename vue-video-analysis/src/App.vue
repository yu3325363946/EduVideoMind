<script setup>
import { useUserStore } from '@/stores/user'
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const useUser = useUserStore()
const route = useRoute()
const router = useRouter()

onMounted(() => {
  router.push('/login')
  console.log('当前路由', route.path)
})
</script>

<template>
  <div>
    <router-view></router-view>
  </div>
</template>

<style scoped></style>
