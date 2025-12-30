// pages/detail/detail.js
const app = getApp()
const api = require('../../utils/api.js')
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    recordId: '',
    record: {},
    currentPhotoIndex: 0,
    themeColors: getInitialTheme(),
    // 分享相关
    showShareModal: false,
    showContentModal: false,
    platforms: [],
    selectedPlatform: '',
    generating: false,
    shareContent: {}
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ recordId: options.id })
      this.loadRecord(options.id)
    }
    this.applyTheme()
    this.loadPlatforms()
  },

  // 加载分享平台列表
  async loadPlatforms() {
    try {
      const platforms = await api.getSharePlatforms()
      this.setData({ platforms: platforms || [] })
    } catch (err) {
      console.error('加载平台列表失败:', err)
      // 使用默认平台列表
      this.setData({
        platforms: [
          { code: 'xiaohongshu', name: '小红书', icon: '/images/redbook.png' },
          { code: 'moments', name: '朋友圈', icon: '/images/circle.png' },
          { code: 'weibo', name: '微博', icon: '/images/blog.png' }
        ]
      })
    }
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
        this.setData({ record: frontendRecord })
      } else {
        wx.showToast({
          title: '记录不存在',
          icon: 'none'
        })
        setTimeout(() => {
          wx.navigateBack()
        }, 1500)
      }
    }).catch(err => {
      wx.hideLoading()
      console.error('加载记录失败', err)
      wx.showToast({
        title: '加载记录失败',
        icon: 'error'
      })
      setTimeout(() => {
        wx.navigateBack()
      }, 1500)
    })
  },

  // 轮播图变化
  onSwiperChange(e) {
    this.setData({
      currentPhotoIndex: e.detail.current
    })
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url
    const photos = this.data.record.photos || []
    
    wx.previewImage({
      current: url,
      urls: photos
    })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数
  },

  // ========== 智能分享功能 ==========

  // 显示分享平台选择弹窗
  showShareModal() {
    this.setData({ showShareModal: true })
  },

  // 隐藏分享平台弹窗
  hideShareModal() {
    this.setData({ showShareModal: false, generating: false })
  },

  // 选择平台并生成文案
  async selectPlatform(e) {
    const platform = e.currentTarget.dataset.platform
    if (this.data.generating) return

    this.setData({
      selectedPlatform: platform,
      generating: true
    })

    try {
      const recordId = parseInt(this.data.recordId)
      const content = await api.generateShareContent(recordId, platform)
      
      this.setData({
        shareContent: content,
        showShareModal: false,
        showContentModal: true,
        generating: false
      })
    } catch (err) {
      console.error('生成文案失败:', err)
      this.setData({ generating: false })
      
      // 如果后端失败，使用本地生成
      this.generateLocalContent(platform)
    }
  },

  // 本地生成文案（备用）
  generateLocalContent(platform) {
    const { record } = this.data
    const shareText = this.generateShareText(record)
    
    const platformNames = {
      'xiaohongshu': '小红书',
      'moments': '朋友圈',
      'weibo': '微博'
    }
    
    this.setData({
      shareContent: {
        platform: platform,
        platformName: platformNames[platform] || '朋友圈',
        content: shareText,
        body: shareText
      },
      showShareModal: false,
      showContentModal: true
    })
  },

  // 隐藏文案预览弹窗
  hideContentModal() {
    this.setData({ showContentModal: false, shareContent: {} })
  },

  // 重新生成文案
  async regenerateContent() {
    const { selectedPlatform, recordId } = this.data
    if (!selectedPlatform) return

    wx.showLoading({ title: '重新生成中...', mask: true })
    
    try {
      const content = await api.generateShareContent(parseInt(recordId), selectedPlatform)
      this.setData({ shareContent: content })
      wx.hideLoading()
      wx.showToast({ title: '生成成功', icon: 'success' })
    } catch (err) {
      wx.hideLoading()
      wx.showToast({ title: '生成失败', icon: 'none' })
    }
  },

  // 复制文案并分享
  copyAndShare() {
    const { shareContent, record } = this.data
    const text = shareContent.content || shareContent.body || ''
    const imageUrl = record.photos && record.photos.length > 0 ? record.photos[0] : ''

    // 复制文案到剪贴板
    wx.setClipboardData({
      data: text,
      success: () => {
        if (imageUrl) {
          // 保存图片到相册
          wx.saveImageToPhotosAlbum({
            filePath: imageUrl,
            success: () => {
              this.setData({ showContentModal: false })
              this.showShareGuide(shareContent.platformName)
            },
            fail: (err) => {
              if (err.errMsg.includes('auth deny')) {
                this.requestPhotoAlbumPermission()
              } else {
                this.setData({ showContentModal: false })
                this.showShareGuide(shareContent.platformName)
              }
            }
          })
        } else {
          this.setData({ showContentModal: false })
          this.showShareGuide(shareContent.platformName)
        }
      }
    })
  },

  // 显示分享引导
  showShareGuide(platformName) {
    const guides = {
      '小红书': '打开小红书APP\n点击“+”发布笔记\n粘贴文案并选择图片',
      '朋友圈': '打开微信朋友圈\n粘贴文案并选择图片\n发布分享',
      '微博': '打开微博APP\n点击“+”发布微博\n粘贴文案并选择图片'
    }
    
    wx.showModal({
      title: `📱 分享到${platformName}`,
      content: `✅ 文案已复制\n✅ 图片已保存\n\n${guides[platformName] || '打开对应APP粘贴文案即可'}`,
      showCancel: false,
      confirmText: '我知道了',
      confirmColor: '#FF6B9D'
    })
  },

  // 请求相册权限
  requestPhotoAlbumPermission() {
    wx.showModal({
      title: '需要授权',
      content: '需要您授权保存图片到相册，才能分享哦~',
      confirmText: '去授权',
      confirmColor: '#FF6B9D',
      success: (res) => {
        if (res.confirm) {
          wx.openSetting()
        }
      }
    })
  },

  // ========== 以下保留原有的本地文案生成方法（作为备用） ==========

  // 分享到朋友圈（保留原有方法作为备用）
  shareToFriends() {
    const { record } = this.data
    const shareText = this.generateShareText(record)
    const imageUrl = record.photos && record.photos.length > 0 ? record.photos[0] : ''

    if (!imageUrl) {
      wx.showToast({
        title: '请先添加照片',
        icon: 'none'
      })
      return
    }

    // 直接保存图片并分享
    this.saveImageAndShare(imageUrl, shareText)
  },
  
  // 保存图片并分享
  saveImageAndShare(imagePath, shareText) {
    // 复制文案到剪贴板
    wx.setClipboardData({
      data: shareText,
      success: () => {
        // 保存图片到相册
        wx.saveImageToPhotosAlbum({
          filePath: imagePath,
          success: () => {
            wx.showModal({
              title: '📱 准备分享',
              content: `✅ 文案已复制
✅ 图片已保存到相册

${shareText}

请退出小程序，打开微信朋友圈，粘贴文案并选择图片发布吧～`,
              showCancel: false,
              confirmText: '我知道了',
              confirmColor: '#FF6B9D'
            })
          },
          fail: (err) => {
            if (err.errMsg.includes('auth deny')) {
              wx.showModal({
                title: '需要授权',
                content: '需要您授权保存图片到相册，才能分享到朋友圈哦~',
                confirmText: '去授权',
                confirmColor: '#FF6B9D',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    wx.openSetting()
                  }
                }
              })
            } else {
              wx.showToast({
                title: '图片保存失败',
                icon: 'none'
              })
            }
          }
        })
      }
    })
  },

  // 生成分享文案
  generateShareText(record) {
    const { mountainName, distance, duration, date, weather } = record
    
    // 解析数据
    const distanceNum = distance ? parseFloat(distance) : 0
    const durationMinutes = this.parseDuration(duration)
    const weekday = this.getWeekday(date)
    const month = date ? parseInt(date.split('-')[1]) : 0
    
    // 智能生成文案
    const text = this.generateSmartText({
      mountainName,
      distance: distanceNum,
      duration: durationMinutes,
      date,
      weekday,
      month,
      weather
    })
    
    return text
  },

  // 解析时长为分钟
  parseDuration(duration) {
    if (!duration) return 0
    let minutes = 0
    const hourMatch = duration.match(/(\d+)\s*(小时|h)/)
    const minuteMatch = duration.match(/(\d+)\s*(分钟|分|m)/)
    if (hourMatch) minutes += parseInt(hourMatch[1]) * 60
    if (minuteMatch) minutes += parseInt(minuteMatch[1])
    return minutes
  },

  // 获取星期
  getWeekday(dateStr) {
    if (!dateStr) return ''
    const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    const date = new Date(dateStr)
    return weekdays[date.getDay()]
  },

  // 智能生成文案
  generateSmartText(data) {
    const { mountainName, distance, duration, date, weekday, month, weather } = data
    
    // 选择开场白
    const openings = this.getOpenings(distance, duration, weekday, month, weather)
    const opening = openings[Math.floor(Math.random() * openings.length)]
    
    // 选择主体内容
    const bodies = this.getBodies(mountainName, distance, duration)
    const body = bodies[Math.floor(Math.random() * bodies.length)]
    
    // 选择结尾
    const endings = this.getEndings(distance, duration)
    const ending = endings[Math.floor(Math.random() * endings.length)]
    
    // 选择hashtag
    const hashtags = this.getHashtags(distance, month)
    
    return `${opening}

${body}

${ending}
${hashtags}`
  },

  // 获取开场白
  getOpenings(distance, duration, weekday, month, weather) {
    const openings = []
    
    // 根据星期
    if (weekday === '周六' || weekday === '周日') {
      openings.push(
        `🌞 ${weekday}好啊！最好的时光就是和宝贝一起疯~`,
        `🎉 ${weekday}特供：亲子疯爬模式已开启！`,
        `✨ 周末不宅家，带娃野去！`
      )
    } else {
      openings.push(
        `🌿 谁说工作日不能疯？下班就是冲！`,
        `💪 ${weekday}也要元气满满！`
      )
    }
    
    // 根据天气
    if (weather) {
      if (weather.includes('晴')) {
        openings.push(`☀️ 阳光正好，微风不燥，完美！`)
      } else if (weather.includes('云')) {
        openings.push(`⛅ 云很轻，我们的脚步也很轻快~`)
      }
    }
    
    // 根据季节
    if (month >= 3 && month <= 5) {
      openings.push(`🌸 春天不出来浪，就是辜负好时光！`)
    } else if (month >= 6 && month <= 8) {
      openings.push(`🌴 夏日爆汗也要快乐加倍！`)
    } else if (month >= 9 && month <= 11) {
      openings.push(`🍂 秋高气爽，最适合亲子爬山啦！`)
    } else {
      openings.push(`❄️ 冬天也阻挡不了我们的热情！`)
    }
    
    // 根据距离
    if (distance >= 10) {
      openings.push(
        `🚀 今天是不是吃了菠菜？这个里程有点猛！`,
        `🏆 挑战超长距离，我们做到了！`
      )
    }
    
    openings.push(
      `🏞️ 记录一下今天的小美好~`,
      `👶 和小神兽的每一次出征都值得记录！`,
      `🌟 今日份快乐加倍中...`
    )
    
    return openings
  },

  // 获取主体内容
  getBodies(mountainName, distance, duration) {
    const bodies = []
    const distStr = distance ? `${distance}km` : ''
    const durStr = duration ? `${Math.floor(duration/60)}h${duration%60 || ''}` : ''
    
    if (distance >= 10) {
      bodies.push(
        `🎯 目标：${mountainName}\n💯 战绩：${distStr} | 耗时${durStr}\n💪 状态：小朋友全程元气满满，老母亲/老父亲已经累趴😂`,
        `🧗‍♀️ 挑战地点：${mountainName}\n📍 这个距离真的没开玩笑！${distStr}！\n🏅 宝贝表现：全程零抱抱，自己走完，真棒！`,
        `⛰️ ${mountainName} × ${distStr}\n⏱️ 时长：${durStr}\n🌟 感受：和宝贝一起创造新纪录，超有成就感！`
      )
    } else if (distance >= 5) {
      bodies.push(
        `🌼 今日打卡：${mountainName}\n👣 里程：${distStr} | 时长：${durStr}\n😊 宝贝全程叨叨叨，分享了好多小发现~`,
        `🌈 地点：${mountainName}\n🚶‍♀️ 运动量：${distStr}\n💕 感受：慢慢走，慢慢看，和宝贝的每一步都充满欢笑`,
        `🧒 ${mountainName}已解锁！\n🎯 ${distStr} ✓ ${durStr} ✓\n✨ 这个距离刚刚好，宝贝说下次还要来！`
      )
    } else {
      bodies.push(
        `🌻 轻松愉快的${mountainName}之旅\n👶 和宝贝慢慢散步，看风景，聊天~\n💞 这样的时光，才是最珍贵的`,
        `🌿 今天的主题：亲子慢游${mountainName}\n👟 ${distStr}的轻松小路线\n🥰 宝贝说爬山真好玩！`
      )
    }
    
    bodies.push(
      `🏔️ 征服目标：${mountainName} ✓\n💯 数据：${distStr} × ${durStr}\n🌟 心情：和宝贝一起的每一步都是快乐！`,
      `🧗 小登山家日常：
📍 ${mountainName}
📏 ${distStr}
⏱️ ${durStr}
👍 今天也是元气满满的一天！`,
      `⛰️ ${mountainName}打卡成功！\n🎯 ${distStr} | ${durStr}\n😍 和宝贝一起的每一次冒险都超值！`
    )
    
    return bodies
  },

  // 获取结尾
  getEndings(distance, duration) {
    const endings = []
    
    if (distance >= 10 || duration >= 180) {
      endings.push(
        `🏆 今天的我们，超级勇！`,
        `💪 留下这个瞬间，纪念我们的小成就！`,
        `🎉 回家可以好好炎耀一番了！`
      )
    } else {
      endings.push(
        `😊 这就是我想要的生活啊！`,
        `✨ 小确幸×亲子时光=大幸福`,
        `💕 和宝贝一起的每一天都是特别的`
      )
    }
    
    endings.push(
      `🌟 下次再来挑战新高度！`,
      `👶 小朋友已经在期待下一次了~`,
      `❤️ 感恩有你，每一步都有意义`,
      `🌈 生活明明可以更美的，比如和你一起爬山`
    )
    
    return endings
  },

  // 获取hashtag
  getHashtags(distance, month) {
    const tags = ['#亲子爬山', '#快乐时光']
    
    if (distance >= 10) {
      tags.push('#挑战自我')
    } else if (distance >= 5) {
      tags.push('#周末遥风')
    } else {
      tags.push('#慢生活')
    }
    
    if (month >= 3 && month <= 5) {
      tags.push('#春日限定')
    } else if (month >= 6 && month <= 8) {
      tags.push('#夏日冲浪')
    } else if (month >= 9 && month <= 11) {
      tags.push('#秋日美好')
    } else {
      tags.push('#冬日暖阳')
    }
    
    tags.push('#成长记录')
    
    return tags.join(' ')
  },

  // 编辑记录
  editRecord() {
    wx.navigateTo({
      url: `/pages/add/add?id=${this.data.recordId}`
    })
  },

  // 删除记录
  deleteRecord() {
    wx.showModal({
      title: '确认删除',
      content: '确定要删除这条记录吗？',
      confirmColor: '#FF6B9D',
      success: (res) => {
        if (res.confirm) {
          app.deleteRecord(this.data.recordId, (success, result) => {
            if (success) {
              wx.showToast({
                title: '删除成功',
                icon: 'success'
              })
              setTimeout(() => {
                wx.navigateBack()
              }, 1500)
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
  
  // 应用主题颜色
  applyTheme() {
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
