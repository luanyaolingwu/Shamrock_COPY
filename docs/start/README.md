# 开始

> 本项目基于[OneBot标准](https://onebot.dev/)开发，我们保证OneBot标准的所有API都会积极维护。

## 下载Shamrock

你可以选择以下方式获取较新版本的Shamrock：

- [Github Release](https://github.com/linxinrao/Shamrock/releases)
- [Github Action](https://github.com/linxinrao/Shamrock/actions/workflows/build-apk.yml)

!> Shamrock仅支持Arm64的设备，其他设备可尝试QEMU或者转译器（因为qq只有arm64版本）。

## 部署运行

!> 如果您使用了`QRSpeed` / `ShamrockNative` 有关的插件，请确保Shamrock存活，无论什么情况都请确保QQ存活！

在 Shamrock 提示激活成功（登陆成功后才会提示激活成功）, 您可以编写程序, 通过 HTTP 或者 WebSocket 与 Shamrock 进行通讯, 实现 QQ 机器人。

非常建议使用Shamrock的`NativeSDK`进行开发，而不是使用`OneBot标准`，因为里面可能并不包含Shamrock的`扩展API`。

!> 禁止对QQ隐藏Shamrock，这将导致无法运行。首次启动，必须打开Shamrock，否则无法推送配置文件导致失败。

### 在拥有Xposed的设备部署（真机/VMOS）

- 安装QQ（8.9.68 ～ 8.9.80）并登陆。
- 安装Shamrock并激活Xposed后`重新启动QQ`/`重新启动手机`。
- 打开Shamrock + QQ，Shamrock提示已激活。

### 在`无Root`，但支持`LSPatch`环境部署

- 使用LSPatch加载QQ并注入Shamrock模块。
- 同时安装Shamrock本体与被Patch的QQ.apk。
- 在Shamrock配置您的数据即可。

### 在VXP中部署

!> VXP在安卓版本高于12时会闪退。

### 使用太极

!> 正在申请太极适配。

### 在Docker部署

待补全。

### 在Win安卓模拟器中部署

待补全。

## 安装SILK语音引擎

语音转换器已经模块化，如果不加入指定的模块，则无法发送mp3/flac/wav/ogg等格式的语音。

为了完整支持，您需要下载[AudioLibrary](https://github.com/linxinrao/Shamrock/blob/master/AudioLibrary.zip)并将里面的so文件全部解压到目标应用数据目录/Tencent/Shamrock/lib文件夹(/mnt/sdcard/Android/`[packageName]`/Tencent/Shamrock/lib)。

目标应用数据目录一般在/storage/emulated/0/Android/data/com.tencent.mobileqq

如果没有lib文件夹，则创建一个，lib文件夹内只能有格式为*.so文件，不能有目录存在，否则无法正常加载。
