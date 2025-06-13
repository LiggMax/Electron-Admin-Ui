import { app, shell, BrowserWindow, ipcMain, autoUpdater, dialog } from 'electron'
import { join } from 'path'
import { electronApp, optimizer, is } from '@electron-toolkit/utils'
import icon from './assets/icon.png?asset'
import axios from 'axios'

// 配置API基础URL，只使用生产环境配置
const API_BASE_URL = process.env.VITE_API_BASE_URL || 'http://ka.kydb.vip/api'  // 生产环境API服务器地址
console.log(`[主进程] API基础URL: ${API_BASE_URL}`)

// 是否启用API请求日志 - 生产环境下默认启用
const ENABLE_API_LOGS = process.env.VITE_ENABLE_API_LOGS !== 'false' // 默认启用，只有明确设置为false才禁用
console.log(`[主进程] API日志状态: ${ENABLE_API_LOGS ? '启用' : '禁用'}`)

/**
 * 主进程代理转发API请求
 */
ipcMain.handle('http-request', async (_event, { url, method = 'GET', data, headers }) => {
  try {
    // 处理URL路径
    let apiUrl = url
    if (!url.startsWith('http')) {
      // 如果不是完整URL，添加基础URL
      apiUrl = `${API_BASE_URL}${url.startsWith('/') ? url : '/' + url}`
    }

    if (ENABLE_API_LOGS) {
      console.log(`[主进程API代理] 请求: ${method} ${apiUrl}`)
      if (data) {
        console.log(`[主进程API代理] 请求数据:`, JSON.stringify(data, null, 2))
      }
    }

    // 构建请求配置
    const requestConfig = {
      url: apiUrl,
      method,
      headers,
      timeout: 30000
    }

    // 根据请求方法处理数据
    if (method.toUpperCase() === 'GET' && data) {
      // GET请求，将数据作为URL参数
      requestConfig.params = data
      if (ENABLE_API_LOGS) {
        console.log(`[主进程API代理] GET参数:`, JSON.stringify(requestConfig.params, null, 2))
      }
    } else if (data) {
      // 其他请求方法，将数据放在请求体中
      requestConfig.data = data
    }

    // 发送请求
    const response = await axios(requestConfig)

    if (ENABLE_API_LOGS) {
      console.log(`[主进程API代理] 响应状态: ${response.status}`)
      console.log(`[主进程API代理] 响应数据:`, JSON.stringify(response.data, null, 2))
    }

    return {
      success: true,
      data: response.data,
      status: response.status
    }
  } catch (error) {
    console.error('[主进程API代理] 错误:', error.message)
    if (error.response && ENABLE_API_LOGS) {
      console.error('[主进程API代理] 错误响应数据:', JSON.stringify(error.response.data, null, 2))
    }

    // 构造错误响应
    return {
      success: false,
      message: error.message,
      status: error.response?.status || 500,
      data: error.response?.data || { message: '请求失败' }
    }
  }
})

// 确保只有一个实例在运行
app.requestSingleInstanceLock()
// 创建主窗口
let mainWindow = null

function createWindow() {
  // Create the browser window.
  mainWindow = new BrowserWindow({
    width: 1000,
    height: 700,
    minHeight: 500,
    minWidth: 800,
    show: false,
    autoHideMenuBar: true,
    frame: false,
    transparent: true,
    backgroundColor: '#00000000',
    ...(process.platform === 'linux' ? { icon } : {}),
    webPreferences: {
      preload: join(__dirname, '../preload/index.js'),
      sandbox: false,
      allowRunningInsecureContent: true
    }
  })

  // 设置窗口圆角
  if (process.platform === 'win32') {
    mainWindow.setBackgroundColor('#00000000')
  }

  // 添加窗口控制事件处理
  ipcMain.on('window-minimize', () => {
    mainWindow.minimize()
  })

  ipcMain.on('window-maximize', () => {
    if (mainWindow.isMaximized()) {
      mainWindow.unmaximize()
    } else {
      mainWindow.maximize()
    }
  })

  ipcMain.on('window-close', () => {
    mainWindow.close()
  })

  // 监听窗口最大化状态变化并通知渲染进程
  mainWindow.on('maximize', () => {
    mainWindow.webContents.send('window-state-changed', true)
  })

  mainWindow.on('unmaximize', () => {
    mainWindow.webContents.send('window-state-changed', false)
  })

  mainWindow.on('ready-to-show', () => {
    mainWindow.show()
  })

  mainWindow.webContents.setWindowOpenHandler((details) => {
    shell.openExternal(details.url)
    return { action: 'deny' }
  })

  // HMR for renderer base on electron-vite cli.
  // Load the remote URL for development or the local html file for production.
  if (is.dev && process.env['ELECTRON_RENDERER_URL']) {
    mainWindow.loadURL(process.env['ELECTRON_RENDERER_URL'])
  } else {
    mainWindow.loadFile(join(__dirname, '../renderer/index.html'))
  }

  // 窗口关闭时触发
  mainWindow.on('closed', () => {
    mainWindow = null
  })
}

// 在Windows平台上设置圆角和透明支持
if (process.platform === 'win32') {
  app.commandLine.appendSwitch('enable-transparent-visuals')
  app.commandLine.appendSwitch('disable-features', 'OutOfBlinkCors')
  // 注释掉硬件加速禁用，因为这可能影响性能
  // app.disableHardwareAcceleration()
}

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.whenReady().then(() => {
  // 设置 CSP
  // session.defaultSession.webRequest.onHeadersReceived((details, callback) => {
  //   callback({
  //     responseHeaders: {
  //       ...details.responseHeaders,
  //       'Content-Security-Policy': ["default-src 'self' 'unsafe-inline' 'unsafe-eval'; img-src 'self' data: https: http: *; connect-src *"]
  //     }
  //   })
  // })

  // Set app user model id for windows
  electronApp.setAppUserModelId('com.electron')

  // Default open or close DevTools by F12 in development
  // and ignore CommandOrControl + R in production.
  // see https://github.com/alex8088/electron-toolkit/tree/master/packages/utils
  app.on('browser-window-created', (_, window) => {
    optimizer.watchWindowShortcuts(window)
  })

  // 手动检查更新（调用后端API）
  ipcMain.on('check-for-updates', () => {
    console.log('手动检查更新请求')
    checkForUpdatesFromAPI()
  })

  // 用户确认开始下载更新
  ipcMain.on('start-download-update', (event, versionInfo) => {
    console.log('=== 收到下载更新请求 ===')
    console.log('versionInfo:', JSON.stringify(versionInfo, null, 2))
    console.log('开始执行下载流程...')

    try {
      downloadAndInstallUpdate(versionInfo)
      console.log('下载流程启动成功')
    } catch (error) {
      console.error('启动下载流程时出错:', error)
    }
  })

  // 设置自动更新器
  setupAutoUpdater()

  // 启动时检查更新（延迟5秒，避免影响启动速度）
  setTimeout(() => {
    checkForUpdatesFromAPI()
  }, 5000)

  // 每小时自动检查一次更新
  setInterval(() => {
    checkForUpdatesFromAPI()
  }, 60 * 60 * 1000) // 1小时

  createWindow()

  app.on('activate', function() {
    // On macOS it's common to re-create a window in the app when the
    // dock icon is clicked and there are no other windows open.
    if (BrowserWindow.getAllWindows().length === 0) createWindow()
  })
})

// Quit when all windows are closed, except on macOS. There, it's common
// for applications and their menu bar to stay active until the user quits
// explicitly with Cmd + Q.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

// 设置自动更新器
function setupAutoUpdater() {
  // 只在生产环境启用自动更新
  if (is.dev) {
    console.log('开发环境，跳过自动更新配置')
    return
  }

  // autoUpdater事件监听
  autoUpdater.on('checking-for-update', () => {
    console.log('正在检查更新...')
  })

  autoUpdater.on('update-available', (info) => {
    console.log('发现新版本:', info)
  })

  autoUpdater.on('update-not-available', (info) => {
    console.log('已是最新版本:', info)
  })

  autoUpdater.on('error', (err) => {
    console.error('自动更新错误:', err)
  })

  autoUpdater.on('download-progress', (progressObj) => {
    let log_message = '下载速度: ' + progressObj.bytesPerSecond
    log_message = log_message + ' - 已下载 ' + progressObj.percent + '%'
    log_message = log_message + ' (' + progressObj.transferred + '/' + progressObj.total + ')'
    console.log(log_message)
  })

  autoUpdater.on('update-downloaded', (info) => {
    console.log('更新下载完成:', info)

    // 显示更新对话框
    const dialogOpts = {
      type: 'info',
      buttons: ['立即重启', '稍后'],
      title: '应用程序更新',
      message: '新版本已下载完成',
      detail: '新版本已经下载完成，重启应用程序以应用更新。'
    }

    dialog.showMessageBox(dialogOpts).then((returnValue) => {
      if (returnValue.response === 0) {
        autoUpdater.quitAndInstall()
      }
    })
  })
}

// 调用后端API检查更新（管理端app=1）
async function checkForUpdatesFromAPI() {
  try {
    const currentVersion = app.getVersion()
    console.log('当前版本:', currentVersion)

    // 通知渲染进程开始检查更新
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-checking')
    })

    const response = await axios.get(`${API_BASE_URL}/version/check`, {
      params: {
        version: currentVersion,
        app: 1  // 管理端应用
      },
      timeout: 10000
    })

    if (response.data && response.data.code === 200 && response.data.data && response.data.data.length > 0) {
      const latestVersion = response.data.data[0]
      console.log('检查到新版本:', latestVersion)

      // 比较版本号
      if (isVersionNewer(latestVersion.version, currentVersion)) {
        console.log('发现新版本，通知渲染进程')

        // 直接通知渲染进程发现新版本，不显示对话框
        BrowserWindow.getAllWindows().forEach(win => {
          win.webContents.send('update-available', latestVersion)
        })

      } else {
        console.log('当前已是最新版本')
        // 通知渲染进程没有更新
        BrowserWindow.getAllWindows().forEach(win => {
          win.webContents.send('update-not-available')
        })
      }
    } else {
      console.log('没有发现新版本')
      // 通知渲染进程没有更新
      BrowserWindow.getAllWindows().forEach(win => {
        win.webContents.send('update-not-available')
      })
    }
  } catch (error) {
    console.error('检查更新失败:', error)
    // 通知渲染进程更新检查失败
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-error', error.message)
    })
  }
}

// 下载并安装更新
async function downloadAndInstallUpdate(versionInfo) {
  try {
    const fs = require('fs')
    const path = require('path')

    console.log('开始下载更新文件:', versionInfo.downloadUrl)

    // 通知渲染进程开始下载
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-download-start', versionInfo)
    })

    // 创建下载目录
    const downloadDir = path.join(app.getPath('temp'), 'app-updates')
    if (!fs.existsSync(downloadDir)) {
      fs.mkdirSync(downloadDir, { recursive: true })
    }

    // 根据平台确定文件扩展名
    const platform = process.platform
    let fileName = `update-${versionInfo.version}`
    if (platform === 'win32') {
      fileName += '.exe'
    } else if (platform === 'darwin') {
      fileName += '.dmg'
    } else {
      fileName += '.AppImage'
    }

    const filePath = path.join(downloadDir, fileName)

    // 创建文件写入流
    const writer = fs.createWriteStream(filePath)

    // 下载文件
    const response = await axios({
      method: 'GET',
      url: versionInfo.downloadUrl,
      responseType: 'stream',
      timeout: 300000 // 5分钟超时
    })

    const totalLength = parseInt(response.headers['content-length'] || '0')
    let downloadedLength = 0
    let startTime = Date.now()

    response.data.on('data', (chunk) => {
      downloadedLength += chunk.length
      const progress = totalLength > 0 ? (downloadedLength / totalLength * 100) : 0
      const elapsed = (Date.now() - startTime) / 1000
      const speed = downloadedLength / elapsed

      const progressInfo = {
        percent: progress,
        downloadedBytes: downloadedLength,
        totalBytes: totalLength,
        bytesPerSecond: speed
      }

      // 只通知渲染进程下载进度
      BrowserWindow.getAllWindows().forEach(win => {
        win.webContents.send('update-download-progress', progressInfo)
      })
    })

    response.data.pipe(writer)

    await new Promise((resolve, reject) => {
      writer.on('finish', resolve)
      writer.on('error', reject)
    })

    console.log('下载完成:', filePath)

    // 通知渲染进程下载完成
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-downloaded', {
        filePath: filePath,
        version: versionInfo.version
      })
    })

    // 自动开始安装
    await installUpdate(filePath)

  } catch (error) {
    console.error('下载更新失败:', error)

    // 通知渲染进程下载失败
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-error', error.message)
    })
  }
}

// 安装更新
async function installUpdate(filePath) {
  try {
    const { execFile, spawn } = require('child_process')
    const platform = process.platform

    console.log('开始安装更新:', filePath)

    // 通知渲染进程准备安装
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-installing', { filePath })
    })

    if (platform === 'win32') {
      // Windows: 启动安装程序
      spawn(filePath, ['/S'], {
        detached: true,
        stdio: 'ignore'
      })

      // 延迟关闭当前应用
      setTimeout(() => {
        app.quit()
      }, 2000)

    } else if (platform === 'darwin') {
      // macOS: 打开DMG文件
      const { exec } = require('child_process')
      exec(`open "${filePath}"`, (error) => {
        if (error) {
          console.error('打开DMG失败:', error)
          BrowserWindow.getAllWindows().forEach(win => {
            win.webContents.send('update-error', '无法打开安装文件')
          })
        } else {
          // 通知用户手动安装
          BrowserWindow.getAllWindows().forEach(win => {
            win.webContents.send('update-manual-install', {
              message: '已打开安装文件，请手动完成安装过程'
            })
          })
        }
      })

    } else {
      // Linux: 启动AppImage
      execFile('chmod', ['+x', filePath], (error) => {
        if (error) {
          console.error('设置执行权限失败:', error)
          BrowserWindow.getAllWindows().forEach(win => {
            win.webContents.send('update-error', '设置执行权限失败')
          })
          return
        }

        spawn(filePath, [], {
          detached: true,
          stdio: 'ignore'
        })

        setTimeout(() => {
          app.quit()
        }, 2000)
      })
    }

  } catch (error) {
    console.error('安装更新失败:', error)
    BrowserWindow.getAllWindows().forEach(win => {
      win.webContents.send('update-error', error.message)
    })
  }
}

// 版本比较函数
function isVersionNewer(newVersion, currentVersion) {
  const newParts = newVersion.split('.').map(Number)
  const currentParts = currentVersion.split('.').map(Number)

  for (let i = 0; i < Math.max(newParts.length, currentParts.length); i++) {
    const newPart = newParts[i] || 0
    const currentPart = currentParts[i] || 0

    if (newPart > currentPart) return true
    if (newPart < currentPart) return false
  }

  return false
}

// In this file you can include the rest of your app"s specific main process
// code. You can also put them in separate files and require them here.
