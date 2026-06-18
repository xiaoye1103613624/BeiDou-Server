# GMS232 微调客户端启动器Release分享

> 来源：https://moguwuyu.com/d/202
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

最近大家开发GMS232的热情高涨，或许会需要频繁开服/游戏调试，我在Claude的辅助下微调了一下剑端开源的客户端启动器分享给大家权当抛砖引玉
目前写的很多东西都是硬编码，没有能开源出去的版本，等稳定下来会尝试把自己在用的
服务端面板
和完整服务端/客户端汉化文件分享给大家（再等我几天！！）
链接：
https://pan.quark.cn/s/190ba5b19397
【YourStory客户端启动器】
基于 Swordie v232 私服的自定义启动器，支持一键启动游戏、服务器状态检测、自定义图片。
系统要求
Windows 10 (21H2+) 或 Windows 11
.NET Framework 4.8.1（Win10/11 一般自带，如果启动报错请安装）
文件放置
将 "YourStory Launcher" 文件夹放到你的游戏根目录（或任意位置）
将 "Swordie.dll" 放到游戏根目录（和 MapleStory.exe 同级）
示例目录结构：
MapleStory V232/
├── MapleStory.exe
├── Swordie.dll          ← 放这里
├── *.wz 文件们
└── YourStory Launcher/  ← 启动器文件夹
├── Swordie Launcher.exe
└── WpfAnimatedGif.dll
━━━━━━━━━━━━━━━━━━━━━━━
🚀 首次使用
右键 "Swordie Launcher.exe" → 以管理员身份运行
必须以管理员身份启动！
否则无法注入 DLL 到游戏进程。
首次启动需要设置：
【Game Path】选择你的 MapleStory.exe 路径
【Server IP】填写服务器 IP 地址（本机测试填 127.0.0.1）
【Server Port】填写登录端口号（默认看服务端配置）
点击 "Check Status" 确认服务器在线
点击 "PLAY" 启动游戏
━━━━━━━━━━━━━━━━━━━━━━━
🎨 额外功能
一键启动游戏：移除了启动器源码的注册登录（对大部分人无用）
自定义背景：可设置 jpg/png/gif/bmp 作为启动器背景
服务器状态：一键检测服务器是否在线
━━━━━━━━━━━━━━━━━━━━━━━
⚠️ 注意事项
杀毒软件误报
启动器使用 DLL 注入技术（CreateRemoteThread），这是私服启动器
的常见做法，但会被杀软标记。请将启动器和 Swordie.dll 添加到
杀毒软件白名单/排除项中。
必须管理员运行
不以管理员权限运行会导致 "DLL injection failed" 或
"CreateProcess failed" 错误。
配置文件位置
设置保存在：%APPDATA%\SwordieLauncher\config.json
如需重置设置，删除该文件即可。
Swordie.dll
Localhost的DLL 必须命名为 "Swordie.dll"，必须放在 MapleStory.exe 同目录下！！