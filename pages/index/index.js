// pages/index/index.js
const app = getApp()
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    records: [],
    themeColors: getInitialTheme()
  },

  onLoad() {
    this.loadRecords()
    this.applyTheme()
  },

  onShow() {
    this.loadRecords()
    this.applyTheme()
  },

  // 加载记录
  loadRecords() {
    // 显示加载状态
    wx.showLoading({
      title: '加载中...',
      mask: true
    })
    
    const app = getApp()
    app.getRecords((records) => {
      // 隐藏加载状态
      wx.hideLoading()
      
      // 添加动画效果
      const recordsWithAnimation = records.map((record, index) => ({
        ...record,
        animation: true
      }))
      this.setData({
        records: recordsWithAnimation
      })
    })
  },

  // 添加记录
  addRecord() {
    wx.navigateTo({
      url: '/pages/add/add'
    })
  },

  // 编辑记录
  editRecord(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/add/add?id=${id}`
    })
  },

  // 删除记录
  deleteRecord(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条记录吗？',
      confirmColor: '#FF6B9D',
      success: (res) => {
        if (res.confirm) {
          app.deleteRecord(id, (success, result) => {
            if (success) {
              this.loadRecords()
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              })
            } else {
              wx.showToast({
                title: '删除失败',
                icon: 'error'
              })
            }
          })
        }
      }
    })
  },

  // 查看详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/detail/detail?id=${id}`
    })
  },
  
  // 应用主题颜色
  applyTheme() {
    const themeConfig = app.getThemeConfig()
    if (themeConfig) {
      // 设置导航栏和TabBar
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
  }
})
