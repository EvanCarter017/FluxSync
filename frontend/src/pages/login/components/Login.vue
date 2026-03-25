<script setup lang="ts">
import useLogin from "../composables/useLogin";
import {
  loginStatus
} from "../composables/script";
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import config from "@/store";
import {ElNotification} from "element-plus";

const Router = useRouter();
const login = useLogin();

async function submit() {
  const status = await login.login();
  if (status[0] && status[1]) {
    await Router.push("/board");
  } else if (status[0] && !status[1]) {
    ElNotification({
      title: "您的账号已开启二次身份验证",
      message: "基于2FA(双因素认证)的安全认证方法, 为登录敏感操作额外增加一层保护, 最大限度地保护您的账号安全.",
      type: "success",
      duration: 10000,
      showClose: false
    });
    loginStatus.value = "totp";
  } else {
    ElNotification({
      title: "登录失败",
      message: "账号或密码错误",
      type: "error"
    });
  }
}

onMounted(() => {
  if (config.info.username != "") {
    Router.replace("/board");
  }
})

</script>

<template>
  <div style="display: flex; flex-direction: column; width: 80%;">

    <el-text style="color: aliceblue" type="primary">欢迎回来. 请登录以访问您的账户.</el-text>

    <el-form @submit.prevent="submit()" :model="login.form" style="margin-top: 30px; width: 100%;">
      <el-form-item>
        <el-text style="color: aliceblue">用户名</el-text>
        <el-input class="inputBox" v-model="login.form.username" placeholder="请输入您的用户名" type="text" clearable />
      </el-form-item>
      <el-form-item>
        <el-text style="color: aliceblue">密码</el-text>
        <el-input class="inputBox" v-model="login.form.pwd" placeholder="请输入您的密码" type="password" show-password clearable />
      </el-form-item>
      <el-form-item style="margin-top: 30px;">
        <el-button style="width: 100%; height: 45px;" type="primary" native-type="submit">
          登录
        </el-button>
      </el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%; user-select: none; margin-top: 10px;">
        <el-link style="color: aliceblue" @click="loginStatus = 'register';" type="primary">注册账户</el-link>
        <el-link type="primary" style="color: aliceblue">找回密码</el-link>
      </div>
    </el-form>

  </div>

</template>

<style scoped>
.inputBox {
  height: 40px;
}
:deep(.el-form-item) {
  margin-bottom: 5px !important;
}
:deep(.el-input__wrapper) {
  background-color: #1C242D;
}
:deep(.el-input) {
  --el-input-border-color: transparent !important;
  --el-input-hover-border-color: rgb(148, 163, 184) !important;
  --el-input-focus-border-color: rgb(148, 163, 184) !important;
  --el-input-text-color: aliceblue;
}

:deep(.el-link:hover:after) {
  border-color: aliceblue !important;
}

</style>