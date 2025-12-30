/**
 * API 请求工具类
 * 用于调用后端 RESTful API
 */

// 后端服务地址（开发环境）
const BASE_URL = 'http://localhost:8080/api'

// 获取用户ID（模拟，实际应该从登录获取）
function getUserId() {
  let userId = wx.getStorageSync('userId')
  if (!userId) {
    userId = 'user_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
    wx.setStorageSync('userId', userId)
  }
  return userId
}

// 获取所有者ID（登录后获取）
function getOwnerId() {
  return wx.getStorageSync('ownerId') || null
}

// 设置所有者ID
function setOwnerId(ownerId) {
  wx.setStorageSync('ownerId', ownerId)
}

// 获取当前用户信息
function getCurrentUser() {
  return wx.getStorageSync('currentUser') || null
}

// 设置当前用户信息
function setCurrentUser(user) {
  wx.setStorageSync('currentUser', user)
}

/**
 * 通用请求方法
 */
function request(options) {
  return new Promise((resolve, reject) => {
    const userId = getUserId()
    
    wx.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'X-User-Id': userId,
        ...options.header
      },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          // 检查业务状态码
          if (res.data && res.data.code === 0) {
            resolve(res.data.data)
          } else {
            reject(res.data)
          }
        } else {
          reject(res)
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        reject(err)
      }
    })
  })
}

/**
 * 获取所有徒步记录
 */
function getRecords() {
  return request({
    url: '/v1/hiking-records',
    method: 'GET'
  })
}

/**
 * 获取单条记录详情
 */
function getRecordById(id) {
  return request({
    url: `/v1/hiking-records/${id}`,
    method: 'GET'
  })
}

/**
 * 创建徒步记录
 */
function createRecord(record) {
  return request({
    url: '/v1/hiking-records',
    method: 'POST',
    data: {
      mountainName: record.mountainName,
      hikingDate: record.date,
      distance: record.distance ? parseFloat(record.distance) : null,
      duration: record.duration,
      weatherType: record.weather,
      weatherIcon: record.weatherIcon,
      photos: record.photos || [],
      notes: record.notes
    }
  })
}

/**
 * 更新徒步记录
 */
function updateRecord(id, record) {
  return request({
    url: `/v1/hiking-records/${id}`,
    method: 'PUT',
    data: {
      mountainName: record.mountainName,
      hikingDate: record.date,
      distance: record.distance ? parseFloat(record.distance) : null,
      duration: record.duration,
      weatherType: record.weather,
      weatherIcon: record.weatherIcon,
      photos: record.photos || [],
      notes: record.notes
    }
  })
}

/**
 * 删除徒步记录
 */
function deleteRecord(id) {
  return request({
    url: `/v1/hiking-records/${id}`,
    method: 'DELETE'
  })
}

/**
 * 获取统计数据
 */
function getStatistics() {
  return request({
    url: '/v1/hiking-records/statistics',
    method: 'GET'
  })
}

/**
 * 获取团队成员的所有记录
 */
function getTeamRecords() {
  return request({
    url: '/v1/hiking-records/team',
    method: 'GET'
  })
}

// ========== 用户相关 API ==========

/**
 * 登录/注册
 */
function login(openId, nickname, avatar) {
  return request({
    url: '/v1/users/login',
    method: 'POST',
    data: {
      openId: openId || getUserId(),
      nickname: nickname,
      avatar: avatar
    }
  }).then(res => {
    // 保存用户信息
    if (res && res.userId) {
      setOwnerId(res.userId)
      setCurrentUser(res.user)
    }
    return res
  })
}

/**
 * 获取当前用户信息
 */
function getUserInfo() {
  return request({
    url: '/v1/users/me',
    method: 'GET'
  })
}

// ========== 团队相关 API ==========

/**
 * 创建团队
 */
function createTeam(name, description) {
  return request({
    url: '/v1/teams',
    method: 'POST',
    data: { name, description }
  })
}

/**
 * 获取我的团队列表
 */
function getMyTeams() {
  return request({
    url: '/v1/teams',
    method: 'GET'
  })
}

/**
 * 获取团队详情
 */
function getTeamById(teamId) {
  return request({
    url: `/v1/teams/${teamId}`,
    method: 'GET'
  })
}

/**
 * 通过邀请码加入团队
 */
function joinTeamByCode(inviteCode) {
  return request({
    url: '/v1/teams/join',
    method: 'POST',
    data: { inviteCode }
  })
}

/**
 * 获取团队成员列表
 */
function getTeamMembers(teamId) {
  return request({
    url: `/v1/teams/${teamId}/members`,
    method: 'GET'
  })
}

/**
 * 退出团队
 */
function leaveTeam(teamId) {
  return request({
    url: `/v1/teams/${teamId}/leave`,
    method: 'POST'
  })
}

/**
 * 解散团队
 */
function deleteTeam(teamId) {
  return request({
    url: `/v1/teams/${teamId}`,
    method: 'DELETE'
  })
}

/**
 * 重新生成邀请码
 */
function regenerateInviteCode(teamId) {
  return request({
    url: `/v1/teams/${teamId}/regenerate-invite-code`,
    method: 'POST'
  })
}

/**
 * 获取待审批的申请列表
 */
function getPendingApplications(teamId) {
  return request({
    url: `/v1/teams/${teamId}/applications`,
    method: 'GET'
  })
}

/**
 * 审批加入申请
 */
function approveApplication(teamId, memberId, approve) {
  return request({
    url: `/v1/teams/${teamId}/members/${memberId}/approve`,
    method: 'PUT',
    data: { approve }
  })
}

// ========== 分享相关 API ==========

/**
 * 获取支持的分享平台列表
 */
function getSharePlatforms() {
  return request({
    url: '/v1/share/platforms',
    method: 'GET'
  })
}

/**
 * 生成分享文案
 * @param {number} recordId - 徒步记录ID
 * @param {string} platform - 平台代码: xiaohongshu, moments, weibo
 */
function generateShareContent(recordId, platform) {
  return request({
    url: '/v1/share/generate',
    method: 'POST',
    data: { recordId, platform }
  })
}

/**
 * 转换后端数据格式为前端格式
 */
function convertToFrontend(record) {
  if (!record) return null
  return {
    _id: String(record.id),
    id: String(record.id),
    mountainName: record.mountainName,
    date: record.hikingDate,
    distance: record.distance ? String(record.distance) : '',
    duration: record.duration || '',
    weather: record.weatherType || '',
    weatherIcon: record.weatherIcon || '',
    photos: record.photos || [],
    notes: record.notes || '',
    ownerId: record.ownerId,
    ownerName: record.ownerName || '',
    teamId: record.teamId,
    createTime: record.createTime ? new Date(record.createTime).getTime() : Date.now(),
    updateTime: record.updateTime ? new Date(record.updateTime).getTime() : Date.now()
  }
}

/**
 * 批量转换
 */
function convertListToFrontend(records) {
  return (records || []).map(convertToFrontend)
}

module.exports = {
  request,
  getRecords,
  getRecordById,
  createRecord,
  updateRecord,
  deleteRecord,
  getStatistics,
  getTeamRecords,
  convertToFrontend,
  convertListToFrontend,
  getUserId,
  getOwnerId,
  setOwnerId,
  getCurrentUser,
  setCurrentUser,
  login,
  getUserInfo,
  createTeam,
  getMyTeams,
  getTeamById,
  joinTeamByCode,
  getTeamMembers,
  leaveTeam,
  deleteTeam,
  regenerateInviteCode,
  getPendingApplications,
  approveApplication,
  getSharePlatforms,
  generateShareContent,
  BASE_URL
}
