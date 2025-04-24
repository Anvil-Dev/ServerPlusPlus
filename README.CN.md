# Server++

> 关于服务器技术选项控制的 Minecraft Neoforge 模组

## 主要特性

### 视距

> 修改服务器视距

* 用法: `/rg viewDistance <视距>`

### 模拟距离

> 修改服务器模拟距离

* 用法: `/rg simulationDistance <模拟距离>`

### `/here` 命令

> 快速发送位置

* 启用: `/rg commandHere true`
* 用法:
    * `/here`

### `/whereis` 命令

> 快速定位玩家

* 启用: `/rg commandWhereis true`
* 用法:
    * `/whereis <玩家>`
    * `/vris <玩家>`

### `/todo` 命令

> 待办事项管理菜单

* 启用: `/rg commandTodo true`
* 用法:
    * `/todo` 或 `/todo list` 显示待办事项列表
    * `/todo add <内容>` 添加待办事项
    * `/todo remove <索引>` 删除待办事项
    * `/todo success <索引>` 将待办事项标记为已完成
    * `/todo success <索引> false` 取消将待办事项标记为已完成

### `/loc` 命令

> 地标管理菜单

* 启用: `/rg commandLoc true`
* 用法:
    * `/loc` 或 `/loc list` 显示地标列表
    * `/loc add <名称>` 添加玩家的位置为地标
    * `/loc remove <索引>` 删除地标
    * `/loc info <索引>` 显示地标信息

### `/wlist` 命令

> 白名单管理

* 启用: `/rg commandWlist true`
* 用法:
    * `/wlist` 显示白名单列表
    * `/wlist add <玩家>` 将玩家添加到白名单
    * `/wlist remove <玩家>` 从白名单中删除玩家
    * `/wlist permission add <玩家>` 使玩家可以使用 /wlist 命令
    * `/wlist permission remove <玩家>` 使玩家不能使用 /wlist 命令

### `/blist` 命令

> 封禁列表管理

* 启用: `/rg commandBlist true`
* 用法:
    * `/blist` 显示封禁列表
    * `/blist add <玩家>` 将玩家添加到封禁列表
    * `/blist remove <玩家>` 从封禁列表中删除玩家
    * `/blist permission add <玩家>` 使玩家可以使用 /blist 命令
    * `/blist permission remove <玩家>` 使玩家不能使用 /blist 命令

### `/sop` 命令

> 简单的获取 op

* 启用: `/rg commandSop true`
* 用法:
    * `/sop` 获取 op 权限

### `/transfer` 命令

> 允许玩家使用 `/transfer` 命令自行转移

* 启用: `/rg commandTransfer true`

### 快速 Ping 好友

> 快速 ping 你的朋友

* 启用: `/rg fastPingFriend true`
* 用法:
    * `@ <player>` 在聊天中提醒您的朋友并播放捡起经验球的声音
    * `@@ <player>` 显示标题提醒朋友并播放敲钟的声音

### 欢迎玩家

> 当玩家登录服务器时发送欢迎消息

* 启用: `/rg welcomePlayer true`
