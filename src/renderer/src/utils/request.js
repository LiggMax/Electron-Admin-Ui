import axios from 'axios';
import {ElMessage} from "element-plus";
import { userTokenStore } from '../store/token'
//定义一个变量,记录公共的前缀  ,  baseURL
const baseURL = 'http://127.0.0.1:8090/api';
const instance = axios.create({baseURL})
//添加请求拦截器
instance.interceptors.request.use(
  config => {
    //请求前回调
    //添加token
    const tokenStore = userTokenStore();
    if (tokenStore.token) {
      config.headers.Token = tokenStore.token;
    }
    return config;
  },
  (err) => {
    //请求失败回调
    return Promise.reject(err);//异步的状态转化成失败的状态
  }
)

//添加响应拦截器
instance.interceptors.response.use(
  result => {
    //判断业务状态码
    if (result.data.code === 200) {
      //返回数据
      return result.data;
    }
    //提示错误信息
    ElMessage.error(result.data.message?result.data.message:'服务异常')
    //异步操作状态转换为失败
    return Promise.reject(result.data);
  },
  err => {
    if (err.response.status === 429) {
      ElMessage.error('请求过于频繁，请稍后再试')
    } else if (err.response.status === 401) {
      ElMessage.error('请先登录')
      //清除token
      userTokenStore().removeToken();
    } else {
      ElMessage.error('服务异常')
    }
    return Promise.reject(err);//异步的状态转化成失败的状态
  }
)

export default instance;
