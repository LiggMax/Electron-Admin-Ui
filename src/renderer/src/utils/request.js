/**
 * 请求工具类 - 全面采用主进程发送请求
 * 完全避开CSP限制和CORS问题
 */

// 基础URL
const BASE_URL = 'http://127.0.0.1:8090'

/**
 * 通过主进程发送HTTP请求
 * @param {Object} options - 请求选项
 * @returns {Promise} - 返回Promise对象
 */
const requestViaMainProcess = async (options) => {
  try {
    console.log('通过主进程发送请求:', options)

    if (!options.url.startsWith('http')) {
      options.url = BASE_URL + options.url
    }

    // 调用预加载脚本中暴露的http请求方法
    const result = await window.api.http.request(options)

    console.log('主进程请求响应:', result)

    // 处理响应
    if (result.statusCode === 200) {
      const data = result.data

      // 业务状态码检查
      if (typeof data === 'object' && data.code === 200) {
        return data
      } else if (typeof data === 'object' && data.code !== 200) {
        throw new Error(data.message || '业务错误')
      }

      return data
    }

    throw new Error(`HTTP错误: ${result.statusCode}`)
  } catch (error) {
    console.error('请求失败:', error)
    throw error
  }
}

/**
 * GET请求
 * @param {string} url - 请求URL
 * @param {Object} params - 请求参数
 * @param {Object} config - 附加配置
 * @returns {Promise} - 返回Promise对象
 */
export const get = (url, params = {}, config = {}) => {
  // 将参数转换为URL查询字符串
  const queryString = new URLSearchParams(params).toString()
  const fullUrl = queryString ? `${url}?${queryString}` : url

  return requestViaMainProcess({
    method: 'GET',
    url: fullUrl,
    ...config
  })
}

/**
 * POST请求 - 表单格式
 * @param {string} url - 请求URL
 * @param {Object|URLSearchParams} data - 请求数据
 * @param {Object} config - 附加配置
 * @returns {Promise} - 返回Promise对象
 */
export const post = (url, data = {}, config = {}) => {
  // 处理不同类型的数据
  let postData = data
  let headers = {
    'Content-Type': 'application/x-www-form-urlencoded',
    ...config.headers
  }

  // 如果数据不是URLSearchParams实例，将其转换
  if (!(data instanceof URLSearchParams) && typeof data === 'object') {
    const params = new URLSearchParams()
    Object.keys(data).forEach((key) => {
      params.append(key, data[key])
    })
    postData = params.toString()
  } else if (data instanceof URLSearchParams) {
    postData = data.toString()
  }

  return requestViaMainProcess({
    method: 'POST',
    url,
    data: postData,
    headers,
    ...config
  })
}

/**
 * POST请求 - JSON格式
 * @param {string} url - 请求URL
 * @param {Object} data - 请求数据
 * @param {Object} config - 附加配置
 * @returns {Promise} - 返回Promise对象
 */
export const postJSON = (url, data = {}, config = {}) => {
  return requestViaMainProcess({
    method: 'POST',
    url,
    data: typeof data === 'string' ? data : JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json',
      ...config.headers
    },
    ...config
  })
}

/**
 * 导出请求方法和工具函数
 */
export default {
  get,
  post,
  postJSON,
  request: requestViaMainProcess
}
