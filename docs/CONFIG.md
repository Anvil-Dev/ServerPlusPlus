本分支完整支持 Minecraft 1.21.10，NeoForge 依赖范围为 `[21.10,)`，使用 Java 21。

保留管理员及名单命令、待办事项、位置分享、跨服转移、聊天提及与欢迎消息。

构建命令为 `./gradlew.bat compileJava build --console=plain`，输出 `build/libs/server_plus_plus-0.0.1-mc1.21.10.jar`。构建依赖同版本的 RollingGate；本次交付通过 Gradle 复合构建使用同目录的 RollingGate 源码。
