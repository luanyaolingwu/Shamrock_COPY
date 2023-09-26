# 消息格式

## CQ码
CQ 码是指 CQ 中特殊消息类型的文本格式, 这是它的基本语法:

```cqcode
[CQ:类型,参数=值,参数=值]
```

在 QQ 中, 一个消息由多个部分构成, 例如一段文本, 一个图片, at 某人的一个部分. CQ 中定义了与这些消息相符的 CQ 码, 以方便用户使用.

例如, 下面是由一个 at 部分和一个文本部分构成的合法 CQ 消息串

```cqcode
[CQ:at,qq=114514]早上好啊
```

例如qq号为114514的人昵称为"小明", 那么上述消息串在QQ中的渲染是这样的:

```text
@小明 早上好啊
```

注意, CQ 码中不应该有多余的空格, 请不要在任何逗号后或前添加空格, 它会被识别为参数或参数值的一部分.

## 转义
CQ 码由字符 [ 起始, 以 ] 结束, 并且以 , 分割各个参数, 如果你的 CQ 码中, 参数值包括了这些字符, 那么它们应该被使用 HTML 特殊字符的编码方式进行转义.

| 字符  | 对应实体转义序列 |
|-----|----------|
| &   | 	&amp;   |
| [	  | &#91;    |
| ]	  | &#93;    |
|  ,	 | &#44;    |

## 消息段

消息段指的是新一代消息格式，采用json作为基础格式，其模板如下

```json5
{
  "type": "消息类型",
  "data": {
    // 消息数据/参数
    "xxx": 123
  }
}
```

消息段段解析具有规范性，且无需转义更容易理解。

# 消息支持

## QQ表情

消息段: 

```json5
{
  "type": "face",
  "data": {
    "id": 123
  }
}
```

CQ码: 

```cqcode
[CQ:face,id=123]
```

参数: 

| 参数名 | 类型  | 收   | 发   | 必填  | 说明   |
|-----|-----|-----|-----|-----|------|
| id  | int | ✓   | ✓   | 是   | 表情ID |

> 表情ID可以从ID表查询，[ID表](https://github.com/richardchien/coolq-http-api/wiki/%E8%A1%A8%E6%83%85-CQ-%E7%A0%81-ID-%E8%A1%A8)。

## 天气

消息段: 

```json5
{
  "type": "weather",
  "data": {
    "city": "北京"
  }
}
```

或者

```json5
{
  "type": "weather",
  "data": {
    "code": 110000
  }
}
```

CQ码: 

```cqcode
[CQ:weather,city=北京]
```

或者

```cqcode
[CQ:weather,code=110000]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明   |
|------|--------|-----|-----|-----|------|
| city | string |     | ✓   | 否   | 城市名  |
| code | int    |     | ✓   | 否   | 城市代码 |

## 语音

消息段: 

```json5
{
  "type": "record",
  "data": {
    "file": "file:///C:/Users/xxx/xxx/xxx.amr",
    "magic": "1"
  }
}
```

CQ码: 

```cqcode
[CQ:record,file=file:///C:/Users/xxx/xxx/xxx.amr,magic=0]
```

参数:

| 参数名   | 类型     | 收   | 发   | 必填  | 说明                                                                                     |
|-------|--------|-----|-----|-----|----------------------------------------------------------------------------------------|
| file  | string |     | ✓   | 是   | 语音文件路径，可以使用`base64://`提供,也可以使用`http[s]://xxx`指定资源，也可以使用`file:///path/to/xxx.amr`指定绝对路径 |
| url   | string | ✓   | ✓   | 否   | 语音文件下载地址，收发都可以提供                                                                       |
| magic | int    |     | ✓   | 否   | 是否是魔法语音，0/1，收到的语音消息中会有此参数，发送时可忽略此参数，不填默认为0。                                            |

!> 发送语音需要手动安装`AudioLibrary套件`！

## 短视频

消息段: 

```json5
{
  "type": "video",
  "data": {
    "file": "file:///C:/Users/xxx/xxx/xxx.mp4"
  }
}
```

CQ码: 

```cqcode
[CQ:video,file=file:///C:/Users/xxx/xxx/xxx.mp4]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明                                                                                     |
|------|--------|-----|-----|-----|----------------------------------------------------------------------------------------|
| file | string |     | ✓   | 是   | 视频文件路径，可以使用`base64://`提供,也可以使用`http[s]://xxx`指定资源，也可以使用`file:///path/to/xxx.mp4`指定绝对路径 |

## 艾特某人

消息段: 

```json5
{
  "type": "at",
  "data": {
    "qq": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:at,qq=123456789]
```

参数:

| 参数名 | 类型  | 收   | 发   | 必填  | 说明                       |
|-----|-----|-----|-----|-----|--------------------------|
| qq  | int | ✓   | ✓   | 是   | 被@的QQ，当qq为`all`时，为艾特全体成员 |

!> 将在未来实现, `[CQ:at,qq=online]` 和 `[CQ:at,qq=admin]`。

## ~猜拳魔法表情~

!> 在NTQQ中已经废弃⚠️！

消息段: 

```json5
{
  "type": "rps",
  "data": {
  }
}
```

CQ码: 

```cqcode
[CQ:rps]
```

## ~掷骰子魔法表情~

!> 在NTQQ中已经废弃⚠️！

消息段: 

```json5
{
  "type": "dice",
  "data": {
  }
}
```

CQ码: 

```cqcode
[CQ:dice]
```

## ~窗口抖动~

!> 在NTQQ中已经废弃⚠️！Shamrock没有该消息段支持。

消息段: 

```json5
{
  "type": "shake",
  "data": {
  }
}
```

CQ码: 

```cqcode
[CQ:shake]
```

## ~匿名发消息~

!> 在NTQQ中已经废弃⚠️！Shamrock没有该消息段支持。

消息段: 

```json5
{
  "type": "anonymous",
  "data": {
  }
}
```

CQ码: 

```cqcode
[CQ:anonymous]
```

## 链接分享

消息段: 

```json5
{
  "type": "share",
  "data": {
    "url": "http://baidu.com",
    "title": "百度"
  }
}
```

CQ码: 

```cqcode
[CQ:share,url=http://baidu.com,title=百度]
```

参数:

| 参数名     | 类型     | 收   | 发   | 必填  | 说明                |
|---------|--------|-----|-----|-----|-------------------|
| url     | string | ✓   | ✓   | 是   | 分享链接，即点击卡片后跳转的链接。 |
| title   | string | ✓   | ✓   | 是   | 分享标题，显示在卡片的标题内。   |
| content | string | ✓   | ✓   | 否   | 分享内容，显示在卡片的内容内。   |
| image   | string | ✓   | ✓   | 否   | 分享图片链接，显示在卡片的图片内。 |
| file    | string | ✓   | ✓   | 否   | 分享图片url           |


## 推荐好友/群

消息段: 

```json5
{
  "type": "contact",
  "data": {
    "type": "qq",
    "id": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:contact,type=qq,id=123456789]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明                    |
|------|--------|-----|-----|-----|-----------------------|
| type | string | ✓   | ✓   | 是   | 推荐类型，目前支持`qq`和`group` |
| id   | int    | ✓   | ✓   | 是   | 推荐的QQ号或群号             |

## 位置

消息段: 

```json5
{
  "type": "location",
  "data": {
    "lat": 123.456,
    "lon": 123.456
  }
}
```

CQ码: 

```cqcode
[CQ:location,lat=123.456,lon=123.456]
```

参数:

| 参数名     | 类型     | 收   | 发   | 必填  | 说明   |
|---------|--------|-----|-----|-----|------|
| lat     | float  | ✓   | ✓   | 是   | 纬度   |
| lon     | float  | ✓   | ✓   | 是   | 经度   |
| content | string | √   |     | 是   | 地址描述 |
| title   | string | √   |     | 是   | 地址   |

## 音乐分享

消息段: 

```json5
{
  "type": "music",
  "data": {
    "type": "qq",
    "id": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:music,type=qq,id=123456789]
```

参数:

| 参数名     | 类型     | 收   | 发   | 必填  | 说明                        |
|---------|--------|-----|-----|-----|---------------------------|
| type    | string | √   | √   | 是   | music类型，目前支持`qq`和`163`    |
| id      | int    | √   | √   | 是   | 推荐的QQ音乐songid或网易云音乐songid |

## 音乐自定义分享

消息段: 

```json5
{
  "type": "music",
  "data": {
    "type": "custom",
    "url": "http://music.163.com/song/media/outer/url?id=123456789.mp3",
    "audio": "http://music.163.com/song/media/outer/url?id=123456789.mp3",
    "title": "此时此刻",
    "singer": "许巍", // 可选
    "image": "http://p1.music.126.net/xxx.jpg" // 可选
  }
}
```

CQ码: 

```cqcode
[CQ:music,type=custom,url=http://music.163.com/song/media/outer/url?id=123456789.mp3,audio=http://music.163.com/song/media/outer/url?id=123456789.mp3,title=此时此刻,singer=许巍,image=http://p1.music.126.net/xxx.jpg]
```

参数:

| 参数名    | 类型     | 收   | 发   | 必填  | 说明                |
|--------|--------|-----|-----|-----|-------------------|
| type   | string |     | ✓   | 是   | 自定义分享请输入`custom`  |
| url    | string |     | ✓   | 是   | 分享链接，即点击卡片后跳转的链接。 |
| audio  | string |     | ✓   | 是   | 音频链接，音乐的音频链接。     |
| title  | string |     | ✓   | 是   | 音乐的标题。            |
| singer | string |     | ✓   | 否   | 音乐的歌手。            |
| image  | string |     | ✓   | 否   | 音乐的封面图片链接。        |

## 图片

消息段: 

```json5
{
  "type": "image",
  "data": {
    "file": "file:///C:/Users/xxx/xxx/xxx.jpg"
  }
}
```

CQ码: 

```cqcode
[CQ:image,file=file:///C:/Users/xxx/xxx/xxx.jpg]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明                                                                                     |
|------|--------|-----|-----|-----|----------------------------------------------------------------------------------------|
| file | string | ✓   | ✓   | 是   | 图片文件路径，可以使用`base64://`提供,也可以使用`http[s]://xxx`指定资源，也可以使用`file:///path/to/xxx.jpg`指定绝对路径 |
| url  | string | ✓   | ✓   | 否   | 图片文件下载地址，收发都可以提供                                                                       |

!> `QQ秀图` | `闪照` 在NTQQ已经移除！

发送时，file 参数支持：

- 绝对路径，例如 file:///C:\\Users\Alice\Pictures\1.png，格式使用 file URI
- 网络 URL，例如 https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png
- Base64 编码，例如 base64://iVBORw0KGgoAAAANSUhEUgAAABQAAAAVCAIAAADJt1n/AAAAKElEQVQ4EWPk5+RmIBcwkasRpG9UM4mhNxpgowFGMARGEwnBIEJVAAAdBgBNAZf+QAAAAABJRU5ErkJggg==

> 注意
> 
> 图片最大不能超过30MB 
>
> PNG格式不会被压缩, JPG可能不会二次压缩, GIF非动图转成PNG
> 
> GIF动图原样发送(总帧数最大300张, 超过无法发出, 无论循不循环)

## 回复

消息段: 

```json5
{
  "type": "reply",
  "data": {
    "id": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:reply,id=123456789]
```

参数:

| 参数名 | 类型  | 收   | 发   | 必填  | 说明      |
|-----|-----|-----|-----|-----|---------|
| id  | int | ✓   | ✓   | 是   | 回复的消息ID |

## 戳一戳

> Shamrock 戳一戳定义和Go-CQHttp不同！

消息段: 

```json5
{
  "type": "poke",
  "data": {
    "type": 126,
    "id": 2003,
    "strength": 1 // 可选，力度
  }
}
```

CQ码: 

```cqcode
[CQ:poke,type=126,id=2003,strength=1]
```

参数:

| 参数名      | 类型  | 收   | 发   | 必填  | 说明                 |
|----------|-----|-----|-----|-----|--------------------|
| type     | int | ✓   | ✓   | 是   | 戳一戳类型              |
| id       | int | ✓   | ✓   | 是   | 戳一戳ID              |
| strength | int | ✓   | ✓   | 否   | 戳一戳力度，范围1-5，不填默认为1 |

> 来自 Mirai的Poke消息解析，[解析](https://github.com/mamoe/mirai/blob/f5eefae7ecee84d18a66afce3f89b89fe1584b78/mirai-core/src/commonMain/kotlin/net.mamoe.mirai/message/data/HummerMessage.kt#L49)

## 双击头像

消息段: 

```json5
{
  "type": "touch",
  "data": {
    "id": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:touch,id=123456789]
```

参数:

| 参数名 | 类型  | 收   | 发   | 必填  | 说明      |
|-----|-----|-----|-----|-----|---------|
| id  | int | ✓   | ✓   | 是   | 被戳一戳目标  |

## ~礼物~

!> Shamrock没有该消息段支持。

消息段: 

```json5
{
  "type": "gift",
  "data": {
    "qq": 111111111,
    "id": 123456789
  }
}
```

CQ码: 

```cqcode
[CQ:gift,qq=111111111,id=123456789]
```

参数:

| 参数名 | 类型  | 收   | 发   | 必填  | 说明      |
|-----|-----|-----|-----|-----|---------|
| qq  | int | ✓   | ✓   | 是   | 被赠送者QQ号 |
| id  | int | ✓   | ✓   | 是   | 礼物ID    |

## ~合并转发~

!> Shamrock没有该消息段支持。

消息段: 

```json5
{
  "type": "forward",
  "data": {
    "id": "123456789"
  }
}
```

CQ码: 

```cqcode
[CQ:forward,id=123456789]
```

参数:

| 参数名 | 类型     | 收   | 发   | 必填  | 说明                                               |
|-----|--------|-----|-----|-----|--------------------------------------------------|
| id  | string | ✓   | ✓   | 是   | 被合并转发的资源ID，需要通过 `/get_forward_msg`` API获取转发的具体内容 |

## ~合并转发消息节点~

!> Shamrock没有该消息段支持， 如果需要请提交issue！

该操作请查看API `/send_group_forward_msg`中的定义，因为NTQQ上传消息改动大，请注意接口请求格式有所变动！

## ~XML 消息~

!> Shamrock没有该消息段支持， 如果需要请提交issue！

## JSON 消息

消息段: 

```json5
{
  "type": "json",
  "data": {
    "data": "xxx"
  }
}
```

CQ码: 

```cqcode
[CQ:json,data=xxx]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明   |
|------|--------|-----|-----|-----|------|
| data | string | ✓   | ✓   | 是   | JSON |

## ~文本转语音~

!> Shamrock没有该消息段支持， 如果需要请提交issue！

消息段: 

```json5
{
  "type": "tts",
  "data": {
    "text": "xxx"
  }
}
```

CQ码: 

```cqcode
[CQ:tts,text=xxx]
```

参数:

| 参数名  | 类型     | 收   | 发   | 必填  | 说明   |
|------|--------|-----|-----|-----|------|
| text | string |     | ✓   | 是   | 文本   |


# 红包模块

!> 高危模块，如有需要提交issue！