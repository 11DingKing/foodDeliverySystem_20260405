const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 9000;

// 启用 CORS
app.use(cors());

// 静态文件服务
app.use('/images', express.static(path.join(__dirname, 'images')));

// 健康检查
app.get('/health', (req, res) => {
  res.json({ status: 'ok', message: '静态资源服务运行中' });
});

// 404 处理
app.use((req, res) => {
  res.status(404).json({ error: '资源不存在' });
});

app.listen(PORT, () => {
  console.log(`静态资源服务已启动: http://localhost:${PORT}`);
  console.log(`图片访问地址: http://localhost:${PORT}/images/`);
});
