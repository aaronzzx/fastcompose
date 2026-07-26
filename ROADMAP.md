# FastCompose Roadmap

## 当前状态／进度概览

- 当前阶段：Compose BOM 迁移、工具链升级及下游兼容验证已完成。
- 进行中：已获授权，正在提交并推送 FastCompose。
- 阻塞：无代码阻塞；JitPack `main-SNAPSHOT` 需在 FastCompose 推送后才能远端验证。
- 下一步：推送 FastCompose，再让 SideGesture 脱离本地组合替换执行一次远端快照构建。

## 已完成

- [x] 2026-07-26：完成现有模块、Compose 依赖声明、JitPack 分支和 SideGesture 消费方式盘点。
- [x] 2026-07-26：核对官方 Compose BOM、Compose artifact 元数据及 Kotlin／AGP／Gradle 兼容范围。
- [x] 2026-07-26：补充中文项目规范，并建立本路线图。
- [x] 2026-07-26：迁移到 Compose BOM `2026.06.00`，对齐 Kotlin `2.1.21`、AGP `8.6.1`、Gradle `8.7` 和 Compose Lint `8.8.2`。
- [x] 2026-07-26：通过 `api platform(...)` 将 BOM 写入 `compose` 发布 POM，并移除 BOM 管理范围内的单项版本。
- [x] 2026-07-26：修复 `main` 合并遗留的 Paging／Room 依赖、配置和 Demo 类型缺失，并迁移旧 Demo 到当前 Material 3／SmartRefresh API。
- [x] 2026-07-26：SideGesture 已切换 `main-SNAPSHOT` 和同版 BOM，并通过本地组合构建验证。

## 最近验证

- 2026-07-26：`compose` 与 `compose-accessibility` 的 Release 单测、Lint、AAR 和 Maven POM 生成全部通过，`app` 的 Debug 单测与 APK 构建通过。
- 2026-07-26：生成的 `compose` POM 已包含 `androidx.compose:compose-bom:2026.06.00` 的 `dependencyManagement` import。
- 2026-07-26：SideGesture 使用本地依赖替换执行 `testDebugUnitTest assembleDebug` 成功，确认两个 FastCompose 模块均参与消费构建。
- 2026-07-26：SideGesture 依赖解析确认 AndroidX Compose UI 为 `1.11.3`，约束来源为 BOM `2026.06.00`。
- 2026-07-26：确认 Compose UI `1.11.3` 要求 `minCompileSdk 35`、最低 AGP `8.6.0`，并依赖 Kotlin stdlib `2.1.20`。

## 长期待办

### 依赖与构建

- 保持 FastCompose 与主要下游项目的 Compose BOM 版本一致。
- Compose 或构建工具升级后检查 Maven POM，确保消费者能够继承 BOM 约束。

### 质量保障

- 为公共组件和分页状态逻辑逐步补充自动化测试。
- 在发布前验证两个库模块、示例应用及主要下游项目。
