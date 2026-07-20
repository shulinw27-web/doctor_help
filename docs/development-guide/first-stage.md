# 第一阶段开发说明

## 已实现

- Java/Spring Boot 后端骨架。
- 模拟医生、患者、就诊及工作台统计数据。
- `GET /api/v1/doctor-workbench` 演示接口。
- Vue 工作台：医生信息、统计卡片、患者列表和模拟数据提示。

## 尚未实现

- Electron 桌面主进程已配置；桌面打包尚未配置。
- 患者详情、检验结果、影像报告、AI 汇总、知识库、管理端。
- 数据库、认证、审计和真实系统适配器。

## 运行前提

- JDK 21、Maven 3.9+。
- Node.js 20+ 与 npm。
- 后端：在 `doctor-help-backend` 执行 `mvn spring-boot:run`。
- 前端：在 `doctor-help-frontend` 执行 `npm install` 后执行 `npm run dev`。
