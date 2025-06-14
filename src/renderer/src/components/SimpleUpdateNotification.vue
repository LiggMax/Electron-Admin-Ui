<template>
  <transition name="notification" appear>
    <div v-if="visible" class="simple-update-notification">
      <div class="update-header">
        <span class="update-title">{{ title }}</span>
        <button @click="close" class="close-btn">×</button>
      </div>
      <div class="update-content">
        <p class="update-message">{{ message }}</p>

        <!-- 下载进度条 -->
        <div v-if="showProgress" class="progress-container">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <div class="progress-info">
            <span class="progress-text">{{ progress.toFixed(1) }}%</span>
            <span v-if="downloadSpeed" class="download-speed">{{ downloadSpeed }}</span>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div v-if="showButton" class="update-actions">
        <button @click="handleAction" class="action-btn" :class="actionType">
          {{ buttonText }}
        </button>
      </div>
    </div>
  </transition>
</template>

<script>
import { triggerUpdateCheck, startDownloadUpdate } from '../api/appversion'

export default {
  name: 'SimpleUpdateNotification',
  data() {
    return {
      visible: false,
      title: '',
      message: '',
      showProgress: false,
      progress: 0,
      showButton: false,
      buttonText: '',
      actionType: '',
      updateInfo: null, // 存储更新信息
      downloadSpeed: '',
      downloadSize: ''
    }
  },

  mounted() {
    // 检查API可用性
    console.log('=== SimpleUpdateNotification 组件挂载 ===')
    console.log('window.api可用性:', !!window.api)
    console.log('window.electron可用性:', !!window.electron)
    if (window.api && window.api.update) {
      console.log('window.api.update方法:', Object.keys(window.api.update))
    }

    this.setupUpdateListeners()
  },

  beforeUnmount() {
    this.removeListeners()
  },

  methods: {
    setupUpdateListeners() {
      if (!window.api || !window.api.update) return

      const updateApi = window.api.update

      // 检查更新中
      updateApi.onUpdateChecking(() => {
        // 检查更新过程静默进行，不显示通知
        console.log('正在后台检查更新...')
      })

      // 发现新版本
      updateApi.onUpdateAvailable((event, info) => {
        console.log('收到update-available事件，原始info:', info)
        // 转换为普通对象，避免Proxy问题
        this.updateInfo = JSON.parse(JSON.stringify(info))
        console.log('存储的updateInfo:', this.updateInfo)

        this.showNotification(
          '发现新版本',
          `发现新版本 ${info.version}\n更新内容: ${info.releaseNotes}`,
          false,
          true,
          '立即更新',
          'start-update'
        )
      })

      // 没有更新
      updateApi.onUpdateNotAvailable(() => {
      })

      // 开始下载
      updateApi.onUpdateDownloadStart((event, info) => {
        this.showNotification('开始下载', `正在下载 ${info.version}...`, true, false)
        this.progress = 0
      })

      // 下载进度
      updateApi.onUpdateDownloadProgress((event, progress) => {
        this.showNotification('下载更新', '正在下载更新...', true, false)
        this.progress = progress.percent || 0

        // 格式化下载信息
        const downloaded = this.formatBytes(progress.downloadedBytes)
        const total = this.formatBytes(progress.totalBytes)
        const speed = this.formatBytes(progress.bytesPerSecond)

        this.downloadSize = `${downloaded} / ${total}`
        this.downloadSpeed = `${speed}/s`
        this.message = `正在下载更新... ${this.downloadSize}`
      })

      // 下载完成
      updateApi.onUpdateDownloaded(() => {
        this.showNotification('下载完成', '更新下载完成，正在准备安装...', false, true, '知道了', 'close')
      })

      // 准备安装
      updateApi.onUpdateInstalling(() => {
        this.showNotification('正在安装', '正在安装更新，应用程序即将重启...', false, false)
      })

      // 手动安装（macOS）
      updateApi.onUpdateManualInstall((event, data) => {
        this.showNotification('请手动安装', data.message, false, true, '知道了', 'close')
      })

      // 更新错误
      updateApi.onUpdateError((event, error) => {
        this.showNotification('更新失败', `更新失败: ${error}`, false, true, '重试', 'retry')
      })
    },

    showNotification(title, message, showProgress = false, showButton = false, buttonText = '', actionType = '') {
      this.title = title
      this.message = message
      this.showProgress = showProgress
      this.showButton = showButton
      this.buttonText = buttonText
      this.actionType = actionType
      this.visible = true
    },

    close() {
      // 立即隐藏，让Vue transition处理动画
      this.visible = false

      // 动画完成后清理数据
      setTimeout(() => {
        this.progress = 0
        this.updateInfo = null
        this.downloadSpeed = ''
        this.downloadSize = ''
      }, 300) // 等待动画完成
    },

    handleAction() {
      console.log('handleAction被调用', {
        actionType: this.actionType,
        updateInfo: this.updateInfo,
        hasUpdateInfo: !!this.updateInfo
      })

      if (this.actionType === 'start-update' && this.updateInfo) {
        console.log('准备开始下载更新:', this.updateInfo)

        // 验证关键字段
        if (!this.updateInfo.downloadUrl) {
          console.error('缺少下载URL')
          return
        }

        // 开始下载更新
        startDownloadUpdate(this.updateInfo)

        // 不自动关闭，让用户看到下载进度

      } else if (this.actionType === 'retry') {
        console.log('重试检查更新')
        // 重试检查更新
        triggerUpdateCheck()
        this.close()
      } else if (this.actionType === 'close') {
        console.log('用户手动关闭通知')
        this.close()
      } else {
        console.log('未匹配的操作类型或缺少更新信息', {
          actionType: this.actionType,
          hasUpdateInfo: !!this.updateInfo
        })
      }
    },

    formatBytes(bytes) {
      if (bytes === 0) return '0 B'
      const k = 1024
      const sizes = ['B', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
    },

    removeListeners() {
      if (!window.api || !window.api.update) return

      const events = [
        'update-checking', 'update-available', 'update-not-available',
        'update-download-start', 'update-download-progress', 'update-downloaded',
        'update-installing', 'update-manual-install', 'update-error'
      ]

      events.forEach(event => {
        window.api.update.removeAllListeners(event)
      })
    }
  }
}
</script>

<style scoped>
/* Vue Transition 动画 */
.notification-enter-active {
  animation: slideInFromBottom 0.3s ease-out;
}

.notification-leave-active {
  animation: slideOutToBottom 0.3s ease-in;
}

.simple-update-notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 360px;
  max-width: 90vw;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  z-index: 9999;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

@keyframes slideInFromBottom {
  from {
    transform: translateY(100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes slideOutToBottom {
  from {
    transform: translateY(0);
    opacity: 1;
  }
  to {
    transform: translateY(100%);
    opacity: 0;
  }
}

.update-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

.update-title {
  font-weight: 600;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.8);
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.update-content {
  padding: 20px;
}

.update-message {
  margin: 0 0 16px 0;
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  white-space: pre-line;
}

.progress-container {
  margin-top: 16px;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 12px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #28a745, #20c997);
  transition: width 0.3s ease;
  border-radius: 4px;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-text {
  font-size: 14px;
  font-weight: 600;
  color: #28a745;
}

.download-speed {
  font-size: 12px;
  color: #6c757d;
  background: #f8f9fa;
  padding: 2px 8px;
  border-radius: 12px;
}

.update-actions {
  padding: 0 20px 20px;
  text-align: right;
}

.action-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
}

.action-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}
</style>
