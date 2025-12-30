const api = require('../../utils/api.js')
const app = getApp()
const { getInitialTheme } = require('../../utils/theme.js')

Page({
  data: {
    teams: [],
    loading: true,
    showCreateModal: false,
    showJoinModal: false,
    newTeamName: '',
    newTeamDesc: '',
    inviteCode: '',
    currentUser: null,
    themeColors: getInitialTheme()
  },

  onLoad() {
    this.initUser()
    this.applyThemeColors()
  },

  onShow() {
    this.applyThemeColors()
    if (api.getOwnerId()) {
      this.loadTeams()
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
      
      this.setData({
        themeColors: themeConfig
      })
    }
  },

  // 初始化用户
  async initUser() {
    try {
      const ownerId = api.getOwnerId()
      if (!ownerId) {
        // 首次使用，自动登录
        await api.login(api.getUserId(), '徒步爱好者', '')
      }
      this.setData({ currentUser: api.getCurrentUser() })
      this.loadTeams()
    } catch (err) {
      console.error('初始化用户失败:', err)
      this.setData({ loading: false })
    }
  },

  // 加载团队列表
  async loadTeams() {
    this.setData({ loading: true })
    try {
      const teams = await api.getMyTeams()
      this.setData({ teams: teams || [], loading: false })
    } catch (err) {
      console.error('加载团队失败:', err)
      this.setData({ teams: [], loading: false })
    }
  },

  // 显示创建团队弹窗
  showCreateTeam() {
    this.setData({ showCreateModal: true, newTeamName: '', newTeamDesc: '' })
  },

  // 阻止事件冒泡
  stopPropagation() {
    // 空函数，用于阻止事件冒泡
  },

  // 隐藏创建团队弹窗
  hideCreateModal() {
    this.setData({ showCreateModal: false })
  },

  // 输入团队名称
  onTeamNameInput(e) {
    this.setData({ newTeamName: e.detail.value })
  },

  // 输入团队描述
  onTeamDescInput(e) {
    this.setData({ newTeamDesc: e.detail.value })
  },

  // 创建团队
  async createTeam() {
    if (!this.data.newTeamName.trim()) {
      wx.showToast({ title: '请输入团队名称', icon: 'none' })
      return
    }
    try {
      await api.createTeam(this.data.newTeamName, this.data.newTeamDesc)
      wx.showToast({ title: '创建成功', icon: 'success' })
      this.setData({ showCreateModal: false })
      this.loadTeams()
    } catch (err) {
      wx.showToast({ title: err.message || '创建失败', icon: 'none' })
    }
  },

  // 显示加入团队弹窗
  showJoinTeam() {
    this.setData({ showJoinModal: true, inviteCode: '' })
  },

  // 隐藏加入团队弹窗
  hideJoinModal() {
    this.setData({ showJoinModal: false })
  },

  // 输入邀请码
  onInviteCodeInput(e) {
    this.setData({ inviteCode: e.detail.value.toUpperCase() })
  },

  // 通过邀请码加入团队
  async joinTeam() {
    if (!this.data.inviteCode.trim()) {
      wx.showToast({ title: '请输入邀请码', icon: 'none' })
      return
    }
    try {
      await api.joinTeamByCode(this.data.inviteCode)
      wx.showToast({ title: '加入成功', icon: 'success' })
      this.setData({ showJoinModal: false })
      this.loadTeams()
    } catch (err) {
      wx.showToast({ title: err.message || '加入失败', icon: 'none' })
    }
  },

  // 进入团队详情
  goToTeamDetail(e) {
    const teamId = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/team-detail/team-detail?id=${teamId}`
    })
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadTeams().then(() => {
      wx.stopPullDownRefresh()
    })
  }
})
