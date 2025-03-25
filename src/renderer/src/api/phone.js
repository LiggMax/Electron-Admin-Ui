import request from '../utils/request'

/**
 * 获取卡号信息列表
 */
export const getPhoneList = (paramsData) =>{
  const params = new URLSearchParams()
  for (let key in paramsData){
    params.append(key, paramsData[key]);
  }
  return request.get('/phone/phoneList', {params: params})
}

/**
 * 上传手机号码批量导入
 * @param {Object} data - 上传数据，包括国家、项目和文件数据
 * @returns {Promise} - 返回上传结果的Promise
 */
export const uploadPhoneNumbers = (data) => {
  console.log('调用上传API，数据:', data)
  
  // 数据验证
  if (!data.country || !data.project || !data.files || !data.files.length) {
    return Promise.reject(new Error('数据格式不正确，请确保包含国家、项目和文件数据'))
  }
  
  // 检查是否有手机号数据
  const hasPhoneNumbers = data.files.some(file => 
    file.phoneNumbers && file.phoneNumbers.length > 0
  )
  
  if (!hasPhoneNumbers) {
    return Promise.reject(new Error('没有有效的手机号码数据可上传'))
  }
  
  // 发送请求
  return request.post('/phone/upload', data)
    .then(response => {
      console.log('上传成功，响应:', response)
      return response
    })
    .catch(error => {
      console.error('上传失败，错误:', error)
      throw error
    })
}
