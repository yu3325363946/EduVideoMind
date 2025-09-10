import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import Icons from 'unplugin-icons/vite'
import IconsResolver from 'unplugin-icons/resolver'
import path from 'path';

export default defineConfig({
  assetsInclude: ['**/*.woff', '**/*.woff2', '**/*.ttf'],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        // 这两项都要给够大，避免代理层超时
        timeout: 10 * 60 * 1000,       // 10 分钟
        proxyTimeout: 10 * 60 * 1000,  // 10 分钟
      },
    },
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [
        ElementPlusResolver(),
        // 自动导入图标
        IconsResolver({
          prefix: 'Icon',
        }),
      ],
    }),
    Components({
      resolvers: [
        ElementPlusResolver(),
        // 自动注册图标组件
        IconsResolver({
          enabledCollections: ['ep'],
        }),
      ],
    }),
    Icons({
      autoInstall: true,
    }),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
       // 核心别名配置（不会影响其他组件）
       '@mock': path.resolve(__dirname, './src/mock-data'),
       '@tdesign': path.resolve(__dirname, './node_modules/@tdesign-vue-next'),
       
       // 针对TDesign的特殊处理
       '@tdesign-vue-next/lib': path.resolve(__dirname, './node_modules/@tdesign-vue-next/lib')
    },
  },
})
