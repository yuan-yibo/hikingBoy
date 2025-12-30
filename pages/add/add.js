// pages/add/add.js
const app = getApp()
const api = require('../../utils/api.js')
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    isEdit: false,
    recordId: '',
    mountainName: '',
    date: '',
    distance: '',
    duration: '',
    weather: '',
    weatherIcon: '',
    weatherOptions: [
      { value: '晴天', icon: '☀️', label: '晴天' },
      { value: '多云', icon: '⛅', label: '多云' },
      { value: '阴天', icon: '☁️', label: '阴天' },
      { value: '小雨', icon: '🌦️', label: '小雨' },
      { value: '雨天', icon: '🌧️', label: '雨天' }
    ],
    photos: [],
    notes: '',
    themeColors: getInitialTheme()
  },

  onLoad(options) {
    // 设置默认日期为今天
    const today = this.formatDate(new Date())
    this.setData({ date: today })

    // 编辑模式
    if (options.id) {
      this.setData({ isEdit: true, recordId: options.id })
      this.loadRecord(options.id)
    }
    
    this.applyThemeColors()
  },

  // 加载记录
  loadRecord(id) {
    // 显示加载状态
    wx.showLoading({
      title: '加载中...',
      mask: true
    })
    
    api.getRecordById(id).then(record => {
      wx.hideLoading()
      if (record) {
        const frontendRecord = api.convertToFrontend(record)
        // 处理时长显示：去掉单位以便编辑
        let displayDuration = frontendRecord.duration || ''
        if (displayDuration) {
          displayDuration = displayDuration.replace(/h|小时|hour/gi, '').trim()
        }
        
        this.setData({
          mountainName: frontendRecord.mountainName,
          date: frontendRecord.date,
          distance: frontendRecord.distance || '',
          duration: displayDuration,
          weather: frontendRecord.weather || '',
          weatherIcon: frontendRecord.weatherIcon || '',
          photos: frontendRecord.photos || [],
          notes: frontendRecord.notes || ''
        })
      }
    }).catch(err => {
      wx.hideLoading()
      console.error('加载记录失败', err)
      wx.showToast({
        title: '加载记录失败',
        icon: 'error'
      })
    })
  },

  // 山名输入
  onMountainNameInput(e) {
    this.setData({ mountainName: e.detail.value })
  },

  // 日期选择
  onDateChange(e) {
    this.setData({ date: e.detail.value })
  },

  // 公里数输入
  onDistanceInput(e) {
    this.setData({ distance: e.detail.value })
  },

  // 时长输入
  onDurationInput(e) {
    let value = e.detail.value
    // 如果输入的是纯数字，自动添加单位
    if (value && /^\d+(\.\d+)?$/.test(value)) {
      // 纯数字，不添加单位，保存时再加
      this.setData({ duration: value })
    } else {
      this.setData({ duration: value })
    }
  },

  // 选择天气
  selectWeather(e) {
    const { value, icon } = e.currentTarget.dataset
    this.setData({ 
      weather: value,
      weatherIcon: icon
    })
  },

  // 选择照片
  choosePhoto() {
    wx.chooseImage({
      count: 9 - this.data.photos.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const photos = this.data.photos.concat(res.tempFilePaths)
        this.setData({ photos })
      }
    })
  },

  // 删除照片
  deletePhoto(e) {
    const index = e.currentTarget.dataset.index
    const photos = this.data.photos.filter((_, i) => i !== index)
    this.setData({ photos })
  },

  // 笔记输入
  onNotesInput(e) {
    this.setData({ notes: e.detail.value })
  },

  // 保存记录
  saveRecord() {
    const { mountainName, date, distance, duration, weather, weatherIcon, photos, notes, isEdit, recordId } = this.data

    // 验证
    if (!mountainName) {
      wx.showToast({
        title: '请输入山名',
        icon: 'none'
      })
      return
    }

    if (!date) {
      wx.showToast({
        title: '请选择日期',
        icon: 'none'
      })
      return
    }

    // 显示上传进度
    wx.showLoading({
      title: '保存中...'
    })

    // 如果有照片需要上传到云存储
    if (photos.length > 0) {
      // 过滤出本地临时路径的照片（需要上传）
      const localPhotos = photos.filter(photo => photo.startsWith('http') === false && photo.startsWith('cloud') === false)
      
      if (localPhotos.length > 0) {
        // 上传照片到云存储
        app.uploadFiles(localPhotos, (success, cloudFileIds) => {
          if (success) {
            // 替换本地路径为云文件ID
            const updatedPhotos = photos.map(photo => {
              if (photo.startsWith('http') || photo.startsWith('cloud')) {
                return photo // 保留已上传的云文件ID或网络图片
              } else {
                // 找到对应的云文件ID
                const index = localPhotos.indexOf(photo)
                return index !== -1 ? cloudFileIds[index] : photo
              }
            })
            
            this.saveRecordToDB(mountainName, date, distance, duration, weather, weatherIcon, updatedPhotos, notes, isEdit, recordId)
          } else {
            wx.hideLoading()
            wx.showToast({
              title: '照片上传失败',
              icon: 'error'
            })
          }
        })
      } else {
        // 没有需要上传的本地照片，直接保存
        this.saveRecordToDB(mountainName, date, distance, duration, weather, weatherIcon, photos, notes, isEdit, recordId)
      }
    } else {
      // 没有照片，直接保存
      this.saveRecordToDB(mountainName, date, distance, duration, weather, weatherIcon, photos, notes, isEdit, recordId)
    }
  },
  
  // 保存记录到数据库
  saveRecordToDB(mountainName, date, distance, duration, weather, weatherIcon, photos, notes, isEdit, recordId) {
    // 处理时长单位：如果是纯数字，自动添加"h"
    let formattedDuration = duration
    if (duration && /^\d+(\.\d+)?$/.test(duration)) {
      formattedDuration = duration + 'h'
    }
    
    const record = {
      mountainName,
      date,
      distance,
      duration: formattedDuration,
      weather,
      weatherIcon,
      photos,
      notes,
      updateTime: new Date().getTime()
    }

    if (isEdit) {
      // 编辑现有记录
      const app = getApp()
      app.updateRecord(recordId, record, (success, result) => {
        wx.hideLoading()
        if (success) {
          wx.showToast({
            title: '修改成功',
            icon: 'success'
          })
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.showToast({
            title: '修改失败',
            icon: 'error'
          })
        }
      })
    } else {
      // 添加新记录
      record.createTime = new Date().getTime()
      const app = getApp()
      app.addRecord(record, (success, result) => {
        wx.hideLoading()
        if (success) {
          wx.showToast({
            title: '保存成功',
            icon: 'success'
          })
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        } else {
          wx.showToast({
            title: '保存失败',
            icon: 'error'
          })
        }
      })
    }
  },

  // 获取原创建时间
  getCreateTime() {
    const records = app.getRecords()
    const record = records.find(r => r.id === this.data.recordId)
    return record ? record.createTime : new Date().getTime()
  },

  // 格式化日期
  formatDate(date) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
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
      
      this.setData({
        themeColors: themeConfig
      })
    }
  }
})
