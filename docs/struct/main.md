# 数据结构

## POST_TYPE

| 值              | 说明                 |
|----------------|--------------------|
| message        | 消息，例如群聊消息          |
| ~message_sent~ | 消息发送，例如bot发送在群里的消息 |
| ~request~      | 请求，例如好友申请          |
| notice         | 通知，例如群成员增加         |

## MessageSender

如果是私聊：

| 字段名      | 数据类型   | 说明                             |
|----------|--------|--------------------------------|
| user_id  | int64  | 发送者 QQ 号                       |
| nickname | string | 昵称                             |

如果是群聊：

| 字段名      | 数据类型   | 说明                             |
|----------|--------|--------------------------------|
| user_id  | int64  | 发送者 QQ 号                       |
| nickname | string | 昵称                             |
| card     | string | 群名片／备注                         |
| level    | string | 成员等级                           |
| role     | string | 角色，可能是`member`,`admin`,`owner` |
| title    | string | 专属头衔                           |

该消息在 "message" 上报中被使用。

## Message_Type

下面是将给定的枚举值和说明转换为Markdown表格的结果：

| 值       | 说明   |
|---------|------|
| private | 私聊消息 |
| group   | 群消息  |

## Message_SubType

| 值           | 说明     |
|-------------|--------|
| friend      | 好友     |
| normal      | 群聊     |
| ~anonymous~ | 匿名     |
| group_self  | 群中自身发送 |
| group       | 群临时会话  |
| notice      | 系统提示   |

## Request_Type

| 值       | 说明   |
|---------|------|
| friend  | 好友   |
| group   | 群    |

## Notice_Type

| 值              | 说明      |
|----------------|---------|
| group_upload   | 群文件上传   |
| group_admin    | 群管理员变更  |
| group_decrease | 群成员减少   |
| group_increase | 群成员增加   |
| group_ban      | 群成员禁言   |
| friend_add     | 好友添加    |
| group_recall   | 群消息撤回   |
| friend_recall  | 好友消息撤回  |
| group_card     | 群名片变更   |
| offline_file   | 离线文件上传  |
| client_status  | 客户端状态变更 |
| essence        | 精华消息    |
| notify         | 系统通知    |

## Notice_Notify_SubType

| 值          | 说明      |
|------------|---------|
| honor      | 群荣誉变更   |
| poke       | 戳一戳     |
| lucky_king | 群红包幸运王  |
| title      | 群成员头衔变更 |
