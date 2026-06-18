# 从零开始搭建北斗GMS的开发环境

> 来源：https://moguwuyu.com/d/3
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

请配合
参考视频
查看，本贴主要把视频内的部分代码补全，供直接复制使用
恰好重做了系统，什么环境都还没部署刚好可以用来做演示。
在开始之前你得会科学上网。
这次搭建我们用 docker 来辅助部署。
先来安装一些必要的软件
Git
https://git-scm.com/
Github
https://github.com/apps/desktop
Docker
https://www.docker.com/products/docker-desktop/
注意这里是AMD64不是ARM64 后者是移动端CPU的
服务端
接下来把服务端的源码拷贝下来
https://github.com/BeiDouMS/BeiDou-Server
安装 idea
安装 MySQL docker版
代码登录后可见
-name 容器名称
--restart 自动重启容器 unless-stopped 为除非手动停止，否则一直自动启动包括刚启动 docker
-v 映射目录，把本地目录映射到容器内，这样数据才会保存到本地，格式
代码登录后可见
容器路径已经配好了请勿修改
-e 环境变量 MYSQL_ROOT_PASSWORD=root 这里配置的root账号的密码为root
-p 端口映射，将本地端口和容器内的端口映射
-d 后台允许
mysql:9 使用的镜像
控制台
安装 nodejs
https://nodejs.org/dist/v22.12.0/node-v22.12.0-x64.msi
代码登录后可见

---

**#2楼**

博主，我是按照你的方式进行到了构建项目，但是提示了我缺少这个文件，咋搞的。仓库也是克隆下来的

---

**#3楼**

[已注销]
找到问题了。这里是少了个Node.js没有安装导致的

---

**#4楼**

[已注销]
JDK要21的，我安装是25，这估计也是有问题

---

**#5楼**

[已注销]
最后我的mysql也有问题，之前只安装了工作界面，忘记安装sever。  目前结合了北斗本身的教程，算是运行成功