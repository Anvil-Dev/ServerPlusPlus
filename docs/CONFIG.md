本分支完整支持 Minecraft 1.21.11，NeoForge 依赖范围为 `[21.11,)`，使用 Java 21。

保留管理员及名单命令、待办事项、位置分享、跨服转移、聊天提及与欢迎消息。

构建命令为 `./gradlew.bat compileJava build --console=plain`，输出 `build/libs/server_plus_plus-0.0.1-mc1.21.11.jar`。构建时优先使用相邻目录中的同版本 RollingGate 源码，可用 `-Prolling_gate_source=路径` 指定源码位置。
