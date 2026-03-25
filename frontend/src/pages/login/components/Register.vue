<script setup lang="ts">

import useRegister from "../composables/useRegister";
import {
  loginStatus
} from "../composables/script";
import {ref} from "vue";
import type {ElInput} from "element-plus";

const register = useRegister();

const username = ref(null);
const pwd = ref(null);
const pwd2 = ref(null);

function reg() {
  formRef.value.validate(async (v) => {
    if (v) {
      const state = await register.register();

      if (state[0] && !state[1]) {
        username.value.clear();
        pwd.value.clear();
        pwd2.value.clear();
      }
    }
  });
}

const formRef = ref(null);

const rules = {
  username: [
    { validator: (rule, value, callback) => {
        if (!value) return callback(new Error('请输入用户名'));
        if (!/^[a-zA-Z0-9_]{2,20}$/.test(value) || value != value?.trim?.()) return callback(new Error('用户名需 2–20 位，仅含字母、数字或下划线.'));
        callback();
      }, trigger: "blur" }
  ],
  pwd: [
    { validator: (rule, value, callback) => {
        if (!value) return callback(new Error('请输入密码'));
        if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@*$#!_]{6,}$/.test(value))
          return callback(new Error('密码需至少 6 位，含字母和数字，仅可用 @ * $ # ! _ 符号.'));
        callback();
      }, trigger: "blur" }
  ],
  pwd2: [
    {
      validator: (rule, value, callback) => {
        if (value !== register.form.pwd) {
          callback(new Error("两次输入的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
};

</script>

<template>
  <div style="display: flex; flex-direction: column; width: 80%;">
    <el-text style="color: aliceblue">
      已经有账号了吗?
      <el-link @click="loginStatus = 'login'" type="primary">登录</el-link>
    </el-text>

    <el-form :rules="rules"
             ref="formRef"
             status-icon
             @submit.prevent="reg"
             :model="register.form"
             style="margin-top: 30px; width: 100%;"
    >
      <el-form-item prop="username">
        <el-text style="color: aliceblue">用户名</el-text>
        <el-input ref="username" class="inputBox" v-model="register.form.username" placeholder="请输入您的用户名" type="text" clearable />
      </el-form-item>
      <el-form-item prop="pwd">
        <el-text style="color: aliceblue">密码</el-text>
        <el-input ref="pwd" class="inputBox" v-model="register.form.pwd" placeholder="请输入您的密码" type="password" show-password clearable />
      </el-form-item>
      <el-form-item prop="pwd2">
        <el-text style="color: aliceblue">确认密码</el-text>
        <el-input ref="pwd2" class="inputBox" v-model="register.form.pwd2" placeholder="请再一次输入您的密码" type="password" show-password clearable />
      </el-form-item>
      <el-form-item style="margin-top: 20px;">
        <el-button native-type="submit" style="width: 100%; height: 45px;" type="primary">
          登录
        </el-button>
      </el-form-item>
      <div style="display: flex; justify-content: space-between; width: 100%; user-select: none; margin-top: 20px;">
        <el-link style="color: aliceblue">找回密码</el-link>
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
:deep(.el-link:hover:after) {
  border-color: aliceblue !important;
}
:deep(.el-input__wrapper) {
  background-color: #1C242D;
}

/* error -> #F56C6C */
:deep(.el-input) {
  --el-input-border-color: transparent !important;
  --el-input-hover-border-color: rgb(148, 163, 184) !important;
  --el-input-focus-border-color: rgb(148, 163, 184) !important;
  --el-input-text-color: aliceblue;
}

:deep(.el-input).error {
  --el-input-border-color: #F56C6C !important;
}

</style>