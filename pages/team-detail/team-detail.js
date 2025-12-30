const api = require('../../utils/api.js')

Page({
  data: {
    teamId: null,
    team: null,
    members: [],
    records: [],
    activeTab: 'records', // records | members
    loading: true,
    isOwner: false,
    currentUserId: null
  },

  onLoad(options) {
    this.setData({ 
      teamId: options.id,
      currentUserId: api.getOwnerId()
    })
  },

  onShow() {
    this.loadTeamData()
  },

  // 加载团队数据
  async loadTeamData() {
    this.setData({ loading: true })
    try {
      const [team, members] = await Promise.all([
        api.getTeamById(this.data.teamId),
        api.getTeamMembers(this.data.teamId)
      ])
      
      const isOwner = team.ownerId === this.data.currentUserId
      
      this.setData({ 
        team, 
        members: members || [], 
        isOwner,
        loading: false 
      })
      
      // 加载团队记录
      this.loadTeamRecords()
    } catch (err) {
      console.error('加载团队数据失败:', err)
      this.setData({ loading: false })
      wx.showToast({ title: '加载失败', icon: 'none' })
    }
  },

  // 加载团队记录
  async loadTeamRecords() {
    try {
      const records = await api.getTeamRecords()
      const formattedRecords = api.convertListToFrontend(records)
      this.setData({ records: formattedRecords })
    } catch (err) {
      console.error('加载团队记录失败:', err)
    }
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  // 复制邀请码
  copyInviteCode() {
    if (!this.data.team) return
    wx.setClipboardData({
      data: this.data.team.inviteCode,
      success: () => {
        wx.showToast({ title: '邀请码已复制', icon: 'success' })
      }
    })
  },

  // 重新生成邀请码
  async regenerateCode() {
    if (!this.data.isOwner) return
    
    wx.showModal({
      title: '提示',
      content: '重新生成后，原邀请码将失效，确定继续？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const result = await api.regenerateInviteCode(this.data.teamId)
            this.setData({ 
              'team.inviteCode': result.inviteCode 
            })
            wx.showToast({ title: '已重新生成', icon: 'success' })
          } catch (err) {
            wx.showToast({ title: '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  // 退出团队
  leaveTeam() {
    wx.showModal({
      title: '提示',
      content: '确定要退出该团队吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.leaveTeam(this.data.teamId)
            wx.showToast({ title: '已退出', icon: 'success' })
            wx.navigateBack()
          } catch (err) {
            wx.showToast({ title: err.message || '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  // 解散团队
  deleteTeam() {
    wx.showModal({
      title: '警告',
      content: '解散团队后，所有成员将被移除，此操作不可恢复！',
      confirmColor: '#ff4444',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.deleteTeam(this.data.teamId)
            wx.showToast({ title: '团队已解散', icon: 'success' })
            wx.navigateBack()
          } catch (err) {
            wx.showToast({ title: err.message || '操作失败', icon: 'none' })
          }
        }
      }
    })
  },

  // 查看记录详情
  goToRecordDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/detail/detail?id=${id}`
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadTeamData().then(() => {
      wx.stopPullDownRefresh()
    })
  }
})
