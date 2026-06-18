# GMSv83 刪除角色跳過 PIC 檢測

> 来源：https://moguwuyu.com/d/56
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

這是我找到的資料
005F7CA1 setz -> setne
005F7CD4 jz -> jnz short
005F7DCD-005F7DE6 fill with nops
005F7E5F-005F7E6A fill with nops
下面是實現方式，但是測試點了確定會崩潰
Memory::WriteByte(0x005F7CA1 + 1, 0x95);
Memory::WriteByte(0x005F7CD4, 0x75);
Memory::WriteByte(0x005F7CD4 + 1, 0x74);
Memory::FillBytes(0x005F7DCD, 0x90, 26);
Memory::FillBytes(0x005F7E5F, 0x90, 12);
參考資料
https://forum.ragezone.com/threa ... ithout-pic.1191320/
測試的伺服器端是 Cosmic
ENABLE_PIC: false

---

**#2楼**

PIC是什么用途:lol

---

**#3楼**

v83 刪除角色會需要PIC

---

**#4楼**

xiaomai
pic 和 pin 相当于二级密码