# Shamrock

☘ 基于Xposed实现的Onebot11/12标准即时通讯Bot框架

> 本项目仅提供学习与交流用途，请在24小时内删除，本项目目的是研究Xposed和Lsposed框架的使用，以及Epic框架开发相关知识，如有违反法律，请联系删除。

# 权限声明

未在此处声明的权限，请警惕是否为正版Shamrock。

- 联网权限: 为了让Shamrock进程使用HTTP API进行一些操作。
- [Hook**系统框架**](https://github.com/fuqiuluo/Shamrock/wiki/perm_hook_android): 为了保证息屏状态下仍能维持服务后台运行。

# 语音解码器支持

语音转换器已经模块化，如果不加入指定的模块，则无法发送mp3/flac/wav/ogg等格式的语音。

为了完整支持，您需要下载[AudioLibrary](https://raw.githubusercontent.com/fuqiuluo/Shamrock/master/AudioLibrary.zip)并将里面的`so文件`全部解压到`目标应用数据目录/Tencent/Shamrock/lib`文件夹。

**目标应用数据目录**一般在`/storage/emulated/0/Android/data/com.tencent.mobileqq`

如果没有`lib`文件夹，则创建一个，`lib`文件夹内只能有格式为`*.so`文件，不能有目录存在，否则无法正常加载。

# 开发进程

☘ 努力实现中：[已实现以及不与实现的接口都在这里](https://github.com/fuqiuluo/Shamrock/wiki)

# 部署

## 云手机 / LSPatch

> 在云服务器部署无法换取最大性能，如果您的云服务器没有KVM虚拟支持甚至无法启动对应的虚拟机，建议采用云手机的方式进行部署。 
> 支持Shamrock的云手机必须采用Android 9以上版本，云手机低廉的价格与稳定性极具性价比。

- 在云手机LSPatch部署时请先安装正版应用登录成功后。
- 将`/data/0/user/包名/files`下所有文件复制一份。
- 卸载官方版，安装LSPatch版本，使用root将数据拷贝回去。
- 启动Shamrock及应用配置后即可正常使用。

## 模拟器部署

可以参考LSPatch部署方法，或者在支持Xposed框架的模拟器上直接使用。


