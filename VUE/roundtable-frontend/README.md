# AI Roundtable 前端

一个现代化的AI圆桌会议前端界面，支持多代理对话和实时WebSocket通信。

## 功能特性

### 🎯 核心功能
- **多代理圆桌会议**: 支持1-N个AI代理同时对话
- **自适应布局**: 根据代理数量自动调整位置
- **实时WebSocket通信**: 与后端服务器实时同步
- **Markdown消息渲染**: 支持富文本消息显示
- **消息过滤**: 点击代理头像过滤相关消息

### 💬 聊天框功能
- **终端风格输入**: 仿终端命令行输入体验
- **快捷键支持**: Enter发送消息
- **消息历史**: 完整的对话历史记录
- **清除功能**: 一键清除所有历史消息
- **演示模式**: 未连接后端时提供本地演示

### 🎨 视觉设计
- **赛博朋克风格**: 深色主题 + 青色 accent
- **全息效果**: 渐变背景和发光效果
- **响应式设计**: 支持不同屏幕尺寸
- **动画过渡**: 平滑的界面动画

## 使用方法

### 1. 启动开发服务器
```bash
npm run serve
# 或
yarn serve
```

### 2. 访问界面
打开浏览器访问 `http://localhost:11124`

### 3. 配置代理
- 点击界面左上角的 "RECONFIGURE" 按钮
- 在设置页面配置AI代理的角色、目标和风格
- 点击 "CONFIRM" 保存配置

### 4. 开始对话
- 在底部的输入框中输入指令
- 按Enter或点击EXECUTE按钮发送
- 观察代理们的实时对话

## 演示模式

当WebSocket未连接到后端服务器时，系统会自动进入演示模式：
- CEO代理会回复演示消息
- 展示完整的UI交互流程
- 无需后端即可体验界面功能

## 技术栈

- **Vue 3**: Composition API
- **Tailwind CSS**: 实用优先的CSS框架
- **@stomp/stompjs**: WebSocket STOMP客户端
- **Markdown-it**: Markdown渲染器

## 项目结构

```
src/
├── components/
│   ├── AgentCard.vue      # 代理卡片组件
│   ├── AgentSetup.vue     # 代理配置组件
│   ├── ControlBar.vue     # 输入控制栏
│   ├── MeetingTable.vue   # 圆桌布局组件
│   └── RoundTableChat.vue # 聊天界面组件
├── composables/
│   └── useRoundtable.js   # 圆桌会议逻辑
└── App.vue               # 主应用组件
```

## Project setup
```
yarn install
```

### Compiles and hot-reloads for development
```
yarn serve
```

### Compiles and minifies for production
```
yarn build
```

### Lints and fixes files
```
yarn lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
