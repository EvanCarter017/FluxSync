<script setup lang="ts">

import { InfoFilled } from '@element-plus/icons-vue';
import {
  username,
    jwtat,
    jwtrt
} from "../composables/script";
import { NInputOtp } from 'naive-ui';
import {onMounted, ref} from "vue";
import userService from "@/services/userService";
import config from "@/store";
import service from "@/services/request";
import {ElNotification} from "element-plus";
import {useRouter} from "vue-router";
import jsCookie from 'js-cookie';

const Router = useRouter();
const otp = ref<string[]>([]);
const onlyAllowNumber = (value: string | null) => !value || /^\d+$/.test(value);

function otpFinish() {
  let str = otp.value.join('');
  service.post("/api/security/totp/verify", {
    username: username.value,
    code: str
  })
      .then(async (res) => {
        const useService = userService();
        const info = await useService.getInfo();
        if (info) {
          config.info = info;
        }
        ElNotification({
          title: "登录成功",
          message: `${username.value} 欢迎回到 FluxSync`,
          type: "success"
        });
        jsCookie.set("jwt-rt", jwtrt.value, { expires: 15 });
        jsCookie.set("jwt-at", jwtat.value, { expires: 15 });
        await Router.replace('/board');
      }).catch(e => {
        console.log(e);
        ElNotification({
          title: "登录失败",
          message: `账号或密码错误`,
          type: "error"
        });
  });
}

onMounted(() => {
  if (config.info.username) {
    Router.replace("/board");
  }
})

</script>

<template>
  <div style="display: flex; flex-direction: column;">
    
    <el-form class="box" label-position="left" label-width="90px">

      <div style="margin-bottom: 20px;">
        <el-text style="font-size: 20px; font-weight: bold; color: aliceblue;">身份验证</el-text>
      </div>

      <div
          style="margin-bottom: 10px;
           color: aliceblue;
           background-color: rgba(255, 255, 255, 0.1);
           border-radius: 5px;
           display: flex;
           align-items: center;
           height: 65px; font-size: 14px;"
      >
        <el-icon style="margin: 0 10px; font-size: 18px;">
          <el-icon><InfoFilled /></el-icon>
        </el-icon>
        为了您的账号安全, 进行敏感操作前须先验证身份
      </div>

      <el-form-item label="验证方式">
        <el-text class="desc">2FA 验证</el-text>
      </el-form-item>

      <el-form-item label="设备类型">
        <el-text style="margin-top: 6px;" class="desc">2FA 设备 (TOTP Authenticator/Google Authenticator/Microsoft Authenticator)</el-text>
      </el-form-item>

      <el-form-item label="账号ID">
        <el-text class="desc">
          {{ username }}
        </el-text>
      </el-form-item>

      <el-form-item label="2FA 动态码">
        <n-input-otp
          v-model:value="otp"
          @finish="otpFinish"
          :allow-input="onlyAllowNumber"
        />
      </el-form-item>

    </el-form>
    
  </div>
</template>

<style scoped>

.desc {
  color: aliceblue;
}

:deep(.el-form-item__label) {
  color: aliceblue;
}
:deep(.el-form-item) {
  margin-bottom: 10px;
}
:deep(.el-text) {
  line-height: 20px;
}

.box {
  border: 1px solid #1D2730;
  padding: 20px;
  box-shadow: 0 10px 25px 0 rgba(255, 255, 255, 0.1);
  border-radius: 10px;
}
</style>