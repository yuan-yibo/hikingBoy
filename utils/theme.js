// utils/theme.js

// 主题配置表
const THEMES = {
  pink: { primary: '#FF6B9D', primaryLight: '#FF8FB3', background: '#FFF5F7', shadow: 'rgba(255, 107, 157, 0.3)' },
  purple: { primary: '#9B59B6', primaryLight: '#B883D4', background: '#F8F5FA', shadow: 'rgba(155, 89, 182, 0.3)' },
  blue: { primary: '#3498DB', primaryLight: '#5DADE2', background: '#F0F8FF', shadow: 'rgba(52, 152, 219, 0.3)' },
  green: { primary: '#27AE60', primaryLight: '#52BE80', background: '#F1F9F6', shadow: 'rgba(39, 174, 96, 0.3)' },
  orange: { primary: '#FF9500', primaryLight: '#FFB84D', background: '#FFF8F0', shadow: 'rgba(255, 149, 0, 0.3)' },
  red: { primary: '#E74C3C', primaryLight: '#EC7063', background: '#FFF5F5', shadow: 'rgba(231, 76, 60, 0.3)' },
  teal: { primary: '#1ABC9C', primaryLight: '#48C9B0', background: '#F0FBF9', shadow: 'rgba(26, 188, 156, 0.3)' },
  sunset: { primary: '#FF6B6B', primaryLight: '#FFA07A', background: '#FFF5F5', shadow: 'rgba(255, 107, 107, 0.3)' }
}

/**
 * 获取初始主题配置（用于页面 data 初始化，避免闪烁）
 */
function getInitialTheme() {
  const themeName = wx.getStorageSync('appTheme') || 'pink'
  return THEMES[themeName] || THEMES.pink
}

// 主题混入对象
const themeMixin = {
  data: {
    themeColors: {}
  },
  
  onLoad() {
    this.applyTheme()
  },
  
  onShow() {
    this.applyTheme()
  },
  
  // 应用主题
  applyTheme() {
    const app = getApp()
    const themeConfig = app.getThemeConfig()
    if (themeConfig) {
      this.setData({
        themeColors: themeConfig
      })
    }
  }
}

module.exports = {
  themeMixin,
  getInitialTheme,
  THEMES
}
