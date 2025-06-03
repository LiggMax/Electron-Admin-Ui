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
    window.api.update.startDownloadUpdate(versionInfo)
  } else {
    console.error('更新API不可用')
  }
}

// 版本比较工具函数
export function compareVersions(newVersion, currentVersion) {
  const newParts = newVersion.split('.').map(Number)
  const currentParts = currentVersion.split('.').map(Number)

  for (let i = 0; i < Math.max(newParts.length, currentParts.length); i++) {
    const newPart = newParts[i] || 0
    const currentPart = currentParts[i] || 0

    if (newPart > currentPart) return 1
    if (newPart < currentPart) return -1
  }

  return 0
}

// 设置更新事件监听器
export function setupUpdateListeners() {
  if (!window.api || !window.api.update) {
    console.error('更新API不可用')
    return
  }

  // 这里可以设置全局的更新事件监听器
  // 具体的监听逻辑应该在组件中实现
  console.log('更新API已就绪')
} 