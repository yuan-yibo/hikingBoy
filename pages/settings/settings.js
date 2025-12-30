// pages/settings/settings.js
const app = getApp()
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    currentTheme: 'pink',
    themeList: [],
    themeColors: getInitialTheme()
  },

  onShow() {
    this.loadThemes()
    this.applyThemeColors()
  },

  // 加载主题列表
  loadThemes() {
    const themes = app.getAllThemes()
    const currentTheme = app.getCurrentTheme()
    
    const themeList = Object.keys(themes).map(key => ({
      key,
      name: themes[key].name,
      gradient: `linear-gradient(135deg, ${themes[key].primary} 0%, ${themes[key].primaryLight} 100%)`
    }))
    
    this.setData({
      themeList,
      currentTheme
    })
  },

  // 选择主题
  selectTheme(e) {
    const theme = e.currentTarget.dataset.theme
    const success = app.setTheme(theme)
    
    if (success) {
      this.setData({ currentTheme: theme })
      
      // 立即应用主题
      this.applyThemeColors()
      
      wx.showToast({
        title: '主题已切换！',
        icon: 'success',
        duration: 1500
      })
    }
  },
  
  // 应用主题颜色
  applyThemeColors() {
    const themeConfig = app.getThemeConfig()
    if (themeConfig) {
      wx.setNavigationBarColor({
        frontColor: '#ffffff',
        backgroundColor: themeConfig.primary,
        animation: {
          duration: 300,
          timingFunc: 'easeIn'
        }
      })
      
      wx.setTabBarStyle({
        selectedColor: themeConfig.primary
      })
      
      // 设置主题数据
      this.setData({
        themeColors: themeConfig
      })
    }
  },
  
  // 同步数据
  syncData() {
    wx.showLoading({
      title: '同步中...'
    })
    
    // 获取云端数据
    app.getRecords((records) => {
      wx.hideLoading()
      wx.showToast({
        title: `同步完成，共${records.length}条记录`,
        icon: 'success'
      })
    })
  }
})
