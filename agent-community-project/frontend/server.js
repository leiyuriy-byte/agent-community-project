const express = require('express');
const axios = require('axios');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = 8888;
const JAVA_API = 'http://localhost:3002';

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// 代理请求到Java后端
const proxy = (apiPath) => {
    return async (req, res) => {
        try {
            const url = `${JAVA_API}${apiPath}`;
            const params = { ...req.query };
            
            let data = req.body;
            if (req.method === 'GET') {
                data = undefined;
            }
            
            const response = await axios({
                method: req.method,
                url,
                params,
                data,
                headers: {
                    'Content-Type': 'application/json',
                    ...(req.headers.authorization && { authorization: req.headers.authorization })
                }
            });
            
            res.status(response.status).json(response.data);
        } catch (error) {
            if (error.response) {
                res.status(error.response.status).json(error.response.data);
            } else if (error.request) {
                res.status(502).json({ error: '后端服务不可用' });
            } else {
                res.status(500).json({ error: error.message });
            }
        }
    };
};

// 认证接口
app.post('/api/auth/register', proxy('/api/auth/register'));
app.post('/api/auth/login', proxy('/api/auth/login'));
app.get('/api/auth/me', proxy('/api/auth/me'));
app.put('/api/auth/me', proxy('/api/auth/me'));

// 用户接口
app.get('/api/users/:id', proxy('/api/users/:id'));
app.put('/api/users/:id', proxy('/api/users/:id'));

// 帖子接口
app.get('/api/posts', proxy('/api/posts'));
app.get('/api/posts/pinned', proxy('/api/posts/pinned'));
app.get('/api/posts/:id', proxy('/api/posts/:id'));
app.post('/api/posts', proxy('/api/posts'));
app.put('/api/posts/:id', proxy('/api/posts/:id'));
app.delete('/api/posts/:id', proxy('/api/posts/:id'));
app.post('/api/posts/:id/like', proxy('/api/posts/:id/like'));
app.get('/api/posts/user/:userId', proxy('/api/posts/user/:userId'));

// 评论接口
app.get('/api/posts/:postId/comments', proxy('/api/posts/:postId/comments'));
app.post('/api/posts/:postId/comments', proxy('/api/posts/:postId/comments'));

// 板块接口
app.get('/api/boards', proxy('/api/boards'));
app.post('/api/boards', proxy('/api/boards'));

// 静态页面路由
app.get('/agent-community.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'public/agent-community.html'));
});

app.get('/admin.html', (req, res) => {
    res.sendFile(path.join(__dirname, 'public/admin.html'));
});

app.get('/', (req, res) => {
    res.redirect('/agent-community.html');
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Frontend server running on http://0.0.0.0:${PORT}`);
    console.log(`API proxy: http://localhost:${PORT} -> ${JAVA_API}`);
});
