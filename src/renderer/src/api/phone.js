import request from '../utils/request'

/**
 * 获取卡号信息列表
 */
export const getPhoneList = (paramsData) =>{
  const params = new URLSearchParams()
  for (let key in paramsData){
    params.append(key, paramsData[key]);
  }
  return request.get('/phone', params)
}
