import { app, shell, BrowserWindow, ipcMain, session } from 'electron'
import { join } from 'path'
import { electronApp, optimizer, is } from '@electron-toolkit/utils'
import icon from './assets/icon.png?asset'
import axios from 'axios'

// 配置API基础URL，只使用生产环境配置
const API_BASE_URL = process.env.VITE_API_BASE_URL || 'http://ka.kydb.vip/api';  // 生产环境API服务器地址
console.log(`[主进程] API基础URL: ${API_BASE_URL}`);

// 是否启用API请求日志
const ENABLE_API_LOGS = process.env.VITE_ENABLE_API_LOGS === 'true';

/**
 * 主进程代理转发API请求
 */
ipcMain.handle("http-request", async (_event, { url, method = "GET", data, headers }) => {
  try {
    // 处理URL路径
    let apiUrl = url;
    if (!url.startsWith('http')) {
      // 如果不是完整URL，添加基础URL
      apiUrl = `${API_BASE_URL}${url.startsWith('/') ? url : '/' + url}`;
    }
    
    if (ENABLE_API_LOGS) {
      console.log(`[主进程API代理] 请求: ${method} ${apiUrl}`);
      console.log(`[主进程API代理] 数据:`, data);
    }
    
    // 构建请求配置
    const requestConfig = {
      url: apiUrl,
      method,
      headers,
      timeout: 30000
    };
    
    // 根据请求方法处理数据
    if (method.toUpperCase() === 'GET' && data) {
      // GET请求，将数据作为URL参数
      requestConfig.params = data;
      if (ENABLE_API_LOGS) {
        console.log(`[主进程API代理] GET参数:`, requestConfig.params);
      }
    } else if (data) {
      // 其他请求方法，将数据放在请求体中
      requestConfig.data = data;
    }
    
    // 发送请求
    const response = await axios(requestConfig);
    
    if (ENABLE_API_LOGS) {
      console.log(`[主进程API代理] 响应状态: ${response.status}`);
      console.log(`[主进程API代理] 响应数据:`, response.data);
    }
    
    return { 
      success: true, 
      data: response.data, 
      status: response.status 
    };
  } catch (error) {
    console.error("[主进程API代理] 错误:", error);
    
    // 构造错误响应
    return {
      success: false,
      message: error.message,
      status: error.response?.status || 500,
      data: error.response?.data || { message: "请求失败" }
    };
  }
});

// 确保只有一个实例在运行
const gotTheLock = app.requestSingleInstanceLock()
if (!gotTheLock) {
  app.quit()
} else {
  app.on('second-instance', (event, commandLine, workingDirectory) => {
    // 当运行第二个实例时，将焦点放到第一个实例的窗口上
    if (mainWindow) {
      if (mainWindow.isMinimized()) mainWindow.restore()
      mainWindow.focus()
    }
  })

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
      mainWindow.setBackgroundColor('#00000000');
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

    createWindow()

    app.on('activate', function () {
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
}

// In this file you can include the rest of your app"s specific main process
// code. You can also put them in separate files and require them here.
