import request from '../utils/request'

/**
 * 获取卡号信息列表
 */
export const getPhoneList = (paramsData) => {
  const params = new URLSearchParams()
  for (let key in paramsData) {
    params.append(key, paramsData[key])
  }
  return request.get('/phone/phoneList', { params: params })
}

/**
 * 获取手机号详情信息
 * @param {Number} phoneId - 手机号ID
 * @returns {Promise} - 返回手机号详情的Promise
 */
export const getPhoneDetail = (phoneId) => {
  return request.get(`/phone/phoneDetail?phoneId=${phoneId}`)
}

/**
 * 上传手机号码批量导入
 * @param {Object} data - 上传数据，包括国家、项目和文件数据
 * @returns {Promise} - 返回上传结果的Promise
 */
export const uploadPhoneNumbers = (data) => {
  return request.post('/phone/upload', data)
}
