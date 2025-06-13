import request from '../utils/request'

/**
 * 获取卡号信息列表
 */
export const getPhoneList = (paramsData) => {
  const params = new URLSearchParams()
  for (let key in paramsData) {
    params.append(key, paramsData[key])
  }
  return request.get('/merchant/phone/list', { params: params })
}

/**
 * 获取项目和地区数据
 */
export const getProjectAndRegionData = () => {
  return request.get('/merchant/phone/projectsAndRegion')
}

/**
 * 获取手机号详情信息
 * @param {Number} phoneId - 手机号ID
 * @returns {Promise} - 返回手机号详情的Promise
 */
export const getPhoneDetail = (phoneId) => {
  return request.get(`/merchant/phone/phoneDetail?phoneId=${phoneId}`)
}

/**
 * 上传手机号码批量导入
 * @param {Object} data - 上传数据，包括地区ID、项目ID和文件数据
 * @returns {Promise} - 返回上传结果的Promise
 */
export const uploadPhoneNumbers = (data) => {
  return request.post('/merchant/phone/upload', data)
}
