import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios';
import jsCookie from 'js-cookie';
import useToken from "@/services/token";

const token = useToken();

const service: AxiosInstance = axios.create({
  baseURL: "http://localhost:1007",
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const token = jsCookie.get("jwt-at");

        if (token && config.headers) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config

    },
    error => Promise.reject(error)
);

// 响应拦截器
service.interceptors.response.use(
    response => response,
    async error => {

        const { config, response } = error;

        if (!response || response.status !== 401) return Promise.reject(error);

        if (config._retry) return Promise.reject(error);

        config._retry = true;

        const rtToken = jsCookie.get('jwt-rt');
        const atToken = await token.refresh(rtToken);

        if (atToken !== -1) jsCookie.set("jwt-at", atToken, { expires: 7 });

        config.headers.Authorization = `Bearer ${atToken}`;
        return service(config);
    }
);

export default service;