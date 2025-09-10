<script setup>
import { ref, watch } from 'vue'
import router from '@/router'
import { ElMessage } from 'element-plus'

const formModel = ref({
  id: '',
  password: '',
  repassword: '',
})

const form = ref()
const isLogin = ref(false)

const rules = {
  id: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 5, max: 10, message: '用户名必须是5-10位的字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { pattern: /^\S{6,15}$/, message: '密码必须是6-15位的非空字符', trigger: 'blur' },
  ],
  repassword: [
    {
      validator: (rule, value, callback) => {
        if (isLogin.value) {
          callback()
        } else if (value === formModel.value.password) {
          callback()
        } else {
          callback(new Error('两次输入密码不一致'))
        }
      },
      trigger: 'blur',
    },
  ],
}

const submitForm = () => {
  form.value.validate((valid) => {
    if (valid) {
      if (!isLogin.value) {
        ElMessage.success('注册成功!')
        isLogin.value = true
      } else {
        ElMessage.success('登录成功!')
        router.push('/')
      }
    }
  })
}

watch(isLogin, () => {
  formModel.value = {
    id: '',
    password: '',
    repassword: '',
  }
})

const toggleLogin = () => {
  isLogin.value = !isLogin.value
}
</script>

<template>
  <div class="container">
    <div class="login-bac">
      <div class="right-form">
        <el-form :model="formModel" ref="form" :rules="rules">
          <h1>{{ isLogin ? 'Login' : 'Registration' }}</h1>

          <el-form-item class="input-box" prop="id">
            <input
              v-model="formModel.id"
              @blur="form.validateField('id')"
              type="text"
              placeholder="账号"
              required
            />
            <i class="bx bxs-user"></i>
          </el-form-item>

          <el-form-item class="input-box" prop="password">
            <input
              v-model="formModel.password"
              @blur="form.validateField('password')"
              type="password"
              placeholder="密码"
              required
            />
            <i class="bx bxs-lock-alt"></i>
          </el-form-item>

          <el-form-item class="input-box" prop="repassword" v-if="!isLogin">
            <input
              v-model="formModel.repassword"
              @blur="form.validateField('repassword')"
              type="password"
              placeholder="确认密码"
              required
            />
            <i class="bx bxs-lock-alt"></i>
          </el-form-item>

          <el-form-item class="option-box">
            <template v-if="isLogin">
              <el-checkbox value="remember-me">记住密码</el-checkbox>
              <a href="#" class="toggle-link" @click.prevent="toggleLogin">没有账号？去注册</a>
            </template>
            <template v-else>
              <span class="empty-placeholder"></span>
              <a href="#" class="toggle-link" @click.prevent="toggleLogin">已有账号？去登录</a>
            </template>
          </el-form-item>

          <button type="submit" class="btn" @click.prevent="submitForm">
            {{ isLogin ? '登录' : '注册' }}
          </button>
        </el-form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('@/assets/images/login-5.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-bac {
  position: relative;
  width: 360px;
  height: 380px;
  background-color: transparent;
  backdrop-filter: blur(20px);
  border: 2px solid rgba(255, 255, 255, 0.5);
  border-radius: 30px;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.2);
}

.right-form {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  text-align: center;
  color: #333;
  padding: 40px;
  border-radius: 30px;
}

form {
  width: 100%;
}

.container h1 {
  font-size: 36px;
  margin: -10px 0;
}

.input-box {
  position: relative;
  margin: 30px 0;
  border-bottom: 2px solid #162938;
}

.input-box input {
  width: 100%;
  padding: 4px 40px 4px 11px;
  border: none;
  outline: none;
  font-size: 14px;
  color: #333;
  font-weight: 500;
  background: transparent;
}

.input-box input::placeholder {
  color: #162938;
  font-weight: 400;
}

.input-box i {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #162938;
}

.btn {
  width: 100%;
  height: 34px;
  background: #162938;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  border: none;
  cursor: pointer;
  font-size: 16px;
  color: #fff;
  font-weight: 600;
}

.option-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: -10px 0 10px;
  width: 100%;
}

.option-box .toggle-link {
  margin-left: auto;
  font-size: 12px;
  color: #162938;
  text-decoration: none;
}

.toggle-link {
  font-size: 12px;
  color: #162938;
  text-decoration: none;
}

.empty-placeholder {
  width: 85px;
  height: 1px;
}

/* 覆盖 Element Plus checkbox 样式 */
:deep(.el-checkbox) {
  margin: 0;
  display: flex;
  align-items: center;
  color: #162938;
}

:deep(.el-checkbox__inner) {
  border-radius: 10px;
  width: 12px;
  height: 12px;
  border: 1.5px solid #162938;
}

:deep(.el-checkbox__label) {
  font-size: 12px;
  margin-left: -3px;
  color: #162938;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #162938;
  border-color: #162938;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #162938;
}
</style>
