# 事件

## 通用数据

下面定义了上报数据所包含的通用数据, 这些可能在你进行面向对象封装并处理继承关系时有用

### 所有上报

所有上报都将包含下面的有效通用数据


| 字段名        | 	数据类型  | 	可能的值                                     | 	说明                               |
|------------|--------|-------------------------------------------|-----------------------------------|
| time	      | int64  | 	-                                        | 	事件发生的unix时间戳                     |
| self_id	   | int64  | 	-	                                       | 收到事件的机器人的 QQ 号                    |
| post_type	 | string | 参考	message, message_sent, request, notice | 	表示该上报的类型, 消息, 消息发送, 请求, 通知, 或元事件 |

*注*: `message`与`message_sent`的数据是一致的, 区别仅在于后者是bot发出的消息. 默认配置下不会上报`message_sent`，当前Shamrock并不支持上报`message_sent`，如果有需要请提交issue。

## 消息上报

`post_type` 为 `message` 或 `message_sent` 的上报将会有以下有效通用数据

| 字段名          | 数据类型                                    | 可能的值           | 说明        |
|--------------|-----------------------------------------|----------------|-----------|
| message_type | string                                  | private, group | 消息类型      |
| sub_type     | string                                  | group, public  | 表示消息的子类型  |
| message_id   | int32                                   | -              | 消息 ID     |
| user_id      | int64                                   | -              | 发送者 QQ 号  |
| message      | message                                 | -              | 一个消息链     |
| raw_message  | string                                  | -              | CQ 码格式的消息 |
| font         | int                                     | 0              | 字体        |
| sender       | [object](/struct/main.md#MessageSender) | -              | 发送者信息     |

### 私聊消息

`message_type` 为 `private` 的上报将会有以下有效通用数据

| 字段名          | 数据类型    | 可能的值          | 说明                                                            |
|--------------|---------|---------------|---------------------------------------------------------------|
| time         | int64   | -             | 事件发生的时间戳                                                      |
| self_id      | int64   | -             | 收到事件的机器人 QQ 号                                                 |
| post_type    | string  | message       | 上报类型                                                          |
| message_type | string  | private       | 消息类型                                                          |
| sub_type     | string  | friend, group | 消息子类型，如果是好友则是 friend，如果是群临时会话则是 group，如果是在群中自身发送则是 group_self |
| message_id   | int32   | -             | 消息 ID                                                         |
| user_id      | int64   | -             | 发送者 QQ 号                                                      |
| message      | message | -             | 消息内容                                                          |
| raw_message  | string  | -             | 原始消息内容                                                        |
| font         | int32   | -             | 字体                                                            |
| sender       | object  | -             | 发送人信息                                                         |

> 需要注意的是，sender中的各字段是尽最大努力提供的，不保证每个字段都一定存在，也不保证存在的字段都是完全正确的（缓存可能过期）。

### 快速回复

| 字段名         | 数据类型    | 说明                                          | 默认情况 |
|-------------|---------|---------------------------------------------|------|
| reply       | message | 要回复的内容                                      | 不回复  |
| auto_escape | boolean | 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效 | 不转义  |

### 群聊消息

| 字段名          | 数据类型    | 可能的值                      | 说明                                                               |
|--------------|---------|---------------------------|------------------------------------------------------------------|
| time         | int64   | -                         | 事件发生的时间戳                                                         |
| self_id      | int64   | -                         | 收到事件的机器人 QQ 号                                                    |
| post_type    | string  | message                   | 上报类型                                                             |
| message_type | string  | group                     | 消息类型                                                             |
| sub_type     | string  | normal、~anonymous~、notice | 消息子类型，正常消息是 normal，匿名消息是 anonymous，系统提示（如「管理员已禁止群内匿名聊天」）是 notice |
| message_id   | int32   | -                         | 消息 ID                                                            |
| user_id      | int64   | -                         | 发送者 QQ 号                                                         |
| message      | message | -                         | 消息内容                                                             |
| raw_message  | string  | -                         | 原始消息内容                                                           |
| font         | int32   | -                         | 字体                                                               |
| sender       | object  | -                         | 发送人信息                                                            |
| group_id     | int64   | -                         | 群号                                                               |
| ~anonymous~  | object  | -                         | 匿名信息，如果不是匿名消息则为 null                                             |

### 快速操作

| 字段名          | 数据类型    | 说明                                          | 默认情况   |
|--------------|---------|---------------------------------------------|--------|
| reply        | message | 要回复的内容                                      | 不回复    |
| auto_escape  | boolean | 消息内容是否作为纯文本发送（即不解析 CQ 码），只在 reply 字段是字符串时有效 | 不转义    |
| at_sender    | boolean | 是否要在回复开头 at 发送者（自动添加），发送者是匿名用户时无效           | at 发送者 |
| delete       | boolean | 撤回该条消息                                      | 不撤回    |
| kick         | boolean | 把发送者踢出群组（需要登录号权限足够），不拒绝此人后续加群请求，发送者是匿名用户时无效 | 不踢出    |
| ban          | boolean | 禁言该消息发送者，对匿名用户也有效                           | 不禁言    |
| ban_duration | number  | 若要执行禁言操作时的禁言时长                              | 30 分钟  |

# 事件上报

## 私聊消息撤回

| 字段名         | 数据类型   | 可能的值          | 说明            |
|-------------|--------|---------------|---------------|
| time        | int64  | -             | 事件发生的时间戳      |
| self_id     | int64  | -             | 收到事件的机器人 QQ 号 |
| post_type   | string | notice        | 上报类型          |
| notice_type | string | friend_recall | 通知类型          |
| user_id     | int64  | -             | 好友 QQ 号       |
 | operator_id | int64  | -             | 撤回操作者 QQ 号    |
| message_id  | int64  | -             | 被撤回的消息 ID     |

## 群聊消息撤回

| 字段名         | 数据类型   | 可能的值         | 说明            |
|-------------|--------|--------------|---------------|
| time        | int64  | -            | 事件发生的时间戳      |
| self_id     | int64  | -            | 收到事件的机器人 QQ 号 |
| post_type   | string | notice       | 上报类型          |
| notice_type | string | group_recall | 通知类型          |
| group_id    | int64  | -            | 群号            |
| user_id     | int64  | -            | 消息发送者 QQ 号    |
| operator_id | int64  | -            | 操作者 QQ 号      |
| message_id  | int64  | -            | 被撤回的消息 ID     |

## 群成员增加

| 字段名         | 数据类型   | 可能的值           | 说明                         |
|-------------|--------|----------------|----------------------------|
| time        | int64  | -              | 事件发生的时间戳                   |
| self_id     | int64  | -              | 收到事件的机器人 QQ 号              |
| post_type   | string | notice         | 上报类型                       |
| notice_type | string | group_increase | 通知类型                       |
| sub_type    | string | approve、invite | 事件子类型，分别表示管理员已同意入群、管理员邀请入群 |
| group_id    | int64  | -              | 群号                         |
| operator_id | int64  | -              | 操作者 QQ 号                   |
| user_id     | int64  | -              | 加入者 QQ 号                   |

## 群成员减少

| 字段名         | 数据类型   | 可能的值               | 说明                               |
|-------------|--------|--------------------|----------------------------------|
| time        | int64  | -                  | 事件发生的时间戳                         |
| self_id     | int64  | -                  | 收到事件的机器人 QQ 号                    |
| post_type   | string | notice             | 上报类型                             |
| notice_type | string | group_decrease     | 通知类型                             |
| sub_type    | string | leave、kick、kick_me | 事件子类型，分别表示主动退群、成员被踢、登录号被踢        |
| group_id    | int64  | -                  | 群号                               |
| operator_id | int64  | -                  | 操作者 QQ 号 (如果是主动退群，则和 user_id 相同) |
| user_id     | int64  | -                  | 离开者 QQ 号                         |

## 群管理员变动

| 字段名         | 数据类型   | 可能的值        | 说明                 |
|-------------|--------|-------------|--------------------|
| time        | int64  | -           | 事件发生的时间戳           |
| self_id     | int64  | -           | 收到事件的机器人 QQ 号      |
| post_type   | string | notice      | 上报类型               |
| notice_type | string | group_admin | 通知类型               |
| sub_type    | string | set、unset   | 事件子类型，分别表示设置和取消管理员 |
| group_id    | int64  | -           | 群号                 |
| user_id     | int64  | -           | 管理员 QQ 号           |

## 群文件上传

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值         | 说明            |
|-------------|--------|--------------|---------------|
| time        | int64  | -            | 事件发生的时间戳      |
| self_id     | int64  | -            | 收到事件的机器人 QQ 号 |
| post_type   | string | notice       | 上报类型          |
| notice_type | string | group_upload | 通知类型          |
| group_id    | int64  | -            | 群号            |
| user_id     | int64  | -            | 发送者 QQ 号      |
| file        | object | -            | 文件信息          |

文件信息对象（file）包含以下字段：

| 字段名   | 数据类型   | 说明                |
|-------|--------|-------------------|
| id    | string | 文件 ID             |
| name  | string | 文件名               |
| size  | int64  | 文件大小（字节数）         |
| busid | int64  | busid（目前不清楚有什么作用） |

## 群禁言

| 字段名         | 数据类型   | 可能的值         | 说明                  |
|-------------|--------|--------------|---------------------|
| time        | int64  | -            | 事件发生的时间戳            |
| self_id     | int64  | -            | 收到事件的机器人 QQ 号       |
| post_type   | string | notice       | 上报类型                |
| notice_type | string | group_ban    | 通知类型                |
| sub_type    | string | ban、lift_ban | 事件子类型，分别表示禁言和解除禁言   |
| group_id    | int64  | -            | 群号                  |
| operator_id | int64  | -            | 操作者 QQ 号            |
| user_id     | int64  | -            | 被禁言 QQ 号（为全员禁言时为0）  |
| duration    | int64  | -            | 禁言时长，单位秒（为全员禁言时为-1） |

## 好友添加

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值       | 说明            |
|-------------|--------|------------|---------------|
| time        | int64  | -          | 事件发生的时间戳      |
| self_id     | int64  | -          | 收到事件的机器人 QQ 号 |
| post_type   | string | notice     | 上报类型          |
| notice_type | string | friend_add | 通知类型          |
| user_id     | int64  | -          | 新添加好友 QQ 号    |

## 好友头像戳一戳

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值   | 说明       |
|-------------|--------|--------|----------|
| time        | int64  | -      | 事件发生的时间戳 |
| self_id     | int64  | -      | BOT QQ 号 |
| post_type   | string | notice | 上报类型     |
| notice_type | string | notify | 消息类型     |
| sub_type    | string | poke   | 提示类型     |
| sender_id   | int64  | -      | 发送者 QQ 号 |
| user_id     | int64  | -      | 发送者 QQ 号 |
| target_id   | int64  | -      | 被戳者 QQ 号 |

## 群头像戳一戳

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值   | 说明       |
|-------------|--------|--------|----------|
| time        | int64  | -      | 时间       |
| self_id     | int64  | -      | BOT QQ 号 |
| post_type   | string | notice | 上报类型     |
| notice_type | string | notify | 消息类型     |
| sub_type    | string | poke   | 提示类型     |
| group_id    | int64  | -      | 群号       |
| user_id     | int64  | -      | 发送者 QQ 号 |
| target_id   | int64  | -      | 被戳者 QQ 号 |

### 群红包运气王提示

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值       | 说明          |
|-------------|--------|------------|-------------|
| time        | int64  | -          | 时间          |
| self_id     | int64  | -          | BOT QQ 号    |
| post_type   | string | notice     | 上报类型        |
| notice_type | string | notify     | 消息类型        |
| sub_type    | string | lucky_king | 提示类型        |
| group_id    | int64  | -          | 群号          |
| user_id     | int64  | -          | 红包发送者的 QQ 号 |
| target_id   | int64  | -          | 运气王的 QQ 号   |

## 群成员荣誉变更

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值                                     | 说明       |
|-------------|--------|------------------------------------------|----------|
| time        | int64  | -                                        | 时间       |
| self_id     | int64  | -                                        | BOT QQ 号 |
| post_type   | string | notice                                   | 上报类型     |
| notice_type | string | notify                                   | 消息类型     |
| sub_type    | string | honor                                    | 提示类型     |
| group_id    | int64  | -                                        | 群号       |
| user_id     | int64  | -                                        | 成员 QQ 号  |
| honor_type  | string | talkative:龙王 performer:群聊之火 emotion:快乐源泉 | 荣誉类型     |


## 群成员头衔更新

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值   | 说明           |
|-------------|--------|--------|--------------|
| time        | int64  | -      | 时间           |
| self_id     | int64  | -      | BOT QQ 号     |
| post_type   | string | notice | 上报类型         |
| notice_type | string | notify | 消息类型         |
| sub_type    | string | title  | 提示类型         |
| group_id    | int64  | -      | 群号           |
| user_id     | int64  | -      | 变更头衔的用户 QQ 号 |
| title       | string | -      | 获得的新头衔       |

## 群成员名片更新

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值       | 说明       |
|-------------|--------|------------|----------|
| time        | int64  | -          | 时间       |
| self_id     | int64  | -          | BOT QQ 号 |
| post_type   | string | notice     | 上报类型     |
| notice_type | string | group_card | 消息类型     |
| group_id    | int64  | -          | 群号       |
| user_id     | int64  | -          | 成员 QQ 号  |
| card_new    | string | -          | 新名片      |
| card_old    | string | -          | 旧名片      |

## 收到离线文件

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值         | 说明       |
|-------------|--------|--------------|----------|
| time        | int64  | -            | 时间       |
| self_id     | int64  | -            | BOT QQ 号 |
| post_type   | string | notice       | 上报类型     |
| notice_type | string | offline_file | 消息类型     |
| user_id     | int64  | -            | 发送者 QQ 号 |
| file        | object | -            | 文件数据     |

文件数据 (file object)：

| 字段名  | 数据类型   | 可能的值 | 说明   |
|------|--------|------|------|
| name | string | -    | 文件名  |
| size | int64  | -    | 文件大小 |
| url  | string | -    | 下载链接 |

## 其他客户端在线状态变更

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型    | 可能的值          | 说明     |
|-------------|---------|---------------|--------|
| post_type   | string  | notice        | 上报类型   |
| notice_type | string  | client_status | 消息类型   |
| client      | Device* | -             | 客户端信息  |
| online      | bool    | -             | 当前是否在线 |

## 精华消息变更

!> Shamrock目前没有实现，如有需要提交issue！

| 字段名         | 数据类型   | 可能的值        | 说明               |
|-------------|--------|-------------|------------------|
| time        | int64  | -           | 时间               |
| self_id     | int64  | BOT QQ 号    | BOT QQ 号         |
| post_type   | string | notice      | 上报类型             |
| notice_type | string | essence     | 消息类型             |
| sub_type    | string | add, delete | 添加为add，移出为delete |
| group_id    | int64  | -           | 群号               |
| sender_id   | int64  | 消息发送者ID     | 消息发送者ID          |
| operator_id | int64  | 操作者ID       | 操作者ID            |
| message_id  | int32  | 消息ID        | 消息ID             |

# 请求事件上报

## 加好友请求

!> Shamrock目前没有实现，如有需要提交issue！

**事件数据**

| 字段名          | 数据类型   | 可能的值    | 说明                       |
|--------------|--------|---------|--------------------------|
| time         | int64  | -       | 事件发生的时间戳                 |
| self_id      | int64  | -       | 收到事件的机器人 QQ 号            |
| post_type    | string | request | 上报类型                     |
| request_type | string | friend  | 请求类型                     |
| user_id      | int64  | -       | 发送请求的 QQ 号               |
| comment      | string | -       | 验证信息                     |
| flag         | string | -       | 请求 flag，在处理请求的 API 中需要传入 |

**快速操作**

| 字段名     | 数据类型    | 说明                | 默认情况 |
|---------|---------|-------------------|------|
| approve | boolean | 是否同意请求            | 不处理  |
| remark  | string  | 添加后的好友备注（仅在同意时有效） | 无备注  |

## 加群请求／邀请

!> Shamrock目前没有实现，如有需要提交issue！

**事件数据**

| 字段名          | 数据类型   | 可能的值        | 说明                       |
|--------------|--------|-------------|--------------------------|
| time         | int64  | -           | 事件发生的时间戳                 |
| self_id      | int64  | -           | 收到事件的机器人 QQ 号            |
| post_type    | string | request     | 上报类型                     |
| request_type | string | group       | 请求类型                     |
| sub_type     | string | add, invite | 请求子类型，分别表示加群请求和邀请登录号入群   |
| group_id     | int64  | -           | 群号                       |
| user_id      | int64  | -           | 发送请求的 QQ 号               |
| comment      | string | -           | 验证信息                     |
| flag         | string | -           | 请求 flag，在处理请求的 API 中需要传入 |

**快速操作**

| 字段名     | 数据类型    | 说明            | 默认情况 |
|---------|---------|---------------|------|
| approve | boolean | 是否同意请求/邀请     | 不处理  |
| reason  | string  | 拒绝理由（仅在拒绝时有效） | 无理由  |

