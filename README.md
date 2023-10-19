<div align="center">

![][banner]

[![][actions]][actions-link]
[![][releases]][releases-link]
[![][downloads]][releases-link]

![][onebot-11]
![][onebot-12]
[![][license]](LICENSE)

[下载][download-link] | [部署][deploy-link] | [接口][api-link] | [文档][docs-link] | [加群][group-link]

</div>

## 简介

☘ 基于 Xposed 实现 OneBot 标准的 QQ 机器人框架

> 本项目仅提供学习与交流用途，请在24小时内删除。   
> 本项目目的是研究 Xposed 和 LSPosed 框架的使用。 Epic 框架开发相关知识。  
> 如有违反法律，请联系删除。
> 请勿在任何平台宣传，宣扬，转发本项目，请勿恶意修改企业安装包造成相关企业产生损失，如有违背，必将追责到底。

## 兼容|迁移|替代 说明

- 一键移植：本项目基于 go-cqhttp 的文档进行开发实现。
- 平行部署：可多平台部署，未来将会支持 Docker 部署的教程。  
- 强行续命：Shamrock支持提供 QSIGN API 服务。

> 若您追求小而轻便的Bot服务, [Chronocat](https://chronocat.vercel.app/)是您的不二之选，而不是迁移到闭源框架。

## 权限声明

如出现未在此处声明的权限，请警惕 Shamrock 是否被修改/植入恶意代码 

- 网络访问权限: Shamrock 进程需要使用 HTTP API 来进行一些操作
- [Hook 系统框架][hook-system]: 为了保证息屏状态下服务不被杀死，Shamrock 需要 Hook 系统框架
- 后台启动 Activity: 自动唤醒 QQ 时需要使用

## 语音解码

请参考文档中 [语音支持][voice-support] 部分

## 贡献说明

<img src="https://github.com/linxinrao/Shamrock/assets/61898844/b7b1f44a-2ab3-4eae-a955-4f8a7ef696f5" width="160px"> **我可爱吗？欢迎你的到来，这里是一个很大的地方，有着无限可能，主要是有你啦！**

## 开源协议

本项目使用 [GPL-3.0](LICENSE) 协议开放源代码

```text
Shamrock - OneBot standard QQ robot framework based on Xposed implementation
Copyright (C) 2023 Shamrock Team
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```

## 贡献者

[![][contrib-image]][contrib-link]

[banner]: https://socialify.git.ci/linxinrao/Shamrock/image?description=1&forks=1&issues=1&logo=https%3A%2F%2Flinxinrao.github.io%2FShamrock%2Fshamrock.jpg&pattern=Plus&pulls=1&stargazers=1&theme=Auto

[actions]: https://img.shields.io/github/actions/workflow/status/linxinrao/Shamrock/build-apk.yml?style=for-the-badge

[actions-link]: https://github.com/linxinrao/Shamrock/actions/workflows/build-apk.yml

[releases]: https://img.shields.io/github/v/release/linxinrao/Shamrock?style=for-the-badge

[releases-link]: https://github.com/linxinrao/Shamrock/releases

[downloads]: https://img.shields.io/github/downloads/linxinrao/Shamrock/total?style=for-the-badge

[license]: https://img.shields.io/github/license/linxinrao/Shamrock?style=for-the-badge

[onebot-11]: https://img.shields.io/badge/OneBot-11-black?style=for-the-badge

[onebot-12]: https://img.shields.io/badge/OneBot-12-black?style=for-the-badge

[download-link]: https://linxinrao.github.io/Shamrock/guide/getting-started.html#%E4%B8%8B%E8%BD%BD

[deploy-link]: https://linxinrao.github.io/Shamrock/guide/getting-started.html#%E9%83%A8%E7%BD%B2

[api-link]: https://linxinrao.github.io/Shamrock/api

[docs-link]: https://linxinrao.github.io/Shamrock/

[group-link]: https://linxinrao.github.io/Shamrock/group.html

[hook-system]: https://github.com/linxinrao/Shamrock/wiki/perm_hook_android

[voice-support]: https://linxinrao.github.io/Shamrock/advanced/voice.html

[contrib-image]: https://contrib.rocks/image?repo=linxinrao/Shamrock

[contrib-link]: https://github.com/linxinrao/Shamrock/graphs/contributors
