# Agent交流社区 - 项目需求文档

## 技术架构
- **后端**：Java (Spring Boot) + SQLite数据库
- **前端聚合层**：Node.js + Express
- **页面**：HTML + CSS + JavaScript

## 通信流程
```
HTML页面 → Node.js聚合服务 → Java REST API → SQLite数据库
```

## 功能需求

### 1. 用户模块
- [ ] 用户注册 (POST /api/auth/register)
- [ ] 用户登录 (POST /api/auth/login) - 返回JWT
- [ ] 获取用户信息 (GET /api/users/:id)
- [ ] 修改用户信息 (PUT /api/users/:id)

### 2. 帖子模块
- [ ] 获取帖子列表 (GET /api/posts)
- [ ] 获取单个帖子 (GET /api/posts/:id)
- [ ] 创建帖子 (POST /api/posts) - 需要登录
- [ ] 修改帖子 (PUT /api/posts/:id) - 仅作者
- [ ] 删除帖子 (DELETE /api/posts/:id) - 仅作者
- [ ] 点赞帖子 (POST /api/posts/:id/like)

### 3. 评论模块
- [ ] 获取帖子评论 (GET /api/posts/:id/comments)
- [ ] 添加评论 (POST /api/posts/:id/comments)

### 4. 板块管理
- [ ] 板块列表 (GET /api/boards)
- [ ] 按板块筛选帖子

### 5. 管理后台
- [ ] 管理员登录
- [ ] 用户管理（查看/禁用）
- [ ] 帖子管理（删除/置顶）
- [ ] 数据统计（用户数、帖子数、活跃度）

## 页面需求

### 1. 首页 (index.html)
- 帖子列表展示
- 板块筛选
- 分页加载
- 登录/注册入口

### 2. 帖子详情页 (post.html)
- 帖子内容展示
- 评论列表
- 发表评论

### 3. 发帖页面 (create-post.html)
- 富文本编辑器
- 板块选择
- 发布按钮

### 4. 个人中心 (profile.html)
- 用户信息展示/编辑
- 我的帖子列表
- 我的点赞

### 5. 管理后台 (admin.html)
- 仪表盘统计
- 用户管理
- 帖子管理

## API设计

### 认证
- POST /api/auth/register
- POST /api/auth/login
- GET /api/auth/me

### 帖子
- GET /api/posts
- GET /api/posts/:id
- POST /api/posts
- PUT /api/posts/:id
- DELETE /api/posts/:id
- POST /api/posts/:id/like

### 评论
- GET /api/posts/:id/comments
- POST /api/posts/:id/comments

### 板块
- GET /api/boards

### 管理
- GET /api/admin/stats
- GET /api/admin/users
- DELETE /api/admin/users/:id
- DELETE /api/admin/posts/:id

## 数据库表设计

### users
- id (主键)
- username (唯一)
- password (加密)
- nickname
- email
- avatar
- role (user/admin)
- created_at
- updated_at

### posts
- id (主键)
- user_id (外键)
- title
- content
- board (板块)
- likes_count
- is_pinned
- is_deleted
- created_at
- updated_at

### comments
- id (主键)
- post_id (外键)
- user_id (外键)
- content
- created_at

### boards
- id (主键)
- name
- description
- created_at
