/**
 * 主进程HTTP服务
 * 处理来自渲染进程的HTTP请求
 */
import { ipcMain, net } from 'electron'

// 统一的URL地址
const DEFAULT_BASE_URL = 'http://127.0.0.1:8090'
/**
 * 通过Electron net模块发送HTTP请求
 * @param {Object} options - 请求选项
 * @returns {Promise} - 包含响应数据的Promise
 */
function sendRequest(options) {
  const { url, method, data, params, headers } = options

  return new Promise((resolve, reject) => {
    try {
      console.log(`[主进程] 发送请求: ${method} ${url}`, {
        headers,
        data,
        params
      })

      // 处理请求URL
      let requestUrl = url

      // 1. 如果URL不是以http开头，添加基础URL
      if (!url.startsWith('http')) {
        requestUrl = `${DEFAULT_BASE_URL}${url.startsWith('/') ? url : '/' + url}`
      }

      // 2. 如果有查询参数，添加到URL中
      if (params && Object.keys(params).length > 0) {
        const queryString = Object.entries(params)
          .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
          .join('&')

        requestUrl += (requestUrl.includes('?') ? '&' : '?') + queryString
      }

      // 创建请求对象
      const request = net.request({
        method: method || 'GET',
        url: requestUrl,
        redirect: 'follow'
      })

      // 添加请求头
      if (headers) {
        Object.keys(headers).forEach((key) => {
          request.setHeader(key, headers[key])
        })
      }

      // 接收响应数据
      let responseData = ''

      request.on('response', (response) => {
        response.on('data', (chunk) => {
          responseData += chunk.toString('utf8')  // 明确指定utf8编码
        })

        response.on('end', () => {
          console.log('[主进程] 收到响应:', {
            statusCode: response.statusCode,
            headers: response.headers
          })

          // 打印响应数据（格式化显示）
          try {
            // 尝试解析JSON
            const parsedData = JSON.parse(responseData)
            console.log('[主进程] 响应数据:', JSON.stringify(parsedData, null, 2))

            resolve({
              statusCode: response.statusCode,
              data: parsedData
            })
          } catch (e) {
            // 如果不是JSON，返回原始数据
            console.log('[主进程] 响应数据(非JSON):', responseData)

            resolve({
              statusCode: response.statusCode,
              data: responseData
            })
          }
        })
      })

      // 处理错误
      request.on('error', (error) => {
        console.error('[主进程] 请求错误:', error)
        reject(error)
      })

      // 发送请求数据
      if (data && (method === 'POST' || method === 'PUT')) {
        let postData

        // 处理数据格式
        if (typeof data === 'string') {
          postData = data
        } else if (data instanceof URLSearchParams) {
          postData = data.toString()
        } else if (typeof data === 'object') {
          // 根据Content-Type决定如何处理数据
          const contentType = headers && headers['Content-Type'] ? headers['Content-Type'] : ''

          if (contentType.includes('application/json')) {
            postData = JSON.stringify(data)
          } else if (contentType.includes('application/x-www-form-urlencoded')) {
            // 将对象转换为URL编码的表单数据
            const params = new URLSearchParams()
            Object.keys(data).forEach(key => {
              params.append(key, data[key])
            })
            postData = params.toString()
          } else {
            // 默认为JSON
            postData = JSON.stringify(data)
          }
        }

        if (postData) {
          console.log('[主进程] 发送数据:', postData)
          request.write(postData)
        }
      }

      // 结束请求
      request.end()
    } catch (error) {
      console.error('[主进程] 创建请求失败:', error)
      reject(error)
    }
  })
}

/**
 * 注册IPC处理程序
 */
function registerIpcHandlers() {
  // 处理HTTP请求
  ipcMain.handle('http-request', async (event, options) => {
    try {
      return await sendRequest(options)
    } catch (error) {
      console.error('[主进程] IPC请求处理错误:', error)
      return {
        statusCode: 500,
        data: {
          code: 500,
          message: error.message || '请求处理失败'
        }
      }
    }
  })

  console.log('[主进程] 已注册HTTP请求IPC处理程序')
}

export default {
  registerIpcHandlers,
  sendRequest
}
