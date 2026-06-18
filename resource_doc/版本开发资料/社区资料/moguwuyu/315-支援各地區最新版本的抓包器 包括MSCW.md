# 支援各地區最新版本的抓包器 包括MSCW

> 来源：https://moguwuyu.com/d/315
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

https://gitlab.com/chuichui/MapleShark2-m1
頻道伺服器中從客戶端發出的包頭有二次加密
需要找到用來加密的 LP_OpcodeEncryption 和 CP_BEGIN_USER 的值
前者看進入頻道後服務端發出的第一個長度比較大的包就是它
後者在InitOpcodeCrypt函數中根據地區對應的KEY字串 在IDA往回翻就能看到了
都找到之後 照此格式放進這個文件

---

**#2楼**

大佬！！