// 数据迁移工具
const app = getApp()

// 检查并迁移本地数据到云端
function migrateLocalData() {
  return new Promise((resolve, reject) => {
    // 检查是否有本地数据
    const localRecords = wx.getStorageSync('hikingRecords') || []
    
    if (localRecords.length === 0) {
      console.log('没有本地数据需要迁移')
      resolve(false)
      return
    }
    
    console.log(`发现${localRecords.length}条本地记录，开始迁移...`)
    
    // 获取云端数据，避免重复迁移
    app.getRecords((cloudRecords) => {
      const cloudRecordIds = new Set(cloudRecords.map(r => r.id || r._id))
      const recordsToMigrate = localRecords.filter(record => !cloudRecordIds.has(record.id))
      
      if (recordsToMigrate.length === 0) {
        console.log('云端数据已是最新，无需迁移')
        resolve(false)
        return
      }
      
      console.log(`需要迁移${recordsToMigrate.length}条记录`)
      
      // 批量迁移数据
      let migratedCount = 0
      const migrateNext = () => {
        if (migratedCount >= recordsToMigrate.length) {
          console.log(`数据迁移完成，共迁移${recordsToMigrate.length}条记录`)
          resolve(true)
          return
        }
        
        const record = recordsToMigrate[migratedCount]
        // 确保记录格式正确
        const recordToSave = {
          ...record,
          _openid: wx.getStorageSync('userOpenId') || '',
          createTime: record.createTime || new Date().getTime(),
          updateTime: record.updateTime || new Date().getTime()
        }
        
        app.addRecord(recordToSave, (success, result) => {
          if (success) {
            migratedCount++
            console.log(`已迁移第${migratedCount}条记录`)
            migrateNext()
          } else {
            console.error('迁移记录失败', record, result)
            reject(new Error(`迁移记录失败: ${result.errMsg}`))
          }
        })
      }
      
      migrateNext()
    })
  })
}

module.exports = {
  migrateLocalData
}