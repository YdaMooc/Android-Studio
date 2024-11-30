const os = require('os');
const express = require('express');
const axios = require('axios');
const app = express();

const AMAP_API_KEY = '0fff6d333a597852034f78dfaa8a0c00'; // API密钥
const AMAP_GEOCODE_URL = 'https://restapi.amap.com/v3/geocode/geo';
const AMAP_WEATHER_URL = 'https://restapi.amap.com/v3/weather/weatherInfo';

console.log('本机IP地址：' + getLocalIPAddress().join(', '));

// 处理聊天机器人的请求
app.use(express.json());

const chatDefault = '抱歉，我无法理解您的问题，请再描述详细一些。';

const chatData = {
  '你好': '你好你好，有什么可以帮助你。',
  '您好': '您好您好，有什么可以帮助你。',
  '西红柿好吃吗': '不管和什么搭起来都很好吃。',
  '西红柿炒鸡蛋呢': '这个也是下饭的好菜',
  '功能': `
当前支持的功能：
1. 天气查询 - 发送“天气+城市名称”，例如“天气北京”。
2. 天气预报 - 发送“预报天气+城市名称”，例如“预报天气北京”。
`
};

// 主逻辑处理 POST 请求
app.post('/', async (req, res) => {
  let resText = '';
  const data = req.body;

  console.log('收到消息：', data);

  if (data.perception && data.perception.inputText && data.perception.inputText.text) {
    const text = data.perception.inputText.text;

    // 判断输入是否为实时天气查询
    if (text.startsWith('天气')) {
      const city = text.replace('天气', '').trim();
      if (city) {
        try {
          const adcode = await getAdcode(city);
          if (adcode) {
            const weather = await queryWeather(adcode);
            resText = weather || `无法获取${city}的天气信息，请稍后再试。`;
          } else {
            resText = `无法找到城市“${city}”对应的行政区划代码，请检查城市名称是否正确。`;
          }
        } catch (err) {
          console.error('查询天气时出错：', err);
          resText = `查询天气时发生错误，请稍后重试。`;
        }
      } else {
        resText = '请提供查询的城市名称，例如“天气北京”。';
      }
    }

    // 判断输入是否为天气预报查询
    else if (text.startsWith('预报天气')) {
      const city = text.replace('预报天气', '').trim();
      if (city) {
        try {
          const adcode = await getAdcode(city);
          if (adcode) {
            const forecast = await queryWeatherForecast(adcode);
            resText = forecast || `无法获取${city}的天气预报，请稍后再试。`;
          } else {
            resText = `无法找到城市“${city}”对应的行政区划代码，请检查城市名称是否正确。`;
          }
        } catch (err) {
          console.error('查询天气预报时出错：', err);
          resText = `查询天气预报时发生错误，请稍后重试。`;
        }
      } else {
        resText = '请提供查询的城市名称，例如“天气预报北京”。';
      }
    }

    // 常规聊天处理
    else if (chatData[text]) {
      resText = chatData[text];
    } else {
      resText = chatDefault;
    }
  } else {
    resText = '请求参数有误';
  }

  res.json({
    intent: {
      code: 0
    },
    results: [{
      values: {
        text: resText
      }
    }]
  });
});

app.listen(3000, () => {
  console.log('聊天机器人服务器端已经启动，监听端口为：3000');
});

// 获取本机IP地址
function getLocalIPAddress() {
  const ip = [];
  const interfaces = os.networkInterfaces();
  for (const name of Object.keys(interfaces)) {
    const networkInterface = interfaces[name];
    for (const network of networkInterface) {
      if (!network.internal && network.family === 'IPv4') {
        ip.push(network.address);
      }
    }
  }
  return ip;
}

// 获取城市的行政区划代码（adcode）
async function getAdcode(city) {
  try {
    const response = await axios.get(AMAP_GEOCODE_URL, {
      params: {
        key: AMAP_API_KEY,
        address: city,
      }
    });
    if (response.data && response.data.geocodes && response.data.geocodes.length > 0) {
      return response.data.geocodes[0].adcode; // 返回第一个匹配的adcode
    } else {
      console.log(`未找到城市${city}的adcode`);
      return null;
    }
  } catch (err) {
    console.error('请求高德地理编码API时出错：', err);
    throw err;
  }
}

// 查询实时天气
async function queryWeather(adcode) {
  try {
    const response = await axios.get(AMAP_WEATHER_URL, {
      params: {
        key: AMAP_API_KEY,
        city: adcode,
        extensions: 'base', // 基本信息
        output: 'JSON'
      }
    });
    if (response.data && response.data.lives && response.data.lives.length > 0) {
      const weatherInfo = response.data.lives[0];
      console.log('返回：', weatherInfo);
      return `
当前城市：${weatherInfo.city}
--------------------------------
天气状况：${weatherInfo.weather}
当前温度：${weatherInfo.temperature}°C
风        向：${weatherInfo.winddirection}
风        力：${weatherInfo.windpower}级
湿        度：${weatherInfo.humidity}%
--------------------------------`;
    } else {
      return '未能获取实时天气信息，请稍后再试。';
    }
  } catch (err) {
    console.error('请求高德天气API时出错：', err);
    throw err;
  }
}

// 查询天气预报
async function queryWeatherForecast(adcode) {
  const weekMap = {
    '1': '一',
    '2': '二',
    '3': '三',
    '4': '四',
    '5': '五',
    '6': '六',
    '7': '日'
  };
  try {
    const response = await axios.get(AMAP_WEATHER_URL, {
      params: {
        key: AMAP_API_KEY,
        city: adcode,
        extensions: 'all', // 天气预报信息
        output: 'JSON'
      }
    });
    if (response.data && response.data.forecasts && response.data.forecasts.length > 0) {
      const forecastInfo = response.data.forecasts[0];
      let forecastText = `
未来几天天气预报：${forecastInfo.city}
--------------------------------`;
      forecastInfo.casts.forEach((cast) => {
        const chineseWeek = weekMap[cast.week] || cast.week;
        forecastText += `
日        期：${cast.date}
星        期：${chineseWeek}
白天天气：${cast.dayweather}
夜间天气：${cast.nightweather}
温度范围：${cast.nighttemp_float}°C ~ ${cast.daytemp_float}°C
风        力：白天${cast.daypower}级，夜晚${cast.nightpower}级
--------------------------------`;
      });
      console.log('返回：', forecastInfo);
      return forecastText;
    } else {
      return '未能获取天气预报信息，请稍后再试。';
    }
  } catch (err) {
    console.error('请求高德天气预报API时出错：', err);
    throw err;
  }
}
