import React from 'react';
import { omit, isEmpty } from 'lodash';
import { Choerodon, axios, stores } from '@choerodon/boot';

/**
 * 将以base64的图片url数据转换为Blob
 * @param {string} urlData 用url方式表示的base64图片数据
 */
function convertBase64UrlToBlob(urlData) {
  const bytes = window.atob(urlData.split(',')[1]); // 去掉url的头，并转换为byte

  // 处理异常,将ascii码小于0的转换为大于0
  const buffer = new ArrayBuffer(bytes.length);
  const unit8Array = new Uint8Array(buffer);
  for (let i = 0; i < bytes.length; i += 1) {
    unit8Array[i] = bytes.charCodeAt(i);
  }

  return new Blob([buffer], { type: 'image/png' });
}
/**
 * 从deltaOps中获取图片数据
 * @param {DeltaOperation []} deltaOps
 */
function getImgInDelta(deltaOps) {
  const imgBase = [];
  const formData = new FormData();
  deltaOps.forEach((item) => {
    if (item.insert && item.insert.image) {
      if (item.insert.image.split(':').length && item.insert.image.split(':')[0] === 'data') {
        imgBase.push(item.insert.image);
        formData.append('file', convertBase64UrlToBlob(item.insert.image), 'blob.png');
      }
    }
  });
  return { imgBase, formData };
}

/**
 * 将富文本中的base64图片替换为对应的url
 * @param {{url:string} []} imgUrlList 图标url对应的
 * @param {any []} imgBase base64图片数组
 * @param {*} text 富文本的文本结构
 */
function replaceBase64ToUrl(imgUrlList, imgBase, text) {
  const deltaOps = text;
  const imgMap = {};
  imgUrlList.forEach((imgUrl, index) => {
    imgMap[imgBase[index]] = `${imgUrl}`;
  });
  deltaOps.forEach((item, index) => {
    if (item.insert && item.insert.image && imgBase.indexOf(item.insert.image) !== -1) {
      deltaOps[index].insert.image = imgMap[item.insert.image];
    }
  });
}

function text2Delta(description) {
  if (!description) {
    return undefined;
  }
  // eslint-disable-next-line no-restricted-globals
  if (!isNaN(description)) {
    return String(description);
  }
  let temp = description;
  try {
    temp = JSON.parse(description.replace(/\\n/g, '\\n')
      .replace(/\\'/g, "\\'")
      .replace(/\\"/g, '\\"')
      .replace(/\\&/g, '\\&')
      .replace(/\\r/g, '\\r')
      .replace(/\\t/g, '\\t')
      .replace(/\\b/g, '\\b')
      .replace(/\\f/g, '\\f'));
  } catch (error) {
    temp = description;
  }
  // return temp;
  return temp || '';
}

// eslint-disable-next-line no-restricted-globals
export function getParams(url = location.href) {
  const theRequest = {};
  if (url.indexOf('?') !== -1) {
    const str = url.split('?')[1];
    const strs = str.split('&');
    for (let i = 0; i < strs.length; i += 1) {
      theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}
function commonFormatDate(str) {
  const MONTH = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二'];
  if (!str) {
    return '';
  }
  const arr = str.split(' ');
  if (arr.length < 1) {
    return '';
  }
  const date = arr[0];
  const time = arr[1];
  if (!arr[0] || !arr[1]) {
    return '';
  }
  const d = date.split('-');
  const t = time.split(':');
  if (d.length < 3 || t.length < 3) {
    return '';
  }
  // eslint-disable-next-line
  return `${d[2]}/${MONTH[d[1] * 1 - 1]}月/${d[0]} ${t[0] < 12 ? t[0] : t[0] * 1 - 12}:${t[1]}  ${t[0] * 1 < 12 ? ' 上' : ' 下'}午`;
}

/**
 * 生成指定长度的随机字符串
 * @param len 字符串长度
 * @returns {string}
 */
function randomString(len = 32) {
  let code = '';
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  const maxPos = chars.length;
  for (let i = 0; i < len; i += 1) {
    code += chars.charAt(Math.floor(Math.random() * (maxPos + 1)));
  }
  return code;
}
/**
 * randomWord 产生任意长度随机字母数字组合
 * @param randomFlag 是否任意长度 min-任意长度最小位[固定位数] max-任意长度最大位
 * @param min
 * @param max
 * @returns {string}
 */
function randomWord(randomFlag, min, max) {
  let str = '';
  let range = min;
  const arr = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

  // 随机产生
  if (randomFlag) {
    range = Math.round(Math.random() * (max - min)) + min;
  }
  for (let i = 0; i < range; i += 1) {
    const pos = Math.round(Math.random() * (arr.length - 1));
    str += arr[pos];
  }
  return str;
}

/**
 * 动态计算名称宽度
 * @param val
 * @returns {number}
 */
function getByteLen(val) {
  let len = 0;
  for (let i = 0; i < val.length; i += 1) {
    const a = val.charAt(i);
    if (a.match(/[^\x00-\xff]/ig) !== null) { // \x00-\xff→GBK双字节编码范围
      len += 15;
    } else {
      len += 10;
    }
  }
  return len;
}

/**
 * 解析url
 * @param url
 * @returns {{}}
 */
function getRequest(url) {
  const theRequest = {};
  if (url.indexOf('?') !== -1) {
    const str = url.split('?')[1];
    const strs = str.split('&');
    for (let i = 0; i < strs.length; i += 1) {
      theRequest[strs[i].split('=')[0]] = decodeURI(strs[i].split('=')[1]);
    }
  }
  return theRequest;
}

/**
 * 获取文件名后缀
 * @param {string} fileName 
 */
function getFileSuffix(fileName) {
  return fileName.replace(/.+\./, '').toLowerCase();
}

/**
 * 得到get请求后面的参数部分,并去掉参数值为空的
 * @param param
 * @returns {String}
 */

/* eslint-disable */
function getUrlParam(param) {
  let on = true;
  let result = '';
  for (const item in param) {
    if (on) {
      on = false;
      if (param[item] || param[item] === 0 || param[item] === false) {
        result = `?${item}=${param[item]}`;
      } else {
        result = '?';
      }
    } else if (param[item] || param[item] === 0 || param[item] === false) {
      result = `${result}&${item}=${param[item]}`;
    }
  }
  return result;
}


/**
 * 数据请求后的错误拦截
 * 不建议使用此错误处理方法
 * @param data
 * @param hasReturn
 */
function handlePromptError(data, hasReturn = true) {
  if (hasReturn && !data) return false;

  if (data && data.failed) {
    Choerodon.prompt(data.message);
    return false;
  }

  return true;
}

/**
 * 参数 长度低于2则前面加 0，否则不加
 * @param {string | number} str
 * @returns {string}
 */
function padZero(str) {
  return str.toString().padStart(2, '0');
}

/**
 * 格式化时间，转化为 YYYY-MM-DD HH:mm:ss
 * @param {Date} timestamp
 * @returns {string}
 */
function formatDate(timestamp) {
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hour = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();

  return `${[year, month, day].map(padZero).join('-')} ${[hour, minutes, seconds].map(padZero).join(':')}`;
}

/**
 * 计算剩余时间
 * @param now 当前时间 时间戳
 * @param end 结束时间 时间戳
 * @returns {string}
 */
function getTimeLeft(now, end) {
  if (now >= end) {
    return '剩余 0 天';
  }
  const resTime = end - now;
  const days = Math.floor(resTime / (24 * 3600 * 1000));
  return `剩余 ${days} 天`;
}

/**
 * 将毫秒数转为时分秒格式
 * @param time 毫秒数
 */
function timeConvert(time) {
  if (!time || typeof time !== 'number') {
    return;
  }
  // 毫秒转为秒
  const now = time / 1000;
  const sec = Math.floor((now % 60) % 60);
  const min = Math.floor(now / 60) % 60;
  const hour = Math.floor(now / 3600);

  let result = `${sec}s`;
  if (hour > 0) {
    result = `${hour}h ${min}m ${sec}s`;
  } else if (hour <= 0 && min > 0) {
    result = `${min}m ${sec}s`;
  }

  return result;
}

function removeEndsChar(str, char) {
  if (typeof str !== 'string') return '';

  return str.endsWith(char) ? str.slice(0, -1) : str;
}

async function checkPermission({ projectId, organizationId, resourceType, code }) {
  const params = { tenantId: resourceType === 'site' ? 0: organizationId };
  if (resourceType === 'project') {
    params.projectId = projectId;
  }

  try {
    const res = await axios({
      method: 'post',
      url: '/iam/choerodon/v1/permissions/menus/check-permissions',
      data: code,
      params,
    });
    if (res && res.failed) {
      return false;
    } else if (res && res.length) {
      const [{ approve }] = res;
      return approve;
    }
  } catch (e) {
    // Choerodon.handleResponseError(e);
    return false;
  }
}

function useCheckPermission(code) {
  const [hasPermission, setHasPermission] = React.useState(false);
  React.useEffect(() => {
    async function fetchPermission() {
      const { projectId, type: resourceType } = stores.AppState.currentMenuType;
      const res = await checkPermission({ projectId, code, resourceType });
      setHasPermission(res);
    }
    fetchPermission();
  }, [...code, code.length])
  return hasPermission;
}

/**
 * 因为c7n-ui下拉框全做了缓存，我们没有权限改uiConfig的全局配置，同时业务要求不缓存
 * 使用参见react\routes\product-lib\management\Pages\AddAuthButton\MavenAddMemberButton\AddMemberModal.js
 * @param {lookupCode?: string,lookupUrl?: string} param0 快码编码或者快码url
 */
function useNoCacheSelectData({ lookupCode, lookupUrl }) {
  const [dataSource, setDataSource] = React.useState([]);
  React.useEffect(() => {
    async function fetchSelectData() {
      let res = [];
      if (lookupCode) {
        res = await axios.get(`/hpfm/v1/lovs/value?lovCode=${lookupCode}`);
      } else if (lookupUrl) {
        res = await axios.get(lookupUrl);
      } else {
        throw new Error("参数不对");
      }
      setDataSource(res);
    }
    fetchSelectData();
  }, [lookupCode, lookupUrl])
  return dataSource;
}

function eventStopProp(e) {
  e.stopPropagation();
}

/**
 * 表格搜索数据，并在初始化请求无相应字段时自动添加
 * @param data
 * @returns {{searchParam, params: Array}}
 */
export default function (data) {
  let params = [];
  let searchParam = {};

  if (!isEmpty(data)) {
    params = typeof data.params === 'string' ? [data.params] : data.params;
    searchParam = omit(data, ['params']);
  }

  return {
    params,
    searchParam,
  };
}

/**
 * 返回字符串的首字
 * @param {string} str 
 */
function getFirstWord(str) {
  if (!str) {
    return '';
  }
  const re = /[\u4E00-\u9FA5]/g;
  for (let i = 0, len = str.length; i < len; i += 1) {
    if (re.test(str[i])) {
      return str[i];
    }
  }
  return str[0];
}

function handleRequestFailed(promise) {
  return promise.then((res) => {
    if (res.failed) {
      Choerodon.prompt(res.message, 'error');
      throw new Error(res.message);
    } else {
      return res;
    }
  });
}

export {
  convertBase64UrlToBlob,
  getImgInDelta,
  replaceBase64ToUrl,
  text2Delta,
  getUrlParam,
  formatDate,
  commonFormatDate,
  randomString,
  randomWord,
  getByteLen,
  getRequest,
  getFileSuffix,
  getTimeLeft,
  timeConvert,
  handlePromptError,
  removeEndsChar,
  checkPermission,
  eventStopProp,
  getFirstWord,
  handleRequestFailed,
  useCheckPermission,
  useNoCacheSelectData,
};
