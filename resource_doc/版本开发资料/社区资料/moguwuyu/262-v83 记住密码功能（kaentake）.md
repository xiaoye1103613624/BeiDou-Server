# v83 记住密码功能（kaentake）

> 来源：https://moguwuyu.com/d/262
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

原帖地址
This is my first time diving this deep into reverse engineering, and I likely wouldn't have been able to succeed without the help of LLMs.
I had only used Cheat Engine for simple tasks before, so I assumed this kind of work had to be done in assembly.
Big thanks to teto, the creator of kaentake, for making C++ modding possible.
And I'm still a beginner in reverse engineering, Any feedback or suggestions are always welcome.
描述
修复登录界面保存密码功能
原理
对两个方法进行修改
代码登录后可见
→ 创建登录界面时执行一些方法
代码登录后可见
→ 尝试登录时调用一些方法（点击按钮或者回车键）
登录时密码会被保存到注册表，进入登录界面时自动填入密码。
修改方法
修改 hook.h，添加
代码登录后可见
和
代码登录后可见
修改 CMakeLists.txt 添加
代码登录后可见
创建新文件 rememberpw.cpp
代码登录后可见