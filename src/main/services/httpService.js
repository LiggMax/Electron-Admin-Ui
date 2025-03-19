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
  const { url, method, data, headers } = options

  return new Promise((resolve, reject) => {
    try {
      console.log(`主进程发送请求: ${method} ${url}`, options)

      // 创建请求对象
      const request = net.request({
        method: method || 'GET',
        url,
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
          responseData += chunk.toString()
        })

        response.on('end', () => {
          console.log('主进程收到响应:', responseData)
          try {
            // 尝试解析JSON
            const parsedData = JSON.parse(responseData)
            resolve({
              statusCode: response.statusCode,
              data: parsedData
            })
          } catch (e) {
            // 如果不是JSON，返回原始数据
            resolve({
              statusCode: response.statusCode,
              data: responseData
            })
          }
        })
      })

      // 处理错误
      request.on('error', (error) => {
        console.error('主进程请求错误:', error)
        reject(error)
      })

      // 发送请求数据
      if (data && (method === 'POST' || method === 'PUT')) {
        if (typeof data === 'string') {
          request.write(data)
        } else if (data instanceof URLSearchParams) {
          request.write(data.toString())
        } else {
          request.write(JSON.stringify(data))
        }
      }

      // 结束请求
      request.end()
    } catch (error) {
      console.error('创建请求失败:', error)
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
    // 确保URL是完整的
    if (options.url && !options.url.startsWith('http')) {
      options.url = DEFAULT_BASE_URL + options.url
    }

    return sendRequest(options)
  })

  console.log('已注册HTTP请求IPC处理程序')
}

export default {
  registerIpcHandlers,
  sendRequest
}
