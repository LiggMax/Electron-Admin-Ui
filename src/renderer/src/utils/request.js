import axios from 'axios';
import { ElMessage } from "element-plus";
import { userTokenStore } from '../store/token'
//导入路由
import router from '../router'

// 从环境变量中获取API基础URL
const baseURL = import.meta.env.VITE_API_BASE_URL || '/api';
console.log(`[渲染进程] API基础URL: ${baseURL}`);

// 判断是否为生产环境
const isProd = import.meta.env.PROD;
// 是否启用API日志 - 生产环境下默认启用
const enableLogs = import.meta.env.VITE_ENABLE_API_LOGS !== 'false'; // 默认启用，只有明确设置为false才禁用
console.log(`[渲染进程] 当前环境: ${isProd ? '生产' : '开发'}`);
console.log(`[渲染进程] API日志状态: ${enableLogs ? '启用' : '禁用'}`);

const instance = axios.create({
  baseURL,
  timeout: 20000 // 设置20秒超时
})

// 创建适配器，用于在生产环境中通过主进程代理发送请求
const electronAdapter = async (config) => {
  // 开发环境直接使用axios默认适配器
  if (!isProd) {
    return axios.defaults.adapter(config);
  }

  try {
    // 从config中提取需要的参数
    const { url, method, data, params, headers } = config;
    
    if (enableLogs) {
      console.log(`[渲染进程请求] ${method.toUpperCase()} ${url}`);
      const requestData = method.toUpperCase() === 'GET' ? params : data;
      if (requestData) {
        console.log(`[渲染进程请求数据]`, JSON.stringify(requestData, null, 2));
      }
    }
    
    // 通过预加载脚本暴露的API发送请求
    const response = await window.api.http.request({
      url: url,
      method,
      // 对于GET请求，使用params作为data传递
      data: method.toUpperCase() === 'GET' ? params : data,
      headers
    });
    
    if (enableLogs) {
      console.log(`[渲染进程响应]`, JSON.stringify(response, null, 2));
    }
    
    if (!response.success) {
      // 请求失败
      return Promise.reject({
        response: {
          status: response.status,
          data: response.data
        },
        message: response.message
      });
    }
    
    // 构造axios响应格式
    return {
      data: response.data,
      status: response.status,
      statusText: 'OK',
      headers: {},
      config,
      request: {}
    };
  } catch (error) {
    console.error(`[渲染进程请求错误]`, error);
    return Promise.reject(error);
  }
};

// 仅在生产环境中设置适配器
if (isProd) {
  instance.defaults.adapter = electronAdapter;
}

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
    ElMessage.error('请求发送失败，请检查网络连接');
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
    ElMessage.error(result.data.message || '服务异常');
    //异步操作状态转换为失败
    return Promise.reject(result.data);
  },
  err => {
    // 处理HTTP错误状态
    if (err.response) {
      if (err.response.status === 429) {
        ElMessage.error('请求过于频繁，请稍后再试');
      } else if (err.response.status === 401) {
        // ElMessage.error('请先登录');
        //清除token
        userTokenStore().removeToken();
        //跳转到登录页面
        router.push('/login')
      } else if (err.response.status === 404) {
        ElMessage.error('请求的资源不存在');
      } else if (err.response.status === 500) {
        ElMessage.error('服务器内部错误');
      } else {
        ElMessage.error(`请求失败(${err.response.status}): ${err.response.data?.message || '未知错误'}`);
      }
    } else if (err.request) {
      // 请求发出但没有收到响应
      ElMessage.error('服务器无响应，请检查网络连接');
    } else {
      // 请求配置有误
      ElMessage.error('请求配置错误: ' + err.message);
    }
    return Promise.reject(err);//异步的状态转化成失败的状态
  }
)

export default instance;
