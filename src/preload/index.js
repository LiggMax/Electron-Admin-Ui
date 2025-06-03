import { contextBridge, ipcRenderer } from 'electron'
import { electronAPI } from '@electron-toolkit/preload'

// Custom APIs for renderer
const api = {
  // 添加窗口控制API
  window: {
    minimize: () => ipcRenderer.send('window-minimize'),
    maximize: () => ipcRenderer.send('window-maximize'),
    close: () => ipcRenderer.send('window-close')
  },
  // 添加HTTP请求API，通过主进程发送
  http: {
    request: (options) => ipcRenderer.invoke('http-request', options)
  },
  // 自动更新相关API
  update: {
    // 手动检查更新
    checkForUpdates: () => ipcRenderer.send('check-for-updates'),
    // 开始下载更新
    startDownloadUpdate: (versionInfo) => ipcRenderer.send('start-download-update', versionInfo),
    // 事件监听器
    onUpdateChecking: (callback) => ipcRenderer.on('update-checking', callback),
    onUpdateAvailable: (callback) => ipcRenderer.on('update-available', callback),
    onUpdateNotAvailable: (callback) => ipcRenderer.on('update-not-available', callback),
    onUpdateDownloadStart: (callback) => ipcRenderer.on('update-download-start', callback),
    onUpdateDownloadProgress: (callback) => ipcRenderer.on('update-download-progress', callback),
    onUpdateDownloaded: (callback) => ipcRenderer.on('update-downloaded', callback),
    onUpdateInstalling: (callback) => ipcRenderer.on('update-installing', callback),
    onUpdateManualInstall: (callback) => ipcRenderer.on('update-manual-install', callback),
    onUpdateError: (callback) => ipcRenderer.on('update-error', callback),
    // 移除事件监听器
    removeAllListeners: (channel) => ipcRenderer.removeAllListeners(channel)
  }
}

// Use `contextBridge` APIs to expose Electron APIs to
// renderer only if context isolation is enabled, otherwise
// just add to the DOM global.
if (process.contextIsolated) {
  try {
    contextBridge.exposeInMainWorld('electron', electronAPI)
    contextBridge.exposeInMainWorld('api', api)
  } catch (error) {
    console.error(error)
  }
} else {
  window.electron = electronAPI
  window.api = api
}
