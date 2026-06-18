# Win11最新系统补丁导致的修饰键BUG

> 来源：https://moguwuyu.com/d/285
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

这阵子在冒险岛里长按修饰键（Ctrl、Shift、Alt）都无法持续操作，也有其他人反映了相同的问题，一对比我和他的系统版本完全一致。
经过排查，最后我这边是卸载了4月份最新的安全补丁KB5083769问题就好了。
如果你有同样的问题，可以先去
https://w3c.github.io/uievents/tools/key-event-viewer.html
测一下自己的修饰键是否正常。正常情况下按住按键会不断刷出 keydown 的信息，而打了 KB5083769 补丁的系统，按住修饰键只会输出一条 keydown 信息