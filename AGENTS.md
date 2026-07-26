# FastCompose 项目约定

## 项目概览

FastCompose 是面向 Android 原生项目的 Jetpack Compose 工具库，提供通用 Compose 组件、分页与状态封装、扩展函数，以及无障碍服务中的 Compose 接入能力。

## 模块职责

- `compose/`：核心库模块，包含基础类、通用组件、分页、状态容器、扩展函数和自定义绘制能力。
- `compose-accessibility/`：无障碍服务 Compose 接入模块，依赖 `compose`。
- `app/`：示例应用，用于演示和验证两个库模块的集成。
- `build.gradle`：统一管理 Kotlin、Android Gradle Plugin 和 Compose Compiler 插件版本。
- `ROADMAP.md`：记录当前阶段、阻塞、已完成事项和最近验证结果。

## 构建与验证

根据当前操作系统使用仓库自带的 Gradle Wrapper，不依赖 IDE。macOS／Linux 使用：

```bash
bash gradlew :compose:assembleRelease :compose-accessibility:assembleRelease
bash gradlew :app:assembleDebug
bash gradlew test
```

Windows 使用：

```powershell
.\gradlew.bat :compose:assembleRelease :compose-accessibility:assembleRelease
.\gradlew.bat :app:assembleDebug
.\gradlew.bat test
```

依赖或构建配置改动后，至少完成两个库模块的 Release 构建和示例应用 Debug 构建。涉及发布配置时，同时检查生成的 Maven POM 和依赖解析结果。

## Compose 与依赖管理

- Compose 依赖统一通过官方 Compose BOM 对齐，不为 BOM 管理范围内的单个 Compose artifact 单独声明版本。
- `compose` 是对外库模块，公共 API 使用到的 Compose 依赖保持 `api` 作用域；BOM 约束也必须通过 `api platform(...)` 暴露给消费者。
- Android 测试和 Debug 专用 Compose 依赖需要在对应配置中导入同一 BOM。
- Compose Compiler 插件版本必须与 Kotlin 版本一致。
- 新增依赖前先确认标准库或现有依赖是否足够；不引入功能重复的库。
- 只升级本次任务需要的依赖，不顺带批量更新无关依赖。

## 代码修改规则

- Kotlin 类成员顺序遵循：`companion object` → 字段 → `init` 和次构造函数 → 函数 → 内部类。
- Java 类成员顺序遵循：静态字段和静态函数 → 实例字段 → 构造函数 → 实例函数 → 内部类。
- `app` 主模块禁止使用 `internal`；库模块可根据公开 API 边界使用 `internal`。
- 公共库 API 变更必须考虑二进制兼容和下游 SideGesture 的使用方式。
- 只修复与当前任务直接相关的问题，不做无关重构、格式化或代码清理。
- 修改 Compose API 后，必须同时验证 `compose`、`compose-accessibility` 和示例应用。

## 发布约束

- `compose` 与 `compose-accessibility` 使用 `maven-publish`，发布配置位于各自模块的 `build.gradle`。
- 修改发布依赖后必须检查生成 POM 是否保留 Compose BOM 约束。
- JitPack 分支、版本坐标、Git tag、远端 push 和公开发布都必须在执行前获得用户确认。
- 密钥、token、密码不得写入代码、构建脚本、日志或提交记录。

## Roadmap 维护

- `ROADMAP.md` 顶部必须保留“当前状态／进度概览”，包含当前阶段、进行中、阻塞和下一步，合计不超过 10 条。
- 只有已经实现并验证的事项才能写入“已完成”。
- 每次完成开发、修复、文档补齐或重要调研后同步更新 `ROADMAP.md`。
- `README.md` 负责项目介绍和使用方式，`ROADMAP.md` 只记录会变化的进度。
