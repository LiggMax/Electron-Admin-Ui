/**
 * 应用版本更新API
 */

// 触发更新检查
export function triggerUpdateCheck() {
  if (window.api && window.api.update) {
    console.log('触发手动检查更新')
    window.api.update.checkForUpdates()
  } else {
    console.error('更新API不可用')
  }
}

// 开始下载更新
export function startDownloadUpdate(versionInfo) {
  if (window.api && window.api.update) {
    console.log('开始下载更新:', versionInfo)

    // 将Proxy对象转换为普通对象，避免IPC传输错误
    const plainVersionInfo = JSON.parse(JSON.stringify(versionInfo))

    window.api.update.startDownloadUpdate(plainVersionInfo)
  } else {
    console.error('更新API不可用')
  }
}
