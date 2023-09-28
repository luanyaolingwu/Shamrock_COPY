# API

请求一个 API 时, 包含了: API 路由名称, 以及 API 所需参数，允许使用POST｜GET。

## 请求与响应

### *使用 HTTP GET*

使用 HTTP GET 请求 API 时, 你需要将参数放在 URL 中, 例如:

| 名称     | 说明                                                                                             |
|--------|------------------------------------------------------------------------------------------------|
| 请求 URL | /路由名称?参数名=参数值&参数名=参数值......                                                                    |
| 补充	    | 使用 GET 虽然简单, 但是你无法传入复杂的数据结构, 所以一些需要嵌套数据的 API 是无法通过 HTTP GET 来调用的, 例如 send_group_forward_msg 接口 |

### *使用 HTTP POST*

| 名称     | 说明                                                              |
|--------|-----------------------------------------------------------------|
| 请求 URL | /路由名称                                                           |
| 请求体    | 请求体可以使用 JSON 也可以使用 Form 表单, 需要注意的是, 请求的 `Content-Type`是一定要设置准确的 |
| 补充     | 同样, 在 POST 中, 如果要使用复杂的 API, 那么也需要使用 JSON 的格式, 表单格式是不支持数据嵌套的     |

### *HTTP POST JSON 格式*
```json
{
    "参数名": "参数值",
    "参数名2": "参数值"
}
```

### *HTTP POST 表单格式*

```urlencoded
param1=value&param2=value
```

### *使用 WebSocket*

| 名称     | 说明                                                                                                          |
|--------|-------------------------------------------------------------------------------------------------------------|
| 请求 URL | 这个其实说的是 websocket 建立连接时的 URL, 你可以连接 / 路径, 也可以连接 /api 路径, 他们的区别是 / 除了用来发送 api 和响应 api, 还提供上报功能               |
| 请求体    | 一个 JSON 数据, 其中包含了请求 API 的路由名称, 以及参数                                                                         |
| 补充     | 在调用 api 时, 你还可以传入 "echo" 字段, 然后响应数据中也会有一个值相同的 "echo" 字段, 可以使用这个方式来甄别 "这个响应是哪个请求发出的", 你可以为每一个请求都使用一个唯一标识符来甄别 |

*WebSocket JSON 格式*

```json
{
    "action": "路由名称, 例如 'send_group_msg'",
    "params": {
        "参数名": "参数值",
        "参数名2": "参数值"
    },
    "echo": "'回声', 如果指定了 echo 字段, 那么响应包也会同时包含一个 echo 字段, 它们会有相同的值"
}
```

## 响应说明

使用 HTTP 调用 API 的时候, HTTP 的响应状态码:

| 值   | 说明                                        |
|-----|-------------------------------------------|
| 403 | access token 不符合                          |
| 404 | 路由 不存在                                    |
| 200 | 除上述情况外所有情况 (具体 API 调用是否成功, 需要看 API 的 响应数据 |

API 的响应是一个 JSON 数据, 如下:

```json
{
    "status": "状态, 表示 API 是否调用成功, 如果成功, 则是 OK, 其他的在下面会说明",
    "retcode": 0,
    "msg": "错误消息, 仅在 API 调用失败时有该字段",
    "wording": "对错误的详细解释(中文), 仅在 API 调用失败时有该字段",
    "data": {
        "响应数据名": "数据值",
        "响应数据名2": "数据值"
    },
    "echo": "'回声', 如果请求时指定了 echo, 那么响应也会包含 echo"
}
```

其中, `status` 字段:

| 值      | 说明       |
|--------|----------|
| ok     | api 调用成功 |
| failed | api 调用失败 |

# 账号相关

## 获取登录号信息

### 路由名称

`POST｜GET /get_account_info`

响应数据

| 名称      | 类型       | 解释   |
|---------|----------|------|
| uin     | `int64`  | QQ   |
| isLogin | `bool`   | 是否登陆 |
| nick    | `string` | 昵称   |

## 设置QQ个人资料

该接口用于设置 QQ 用户的个人资料信息。

### 路由名称

`POST｜GET /set_qq_profile`

### 参数

| 参数名           | 类型     | 必需  | 说明                |
|---------------|--------|-----|-------------------|
| nickname      | string | 是   | 昵称                |
| company       | string | 是   | 公司                |
| email         | string | 是   | 邮箱                |
| college       | string | 是   | 大学                |
| personal_note | string | 是   | 个人备注              |
| age           | int    | 否   | 年龄                |
| birthday      | string | 否   | 生日（格式：YYYY-MM-DD） |

### 响应

该接口将返回处理结果。

## 获取在线机型

该接口用于获取可设置的在线机型的其他类型。

### 路由名称

`POST｜GET /_get_model_show`

### 参数

| 参数名   | 类型     | 必需  | 说明  |
|-------|--------|-----|-----|
| model | string | 是   | 机型  |

### 响应

```json
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "variants": [
      {
        "model_show": "iPhone11,1 (黑色)",
        "need_pay": true
      },
      {
        "model_show": "iPhone11,1 (白色)",
        "need_pay": true
      },
      {
        "model_show": "iPhone11,1 (银色)",
        "need_pay": true
      },
      {
        "model_show": "iPhone11,1 (灰色)",
        "need_pay": true
      },
      {
        "model_show": "iPhone11,1 (金色)",
        "need_pay": true
      },
      {
        "model_show": "iPhone11,1",
        "need_pay": false
      }
    ]
  }
}
```

## 设置在线机型

该接口用于设置在线机型。

### 路由名称

`POST｜GET /_set_model_show`

### 参数

| 参数名   | 类型     | 必需  | 说明  |
|-------|--------|-----|-----|
| model | string | 是   | 机型  |

### 响应

该接口将返回处理结果。

## ~获取当前账号在线客户端列表~

该接口用于获取当前账号在线客户端列表。

### 路由名称

`POST｜GET /get_online_clients`

### 响应

```json5
// 废弃，待实现
```

# 资料卡

## 获取陌生人信息

该接口用于获取陌生人信息。

### 路由名称

`POST｜GET /get_stranger_info`

### 参数

| 参数名     | 类型    | 必需  | 说明  |
|---------|-------|-----|-----|
| user_id | int64 | 是   | QQ号 |

### 响应

```json
{
  "user_id": "12345678",
  "nickname": "伏秋洛~",
  "age": "18",
  "sex": "female"
}
```

!> 该api可能返回了例子响应中没有的参数，请不要作为参考使用。

## 获取好友列表

该接口用于获取好友列表。

> 该方法无输入参数，除了`refresh`参数决断是否刷新数据，可能不是立即生效。

### 路由名称

`POST｜GET /get_friend_list`

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": [
    {
      "user_id": "12345678",
      "user_name": "伏秋洛~",
      "user_displayname": "伏秋洛~",
      "user_remark": "伏秋洛~",
      "age": 18,
      "gender": 2,
      "group_id": 0,
      "platform": "MOBILE_ANDROID",
      "term_type": 65799
    }
  ]
}
```

## ~获取单向好友列表~

!> 当前不支持该操作，如有需要，请提交issue。

# 用户操作

## ~删除好友~

该接口用于删除好友。

!> 该接口尚未实现，如有需要请提交issue。

### 路由名称

`POST｜GET /delete_friend`

## ~删除单向好友~

该接口用于删除单向好友。

!> 该接口尚未实现，如有需要请提交issue。

## 获取群信息

该接口用于获取群信息。

### 路由名称

`POST｜GET /get_group_info`

### 参数

| 参数名      | 类型    | 必需  | 说明  |
|----------|-------|-----|-----|
| group_id | int64 | 是   | 群号  |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "group_id": "12345678",
    "group_name": "PRIVATE",
    "group_remark": "",
    "group_uin": "645830205",
    "admins": [
      1919810,
      114514,
      11111111
    ],
    "class_text": null,
    "is_frozen": false,
    "max_member": 200,
    "member_num": 4,
    "member_count": 4,
    "max_member_count": 200
  }
}
```

!> 该api可能返回了例子响应中没有的参数，请不要作为参考使用。

## 获取群列表

该接口用于获取群列表。

### 路由名称

`POST｜GET /get_group_list`

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": [
    {
      "group_id": "28000000",
      "group_name": "TXHook",
      "group_remark": "",
      "group_uin": "1234567",
      "admins": [
        12345678,
        22222222
      ],
      "class_text": null,
      "is_frozen": false,
      "max_member": 200,
      "member_num": 54,
      "member_count": 54,
      "max_member_count": 200
    }
  ]
}
```

## 获取群成员信息

该接口用于获取群成员信息。

### 路由名称

`POST｜GET /get_group_member_info`

### 参数

| 参数名      | 类型    | 必需  | 说明  |
|----------|-------|-----|-----|
| group_id | int64 | 是   | 群号  |
| user_id  | int64 | 是   | QQ号 |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "user_id": "123456678",
    "group_id": "11111111111",
    "user_name": "伏秋洛~",
    "sex": "female",
    "title": "",
    "title_expire_time": 0,
    "nickname": "伏秋洛~",
    "user_displayname": "群昵称",
    "distance": 100,
    "honor": [],
    "join_time": 1597173804,
    "last_active_time": 1694287344,
    "last_sent_time": 1694287344,
    "unique_name": "",
    "area": "",
    "level": 10315,
    "role": "owner",
    "unfriendly": false,
    "card_changeable": false
  }
}
```

## 获取群成员列表

该接口用于获取群成员列表。

### 路由名称

`POST｜GET /get_group_member_list`

### 参数

| 参数名      | 类型    | 必需  | 说明  |
|----------|-------|-----|-----|
| group_id | int64 | 是   | 群号  |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": [
    {
      "user_id": "22222222",
      "group_id": "11111111",
      "user_name": "伏秋洛~",
      "sex": "female",
      "title": "",
      "title_expire_time": 0,
      "nickname": "伏秋洛~",
      "user_displayname": "二比",
      "distance": 100,
      "honor": [],
      "join_time": 1597173804,
      "last_active_time": 1694287344,
      "last_sent_time": 1694287344,
      "unique_name": "",
      "area": "",
      "level": 10315,
      "role": "owner",
      "unfriendly": false,
      "card_changeable": false
    }
  ]
}
```

## 获取群荣誉信息

该接口用于获取群荣誉信息。

### 路由名称

`POST｜GET /get_group_honor_info`

### 参数

| 参数名      | 类型    | 必需  | 说明  |
|----------|-------|-----|-----|
| group_id | int64 | 是   | 群号  |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "group_id": "702991377",
    "current_talkative": null,
    "talkative_list": [],
    "performer_list": [
      {
        "user_id": "203411690",
        "nickname": "洛洛喵",
        "avatar": "https://qzonestyle.gtimg.cn/aoi/sola/20200217190136_92JEGFKC5k.png",
        "day_count": 0,
        "id": 2,
        "description": "群聊之火"
      }
    ],
    "legend_list": [],
    "strong_newbie_list": [],
    "emotion_list": [
      {
        "user_id": "1619180855",
        "nickname": "没有人比我更懂女装",
        "avatar": "https://qzonestyle.gtimg.cn/aoi/sola/20200213150434_3tDmsJExCP.png",
        "day_count": 0,
        "id": 6,
        "description": "快乐源泉"
      }
    ]
  }
}
```

## ~获取群系统消息~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取精华消息列表~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群 @全体成员 剩余次数~

!> 当前不支持该操作，如有需要，请提交issue。

## 群戳一戳

该接口用于群戳一戳。

### 路由名称

`POST｜GET /group_touch`

### 参数

| 参数名      | 类型    | 必需  | 说明  |
|----------|-------|-----|-----|
| group_id | int64 | 是   | 群号  |
| user_id  | int64 | 是   | QQ号 |

# 消息操作

## 发送私聊消息

该接口用于发送私聊消息。

### 路由名称

`POST｜GET /send_private_msg`

!> WebSocket不支持该action！

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| user_id | int64  | 是 | QQ号 |
| message | string | 是 | 消息内容 |
| auto_escape | bool | 否 | 是否不作为cq解析 |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "message_id": 2102466734,
    "time": 1695580017.687
  }
}
```

## 发送群聊消息

该接口用于发送群聊消息。

### 路由名称

`POST｜GET /send_group_msg`

!> WebSocket不支持该action！

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64  | 是 | 群号 |
| message | string | 是 | 消息内容 |
| auto_escape | bool | 否 | 是否不作为cq解析 |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "message_id": 2102466734,
    "time": 1695580017.687
  }
}
```

## ~发送讨论组消息~

!> 当前不支持该操作，如有需要，请提交issue。

## 发送消息

该接口用于发送消息。

### 路由名称

`POST｜GET /send_msg`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| message_type | string | 是 | 消息类型, 支持 private、group , 分别对应私聊、群组, 如不传入, 则根据传入的 *_id 参数判断 |
| user_id | int64  | 是/否 | QQ号 |
| group_id | int64  | 是/否 | 群号 |
| discuss_id | int64  | 是/否 | 讨论组号 |
| message | string | 是 | 消息内容 |
| auto_escape | bool | 否 | 是否不作为cq解析 |

!> 当前消息并不支持讨论组！

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "message_id": 2102466734,
    "time": 1695580017.687
  }
}
```

## 获取消息

该接口用于获取消息。

### 路由名称

`POST｜GET /get_msg`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| message_id | int32 | 是 | 消息ID |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "time": 1695580015,
    "message_type": "group",
    "message_id": 2102466734,
    "real_id": 17365,
    "sender": {
      "user_id": 12345678,
      "nickname": "伏秋洛",
      "sex": "unknown",
      "age": 0,
      "uid": "u_3xxxxxxxxx"
    },
    "message": [
      {
        "type": "text",
        "data": {
          "text": "111"
        }
      }
    ],
    "group_id": "11451419"
  }
}
```

!> 该api可能返回了例子响应中没有的参数，请不要作为参考使用。

## 撤回消息

该接口用于撤回消息。

### 路由名称

`POST｜GET /delete_msg`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| message_id | int32 | 是 | 消息ID |


## ~标记消息已读~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取合并转发内容~

!> 当前不支持该操作，如有需要，请提交issue。

## ~发送合并转发 ( 群聊 )~

!> 当前不支持该操作，如有需要，请提交issue。

## ~发送合并转发 ( 私聊 )~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群消息历史记录~

!> 当前不支持该操作，如有需要，请提交issue。

# 资源操作

## ~获取图片~

!> 当前不支持该操作，如有需要，请提交issue。

## ~检查是否可以发送图片~

!> 当前不支持该操作，如有需要，请提交issue。

## ~图片 OCR~

!> 当前不支持该操作，如有需要，请提交issue。

## 获取语音

该接口用于获取语音。

### 路由名称

`POST｜GET /get_record`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| file | string | 是 | 文件md5 |
| out_format | string | 是 | 输出格式 |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "file": "/path/to/xxx",
    "url": "http://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
  }
}
```

## ~检查是否可以发送语音~

!> 当前不支持该操作，如有需要，请提交issue。

# 处理

## ~处理加好友请求~

!> 当前不支持该操作，如有需要，请提交issue。

## ~处理加群请求／邀请~

!> 当前不支持该操作，如有需要，请提交issue。

# 群操作

## 设置群名

该接口用于设置群名。

### 路由名称

`POST｜GET /set_group_name`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| group_name | string | 是 | 群名 |

## ~设置群头像~

!> 当前不支持该操作，如有需要，请提交issue。

## 设置群管理员

该接口用于设置群管理员。

### 路由名称

`POST｜GET /set_group_admin`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| user_id | int64 | 是 | QQ号 |
| enable | bool | 是 | 是否设置 |

## ~设置群备注~

!> 当前不支持该操作，如有需要，请提交issue。

## 设置群组专属头衔

该接口用于设置群组专属头衔。

### 路由名称

`POST｜GET /set_group_special_title`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| user_id | int64 | 是 | QQ号 |
| special_title | string | 是 | 头衔 |

!> 该api可能返回了例子响应中没有的参数，请不要作为参考使用。

## 群全员禁言

该接口用于群全员禁言。

### 路由名称

`POST｜GET /set_group_whole_ban`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| enable | bool | 是 | 是否禁言 |

## 群单人禁言

该接口用于群单人禁言。

### 路由名称

`POST｜GET /set_group_ban`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| user_id | int64 | 是 | QQ号 |
| duration | int64 | 是 | 禁言时长 |

> `duration`为0时，解除禁言！

## ~群匿名用户禁言~

!> 当前不支持该操作，如有需要，请提交issue。

## ~设置精华消息~

!> 当前不支持该操作，如有需要，请提交issue。

## ~移出精华消息~

!> 当前不支持该操作，如有需要，请提交issue。

## ~群打卡~

!> 当前不支持该操作，如有需要，请提交issue。

## ~群设置匿名~

!> 当前不支持该操作，如有需要，请提交issue。

## ~发送群公告~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群公告~

!> 当前不支持该操作，如有需要，请提交issue。

## 群组踢人

该接口用于群组踢人。

### 路由名称

`POST｜GET /set_group_kick`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |
| user_id | int64 | 是 | QQ号 |
| reject_add_request | bool | 否 | 拒绝再加群 |

## 退出群组

该接口用于退出群组。

### 路由名称

`POST｜GET /set_group_leave`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| group_id | int64 | 是 | 群号 |

## ~解散群聊~

!> 当前不支持该操作，如有需要，请提交issue。

# 文件系统

## ~上传群文件~

!> 当前不支持该操作，如有需要，请提交issue。

## ~删除群文件~

!> 当前不支持该操作，如有需要，请提交issue。

## ~创建群文件文件夹~

!> 当前不支持该操作，如有需要，请提交issue。

## ~删除群文件文件夹~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群文件系统信息~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群根目录文件列表~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群子目录文件列表~

!> 当前不支持该操作，如有需要，请提交issue。

## ~获取群文件资源链接~

!> 当前不支持该操作，如有需要，请提交issue。

## ~上传私聊文件~

!> 当前不支持该操作，如有需要，请提交issue。

# 天气

## 获取某个地区的adcode

该接口用于获取某个地区的adcode。

### 路由名称

`POST｜GET /get_weather_city_code`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| city | string | 是 | 城市名 |

### 响应

```json5
{
  "status": "ok",
  "retcode": 0,
  "data": {
    "adcode": "110000",
    "city": "北京市",
    "province": "北京市",
  }
}
```

## 获取天气

该接口用于获取天气。

### 路由名称

`POST｜GET /get_weather`

### 参数

| 参数名 | 类型 | 必需 | 说明 |
|--------------|--------|--|-----|
| code | int32 | 是/否 | adcode |
| city | string | 是/否 | 城市名称，如果不填写指定code，请输入名称。 |

### 响应

```json5
{"status":"ok","retcode":0,"data":{"weekStore":{"weatherInfo":{"all_astro":[],"lifeindex_forecast_list":[],"weekly_astro":[],"ret":0,"weather_info":{"temper":"16","air_humidity":"98","wind_power":"微风","wind_direct":"西南风","weather":"多云","pubtime":"01:40","updatetime":1695664205,"weather_type":"多云","weather_type_id":"202","type_id_new":2,"concrete_type":2,"sunrise":"06:05","sunset":"18:06"},"air_info":null,"forecast_list":{"weatherForecast":[{"day_weather":"阴","night_weather":"多云","day_temper":"22","night_temper":"15","day_wind_direct":"西南风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:05","sunset_time":"18:06","pubtime":"202309251800","day":0,"day_weather_type":"阴天","night_weather_type":"多云","day_weather_type_id":"203","night_weather_type_id":"202","day_type_id_new":3,"day_concrete_type":3,"night_type_id_new":2,"night_concrete_type":2,"pm":"82","wind_power_desc":""},{"day_weather":"多云","night_weather":"晴","day_temper":"24","night_temper":"16","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:06","sunset_time":"18:04","pubtime":"202309251800","day":1,"day_weather_type":"多云","night_weather_type":"晴","day_weather_type_id":"202","night_weather_type_id":"201","day_type_id_new":2,"day_concrete_type":2,"night_type_id_new":1,"night_concrete_type":1,"pm":"146","wind_power_desc":""},{"day_weather":"晴","night_weather":"晴","day_temper":"28","night_temper":"14","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:07","sunset_time":"18:03","pubtime":"202309251800","day":2,"day_weather_type":"晴","night_weather_type":"晴","day_weather_type_id":"201","night_weather_type_id":"201","day_type_id_new":1,"day_concrete_type":1,"night_type_id_new":1,"night_concrete_type":1,"pm":"26","wind_power_desc":""},{"day_weather":"晴","night_weather":"多云","day_temper":"26","night_temper":"15","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:08","sunset_time":"18:01","pubtime":"202309251800","day":3,"day_weather_type":"晴","night_weather_type":"多云","day_weather_type_id":"201","night_weather_type_id":"202","day_type_id_new":1,"day_concrete_type":1,"night_type_id_new":2,"night_concrete_type":2,"pm":"36","wind_power_desc":""},{"day_weather":"多云","night_weather":"晴","day_temper":"26","night_temper":"14","day_wind_direct":"西北风","night_wind_direct":"变向风","day_wind_power":"3-4级","night_wind_power":"微风","sunrise_time":"06:09","sunset_time":"17:59","pubtime":"202309251800","day":4,"day_weather_type":"多云","night_weather_type":"晴","day_weather_type_id":"202","night_weather_type_id":"201","day_type_id_new":2,"day_concrete_type":2,"night_type_id_new":1,"night_concrete_type":1,"pm":"15","wind_power_desc":""},{"day_weather":"晴","night_weather":"晴","day_temper":"25","night_temper":"12","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:10","sunset_time":"17:58","pubtime":"202309251800","day":5,"day_weather_type":"晴","night_weather_type":"晴","day_weather_type_id":"201","night_weather_type_id":"201","day_type_id_new":1,"day_concrete_type":1,"night_type_id_new":1,"night_concrete_type":1,"pm":"","wind_power_desc":""},{"day_weather":"晴","night_weather":"晴","day_temper":"25","night_temper":"12","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:11","sunset_time":"17:56","pubtime":"202309251800","day":6,"day_weather_type":"晴","night_weather_type":"晴","day_weather_type_id":"201","night_weather_type_id":"201","day_type_id_new":1,"day_concrete_type":1,"night_type_id_new":1,"night_concrete_type":1,"pm":"","wind_power_desc":""}],"updatetime":1695664205,"tomorrowPrompt":"","weeklyPrompt":""},"forecast":{"day_weather":"多云","night_weather":"晴","day_temper":"24","night_temper":"16","day_wind_direct":"变向风","night_wind_direct":"变向风","day_wind_power":"微风","night_wind_power":"微风","sunrise_time":"06:06","sunset_time":"18:04","pubtime":"202309251800","day":1,"day_weather_type":"多云","night_weather_type":"晴","day_weather_type_id":"202","night_weather_type_id":"201","day_type_id_new":2,"day_concrete_type":2,"night_type_id_new":1,"night_concrete_type":1,"pm":"146","wind_power_desc":""},"hourinfo_list":null,"almanac":"","warning_list":{"lst_warning":[],"last_proc_time":0},"astro":null,"city":"北京","area":"","adcode":110000,"area_id":101010100,"en_name":"Beijing","update_time":1695664205,"tips_list":null,"lifeindex_list":null,"current_time":1695664515,"user_weekly_astro":null,"weekly_summary":null},"qrcode":"data:image/png;base64,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","poster":"","share":{"data":{"app":"com.tencent.weather.share","config":{"autosize":0,"ctime":1695664515,"forward":0,"round":0,"token":"49441099828d62f0ded8112849bce839"},"desc":"","meta":{"share":{"adcode":110000,"air_info":null,"area":"","city":"北京","current_time":1695664515,"forecast_list":{"tomorrowPrompt":"","updatetime":1695664205,"weatherForecast":[{"day":0,"day_concrete_type":3,"day_temper":"22","day_type_id_new":3,"day_weather":"阴","day_weather_type":"阴天","day_weather_type_id":"203","day_wind_direct":"西南风","day_wind_power":"微风","night_concrete_type":2,"night_temper":"15","night_type_id_new":2,"night_weather":"多云","night_weather_type":"多云","night_weather_type_id":"202","night_wind_direct":"变向风","night_wind_power":"微风","pm":"82","pubtime":"202309251800","sunrise_time":"06:05","sunset_time":"18:06","wind_power_desc":""},{"day":1,"day_concrete_type":2,"day_temper":"24","day_type_id_new":2,"day_weather":"多云","day_weather_type":"多云","day_weather_type_id":"202","day_wind_direct":"变向风","day_wind_power":"微风","night_concrete_type":1,"night_temper":"16","night_type_id_new":1,"night_weather":"晴","night_weather_type":"晴","night_weather_type_id":"201","night_wind_direct":"变向风","night_wind_power":"微风","pm":"146","pubtime":"202309251800","sunrise_time":"06:06","sunset_time":"18:04","wind_power_desc":""},{"day":2,"day_concrete_type":1,"day_temper":"28","day_type_id_new":1,"day_weather":"晴","day_weather_type":"晴","day_weather_type_id":"201","day_wind_direct":"变向风","day_wind_power":"微风","night_concrete_type":1,"night_temper":"14","night_type_id_new":1,"night_weather":"晴","night_weather_type":"晴","night_weather_type_id":"201","night_wind_direct":"变向风","night_wind_power":"微风","pm":"26","pubtime":"202309251800","sunrise_time":"06:07","sunset_time":"18:03","wind_power_desc":""},{"day":3,"day_concrete_type":1,"day_temper":"26","day_type_id_new":1,"day_weather":"晴","day_weather_type":"晴","day_weather_type_id":"201","day_wind_direct":"变向风","day_wind_power":"微风","night_concrete_type":2,"night_temper":"15","night_type_id_new":2,"night_weather":"多云","night_weather_type":"多云","night_weather_type_id":"202","night_wind_direct":"变向风","night_wind_power":"微风","pm":"36","pubtime":"202309251800","sunrise_time":"06:08","sunset_time":"18:01","wind_power_desc":""},{"day":4,"day_concrete_type":2,"day_temper":"26","day_type_id_new":2,"day_weather":"多云","day_weather_type":"多云","day_weather_type_id":"202","day_wind_direct":"西北风","day_wind_power":"3-4级","night_concrete_type":1,"night_temper":"14","night_type_id_new":1,"night_weather":"晴","night_weather_type":"晴","night_weather_type_id":"201","night_wind_direct":"变向风","night_wind_power":"微风","pm":"15","pubtime":"202309251800","sunrise_time":"06:09","sunset_time":"17:59","wind_power_desc":""},{"day":5,"day_concrete_type":1,"day_temper":"25","day_type_id_new":1,"day_weather":"晴","day_weather_type":"晴","day_weather_type_id":"201","day_wind_direct":"变向风","day_wind_power":"微风","night_concrete_type":1,"night_temper":"12","night_type_id_new":1,"night_weather":"晴","night_weather_type":"晴","night_weather_type_id":"201","night_wind_direct":"变向风","night_wind_power":"微风","pm":"","pubtime":"202309251800","sunrise_time":"06:10","sunset_time":"17:58","wind_power_desc":""},{"day":6,"day_concrete_type":1,"day_temper":"25","day_type_id_new":1,"day_weather":"晴","day_weather_type":"晴","day_weather_type_id":"201","day_wind_direct":"变向风","day_wind_power":"微风","night_concrete_type":1,"night_temper":"12","night_type_id_new":1,"night_weather":"晴","night_weather_type":"晴","night_weather_type_id":"201","night_wind_direct":"变向风","night_wind_power":"微风","pm":"","pubtime":"202309251800","sunrise_time":"06:11","sunset_time":"17:56","wind_power_desc":""}],"weeklyPrompt":""},"update_time":1695664205,"weather_info":{"air_humidity":"98","concrete_type":2,"pubtime":"01:40","sunrise":"06:05","sunset":"18:06","temper":"16","type_id_new":2,"updatetime":1695664205,"weather":"多云","weather_type":"多云","weather_type_id":"202","wind_direct":"西南风","wind_power":"微风"}}},"prompt":"[分享]北京 多云 16°","ver":"1.0.0.1","view":"share"},"code":0}}}}
```




