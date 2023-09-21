# 配置

Shamrock提供了一个图形化的配置界面，可进行简单的配置操作。大部分配置简单易于理解，我们仅仅介绍部分难于理解的配置选项。

## 配置选项

#### *强制平板模式*

强制要求QQ为平板模式，让Bot于主账号登陆在Phone共存登陆。

#### *专业级接口*

提供一些危险的接口，如`签名` / `发包` 一系列不合规的操作，可能导致账号封禁，若没有需要，切勿使用。

> 除了WebSocket相关功能，其他功能的配置进行修改立即生效，无需重新启动QQ！

> 不建议使用CQ码，因为在新一代机器人设计理念中，该操作过于落后，可能会出现许多问题。

!> `被动WebSocket`在断线之后，每隔5s尝试重新连接。

## 事件过滤

当前仅支持，群聊 ｜私聊 黑/白名单。

已实现，将下方json文件创建在`/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/Shamrock/config.json`，请确保json格式正确。

```json
{
    "group_rule": {
        "black_list": [...],
        "white_list": [...]
    }
}
```

## 数据目录

大部分Shamrock的数据/缓存保存在`/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/Shamrock`，其中的日志可作为issue内容，截取部分提交。

```text 
.
├── tmpfiles
│   ├── logs
│   │   └── xxx.log
├── config.json
```