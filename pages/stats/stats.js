// pages/stats/stats.js
const app = getApp()
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    totalCount: 0,
    totalPhotos: 0,
    recentRecords: [],
    weatherStats: [],
    monthlyStats: [],
    mountainList: [],
    showMountainModal: false,
    motivationText: '每一步都是成长的足迹！',
    themeColors: getInitialTheme()
  },

  onShow() {
    this.loadStats()
    this.applyThemeColors()
  },

  // 加载统计数据
  loadStats() {
    app.getRecords((records) => {
      // 总次数
      const totalCount = records.length
      
      // 总照片数
      const totalPhotos = records.reduce((sum, record) => {
        return sum + (record.photos ? record.photos.length : 0)
      }, 0)
      
      // 最近5次记录
      const recentRecords = records.slice(0, 5)
      
      // 天气统计
      const weatherStats = this.calculateWeatherStats(records)
      
      // 按月分组统计
      const monthlyStats = this.calculateMonthlyStats(records)
      
      // 山峰统计
      const mountainList = this.calculateMountainStats(records)
      
      // 励志话语
      const motivationText = this.getMotivationText(totalCount)
      
      this.setData({
        totalCount,
        totalPhotos,
        recentRecords,
        weatherStats,
        monthlyStats,
        mountainList,
        motivationText
      })
    })
  },

  // 计算天气统计
  calculateWeatherStats(records) {
    const weatherMap = {}
    const weatherIcons = {
      '晴天': '☀️',
      '多云': '⛅',
      '阴天': '☁️',
      '小雨': '🌦️',
      '雨天': '🌧️'
    }
    
    records.forEach(record => {
      if (record.weather) {
        if (!weatherMap[record.weather]) {
          weatherMap[record.weather] = {
            weather: record.weather,
            icon: weatherIcons[record.weather] || '🌤️',
            count: 0
          }
        }
        weatherMap[record.weather].count++
      }
    })
    
    const weatherStats = Object.values(weatherMap)
    const maxCount = Math.max(...weatherStats.map(s => s.count), 1)
    
    return weatherStats.map(stat => ({
      ...stat,
      percentage: (stat.count / maxCount) * 100
    })).sort((a, b) => b.count - a.count)
  },

  // 按月统计
  calculateMonthlyStats(records) {
    const monthlyMap = {}
    
    records.forEach(record => {
      if (!record.date) return
      
      // 提取年月 (2024-12)
      const yearMonth = record.date.substring(0, 7)
      
      if (!monthlyMap[yearMonth]) {
        monthlyMap[yearMonth] = {
          month: yearMonth,
          count: 0,
          totalDistance: 0,
          totalMinutes: 0,
          records: []
        }
      }
      
      monthlyMap[yearMonth].count++
      monthlyMap[yearMonth].records.push(record)
      
      // 统计总公里数
      if (record.distance) {
        monthlyMap[yearMonth].totalDistance += parseFloat(record.distance) || 0
      }
      
      // 统计总时长(转换为分钟)
      if (record.duration) {
        const minutes = this.parseDurationToMinutes(record.duration)
        monthlyMap[yearMonth].totalMinutes += minutes
      }
    })
    
    // 转换为数组并按月份降序排列
    return Object.values(monthlyMap)
      .map(stat => ({
        ...stat,
        displayMonth: this.formatMonth(stat.month),
        totalDistance: stat.totalDistance.toFixed(1),
        totalDuration: this.formatMinutesToDuration(stat.totalMinutes)
      }))
      .sort((a, b) => b.month.localeCompare(a.month))
  },

  // 解析时长为分钟
  parseDurationToMinutes(duration) {
    let totalMinutes = 0
    
    // 匹配小时 (2小时, 2h)
    const hourMatch = duration.match(/(\d+)\s*(小时|h)/)
    if (hourMatch) {
      totalMinutes += parseInt(hourMatch[1]) * 60
    }
    
    // 匹配分钟 (30分钟, 30m, 30分)
    const minuteMatch = duration.match(/(\d+)\s*(分钟|分|m)/)
    if (minuteMatch) {
      totalMinutes += parseInt(minuteMatch[1])
    }
    
    return totalMinutes
  },

  // 分钟转换为时长显示
  formatMinutesToDuration(minutes) {
    if (minutes === 0) return '0分钟'
    
    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60
    
    if (hours > 0 && mins > 0) {
      return `${hours}小时${mins}分钟`
    } else if (hours > 0) {
      return `${hours}小时`
    } else {
      return `${mins}分钟`
    }
  },

  // 格式化月份显示
  formatMonth(yearMonth) {
    const [year, month] = yearMonth.split('-')
    return `${year}年${parseInt(month)}月`
  },

  // 获取励志话语
  getMotivationText(count) {
    const texts = [
      '每一步都是成长的足迹！',
      '继续加油，征服更多山峰！',
      '和宝贝的回忆越来越多啦！',
      '你们是最棒的爬山搭档！',
      '坚持就是胜利，继续前进！',
      '每次登顶都是新的突破！'
    ]
    
    if (count === 0) {
      return '开始你们的第一次爬山冒险吧！'
    } else if (count >= 10) {
      return '哇！已经爬了这么多次，真是了不起！'
    } else if (count >= 5) {
      return '太棒了！你们已经是爬山小能手啦！'
    }
    
    return texts[Math.floor(Math.random() * texts.length)]
  },

  // 跳转到详情
  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/detail/detail?id=${id}`
    })
  },

  // 显示所有山峰
  showAllMountains() {
    this.setData({
      showMountainModal: true
    })
  },

  // 隐藏山峰弹窗
  hideMountainModal() {
    this.setData({
      showMountainModal: false
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数，用于阻止事件冒泡
  },

  // 跳转到山峰详情
  goToMountainDetail(e) {
    const mountainName = e.currentTarget.dataset.name
    // 查找该山的第一条记录
    app.getRecords((records) => {
      const record = records.find(r => r.mountainName === mountainName)
      if (record) {
        this.hideMountainModal()
        wx.navigateTo({
          url: `/pages/detail/detail?id=${record._id || record.id}`
        })
      }
    })
  },

  // 统计山峰数据
  calculateMountainStats(records) {
    const mountainMap = {}
    const gradients = [
      'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
      'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
      'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
      'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
      'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
      'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
      'linear-gradient(135deg, #ff6e7f 0%, #bfe9ff 100%)'
    ]
    
    records.forEach(record => {
      if (!record.mountainName) return
      
      if (!mountainMap[record.mountainName]) {
        mountainMap[record.mountainName] = {
          name: record.mountainName,
          count: 0
        }
      }
      mountainMap[record.mountainName].count++
    })
    
    // 转换为数组并按次数排序
    const mountainList = Object.values(mountainMap)
      .sort((a, b) => b.count - a.count)
      .map((item, index) => ({
        ...item,
        gradient: gradients[index % gradients.length]
      }))
    
    return mountainList
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
  }
})
