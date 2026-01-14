// app.js
const api = require('./utils/api.js')

App({
  onLaunch() {
    // 初始化本地存储（用于存储主题等配置）
    const theme = wx.getStorageSync('appTheme') || 'pink'
    this.globalData.currentTheme = theme
    
    // 应用主题
    this.applyThemeOnLaunch(theme)
    
    // 初始化用户ID
    api.getUserId()
    
    // 自动登录
    this.initUser()
    
    console.log('应用初始化完成，后端地址:', api.BASE_URL)
  },
  
  // 初始化用户
  async initUser() {
    try {
      const ownerId = api.getOwnerId()
      if (!ownerId) {
        // 首次使用，自动登录
        await api.login(api.getUserId(), '徒步爱好者', '')
        console.log('用户自动登录成功')
      }
    } catch (err) {
      console.error('用户初始化失败:', err)
    }
  },
  
  globalData: {
    currentTheme: 'green',
    themes: {
      green: {
        name: '清新绿',
        primary: '#07C160',
        primaryLight: '#10B981',
        background: '#F5F5F5',
        backgroundGradient: 'linear-gradient(135deg, #07C160 0%, #10B981 100%)',
        cardGradient: 'linear-gradient(135deg, #E8F5E9 0%, #F1F8E9 100%)',
        shadow: 'rgba(7, 193, 96, 0.3)'
      },
      pink: {
        name: '甜蜜粉',
        primary: '#FF6B9D',
        primaryLight: '#FF8FB3',
        background: '#FFF5F7',
        backgroundGradient: 'linear-gradient(135deg, #FFF5F7 0%, #FFE8F0 100%)',
        cardGradient: 'linear-gradient(135deg, #FFE8F0 0%, #FFF0F5 100%)',
        shadow: 'rgba(255, 107, 157, 0.3)'
      },
      purple: {
        name: '梦幻紫',
        primary: '#9B59B6',
        primaryLight: '#B883D4',
        background: '#F8F5FA',
        backgroundGradient: 'linear-gradient(135deg, #F8F5FA 0%, #EFE8F4 100%)',
        cardGradient: 'linear-gradient(135deg, #E8DAEF 0%, #F4ECF7 100%)',
        shadow: 'rgba(155, 89, 182, 0.3)'
      },
      blue: {
        name: '清新蓝',
        primary: '#3498DB',
        primaryLight: '#5DADE2',
        background: '#F0F8FF',
        backgroundGradient: 'linear-gradient(135deg, #F0F8FF 0%, #E3F2FD 100%)',
        cardGradient: 'linear-gradient(135deg, #D6EAF8 0%, #EBF5FB 100%)',
        shadow: 'rgba(52, 152, 219, 0.3)'
      },
      green: {
        name: '清新绿',
        primary: '#27AE60',
        primaryLight: '#52BE80',
        background: '#F1F9F6',
        backgroundGradient: 'linear-gradient(135deg, #F1F9F6 0%, #E8F6F3 100%)',
        cardGradient: 'linear-gradient(135deg, #D5F4E6 0%, #EAFAF1 100%)',
        shadow: 'rgba(39, 174, 96, 0.3)'
      },
      orange: {
        name: '阳光橙',
        primary: '#FF9500',
        primaryLight: '#FFB84D',
        background: '#FFF8F0',
        backgroundGradient: 'linear-gradient(135deg, #FFF8F0 0%, #FFEDDB 100%)',
        cardGradient: 'linear-gradient(135deg, #FFE8CC 0%, #FFF4E6 100%)',
        shadow: 'rgba(255, 149, 0, 0.3)'
      },
      red: {
        name: '热情红',
        primary: '#E74C3C',
        primaryLight: '#EC7063',
        background: '#FFF5F5',
        backgroundGradient: 'linear-gradient(135deg, #FFF5F5 0%, #FADBD8 100%)',
        cardGradient: 'linear-gradient(135deg, #F5B7B1 0%, #FADBD8 100%)',
        shadow: 'rgba(231, 76, 60, 0.3)'
      },
      teal: {
        name: '青蓝渐变',
        primary: '#1ABC9C',
        primaryLight: '#48C9B0',
        background: '#F0FBF9',
        backgroundGradient: 'linear-gradient(135deg, #F0FBF9 0%, #D5F4E6 100%)',
        cardGradient: 'linear-gradient(135deg, #A3E4D7 0%, #D5F4E6 100%)',
        shadow: 'rgba(26, 188, 156, 0.3)'
      },
      sunset: {
        name: '日落渐变',
        primary: '#FF6B6B',
        primaryLight: '#FFA07A',
        background: '#FFF5F5',
        backgroundGradient: 'linear-gradient(135deg, #FFF5F5 0%, #FFE8E8 100%)',
        cardGradient: 'linear-gradient(135deg, #FFB6B9 0%, #FFC1C1 100%)',
        shadow: 'rgba(255, 107, 107, 0.3)'
      }
    }
  },
  
  // 获取所有记录
  getRecords(callback) {
    api.getRecords().then(records => {
      const list = api.convertListToFrontend(records)
      if (callback) callback(list)
    }).catch(err => {
      console.error('获取记录失败', err)
      if (callback) callback([])
    })
  },
  
  // 保存记录
  saveRecord(record, callback) {
    if (record._id || record.id) {
      // 更新
      const id = record._id || record.id
      api.updateRecord(id, record).then(res => {
        if (callback) callback(true, res)
      }).catch(err => {
        console.error('更新记录失败', err)
        if (callback) callback(false, err)
      })
    } else {
      // 新增
      api.createRecord(record).then(res => {
        if (callback) callback(true, res)
      }).catch(err => {
        console.error('添加记录失败', err)
        if (callback) callback(false, err)
      })
    }
  },
  
  // 添加记录
  addRecord(record, callback) {
    api.createRecord(record).then(res => {
      if (callback) callback(true, res)
    }).catch(err => {
      console.error('添加记录失败', err)
      if (callback) callback(false, err)
    })
  },
  
  // 更新记录
  updateRecord(id, record, callback) {
    api.updateRecord(id, record).then(res => {
      if (callback) callback(true, res)
    }).catch(err => {
      console.error('更新记录失败', err)
      if (callback) callback(false, err)
    })
  },
  
  // 删除记录
  deleteRecord(id, callback) {
    api.deleteRecord(id).then(res => {
      if (callback) callback(true, res)
    }).catch(err => {
      console.error('删除记录失败', err)
      if (callback) callback(false, err)
    })
  },
  
  // 获取用户ID
  getUserOpenId(callback) {
    const userId = api.getUserId()
    if (callback) callback(userId)
  },
    
  // 上传文件到OSS
  uploadFile(filePath, callback) {
    api.uploadImage(filePath).then(url => {
      if (callback) callback(true, url)
    }).catch(err => {
      console.error('上传文件失败:', err)
      if (callback) callback(false, err)
    })
  },
  
  // 批量上传文件到OSS
  uploadFiles(filePaths, callback) {
    if (!filePaths || filePaths.length === 0) {
      if (callback) callback(true, [])
      return
    }
    
    api.uploadImages(filePaths).then(urls => {
      if (callback) callback(true, urls)
    }).catch(err => {
      console.error('批量上传文件失败:', err)
      if (callback) callback(false, err)
    })
  },
  
  // 获取当前主题
  getCurrentTheme() {
    return this.globalData.currentTheme
  },
  
  // 获取主题配置
  getThemeConfig(themeName) {
    return this.globalData.themes[themeName || this.globalData.currentTheme]
  },
  
  // 切换主题
  setTheme(themeName) {
    if (this.globalData.themes[themeName]) {
      this.globalData.currentTheme = themeName
      wx.setStorageSync('appTheme', themeName)
      
      // 设置导航栏颜色
      const theme = this.globalData.themes[themeName]
      wx.setNavigationBarColor({
        frontColor: '#ffffff',
        backgroundColor: theme.primary,
        animation: {
          duration: 400,
          timingFunc: 'easeIn'
        }
      })
      
      // 设置TabBar颜色
      wx.setTabBarStyle({
        selectedColor: theme.primary
      })
      
      return true
    }
    return false
  },
  
  // 获取所有主题
  getAllThemes() {
    return this.globalData.themes
  },
  
  // 启动时应用主题
  applyThemeOnLaunch(themeName) {
    const theme = this.globalData.themes[themeName]
    if (theme) {
      wx.setNavigationBarColor({
        frontColor: '#ffffff',
        backgroundColor: theme.primary
      })
      wx.setTabBarStyle({
        selectedColor: theme.primary
      })
    }
  },
  
  // 获取初始主题配置（用于页面data初始化）
  getInitialThemeColors() {
    const theme = wx.getStorageSync('appTheme') || 'pink'
    return this.globalData.themes[theme] || this.globalData.themes.pink
  }
})
