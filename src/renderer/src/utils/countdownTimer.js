import { ref } from 'vue'

/**
 * 倒计时工具类
 * 提供一个可重用的倒计时功能
 */
class CountdownTimer {
  /**
   * 创建倒计时实例
   */
  constructor() {
    this.countdown = ref(0)
    this.timer = null
    this.callbacks = {
      onTick: null,
      onComplete: null
    }
  }

  /**
   * 获取当前倒计时值
   * @returns {import('vue').Ref<number>} 倒计时响应式引用
   */
  getCountdown() {
    return this.countdown
  }

  /**
   * 检查倒计时是否正在进行
   * @returns {boolean} 是否在倒计时中
   */
  isRunning() {
    return this.countdown.value > 0
  }

  /**
   * 开始倒计时
   * @param {number} seconds 倒计时秒数
   * @param {Object} options 配置选项
   * @param {Function} options.onTick 每次倒计时变化时的回调
   * @param {Function} options.onComplete 倒计时完成时的回调
   */
  start(seconds, options = {}) {
    // 如果已经在倒计时，先清除之前的定时器
    if (this.timer) {
      clearInterval(this.timer)
    }

    // 设置回调函数
    this.callbacks.onTick = options.onTick || null
    this.callbacks.onComplete = options.onComplete || null

    // 设置初始倒计时值
    this.countdown.value = seconds || 0

    // 如果倒计时值为0，则直接完成
    if (this.countdown.value <= 0) {
      if (this.callbacks.onComplete) {
        this.callbacks.onComplete()
      }
      return
    }

    // 启动定时器
    this.timer = setInterval(() => {
      this.countdown.value--
      
      // 触发onTick回调
      if (this.callbacks.onTick) {
        this.callbacks.onTick(this.countdown.value)
      }

      // 检查是否结束
      if (this.countdown.value <= 0) {
        this.stop()
        
        // 触发onComplete回调
        if (this.callbacks.onComplete) {
          this.callbacks.onComplete()
        }
      }
    }, 1000)
  }

  /**
   * 停止倒计时
   */
  stop() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
  }

  /**
   * 重置倒计时
   */
  reset() {
    this.stop()
    this.countdown.value = 0
  }
}

export default CountdownTimer 