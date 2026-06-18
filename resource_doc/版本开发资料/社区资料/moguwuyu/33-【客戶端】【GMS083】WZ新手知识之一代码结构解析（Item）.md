# 【客戶端】【GMS083】WZ新手知识之一代码结构解析（Item）

> 来源：https://moguwuyu.com/d/33
> 站点：蘑菇物语(moguwuyu.com) · Flarum 改端技术论坛

**#1楼**

Item(8位)
代码为8位结构，前面首位为0，后面实际7位数字。Item不為單獨的img。都保存在自己的大類中。
Consume
Special
Cash
Etc
Install
Pet
1.Consume
列表
202 + 2 + XXX   = 恢復或buff類藥水（效果在spec節點）
203 + XXXX       = 回城卷軸
204 + AA + BB  = 強化捲軸
其中
代码登录后可见
205 + XXXX      = 解除異常狀態藥水
206 + XXXX      = 弓箭
207 + XXXX      = 飛鏢
210 + XXXX      = 召喚包
212 + XXXX      = 寵物食品
216 + XXXX      = 新年賀卡
219 + XXXX      = 測謊儀
221 + XXXX      = 變身藥水
224 + XXXX      = 求婚戒指盒
226 + XXXX      = 騎寵疲勞藥水
227 + XXXX      = 抓捕藥水
228 + XXXX      = 能手冊（全職業部分）
229 + XXXX      = 能手冊
231 + XXXX      = 自由市場貓頭鷹
232 + XXXX      = 瞬移石
233 + XXXX      = 子彈
234 + XXXX      = 祝福捲軸
236 + XXXX      = 萬聖節幽靈變身
237 + XXXX      = 戰國版本經驗書
238 + XXXX      = 怪物卡片
243 + XXXX      = 雙擊物品執行腳本
244 + XXXX      = 道具橘子寶寶
245 + XXXX      = 幸運的狩獵
2.Special
900 + XXXX      = 金幣
910 + 1 + XXX  = 商城時裝禮包物品
代码登录后可见
911 + XXXX      = 增加背包
3.Cash
501 + XXXX      = 現金特效
502 + XXXX      = 現金飛鏢
503 + XXXX      = 現金商店，僱傭商人
504 + XXXX      = 瞬移石
505 + XXXX      = 重置卷
506 + XXXX      = 道具取名
507 + XXXX      = 普通喇叭
508 + XXXX      = 告白等
509 + XXXX      = 消息
510 + XXXX      = 音樂盒
511 + XXXX      = 巧克力
512 + XXXX      = BUFF特效
513 + XXXX      = 護身符
514 + XXXX      = 現金個人商店
515 + XXXX      = 會員卡（美容美髮）
516 + XXXX      = 人物表情
517 + XXXX      = 寵物取名
518 + XXXX      = 生命水
519 + XXXX      = 寵物技能
520 + XXXX      = 金幣包
521 + XXXX      = 經驗卡
522 + XXXX      = 百寶券
523 + XXXX      = 搜索器
524 + XXXX      = 寵物食品
525 + XXXX      = 婚禮門票
528 + XXXX      = 臭屁花香特效
529 + XXXX      = 家族留言板
530 + XXXX      = 變身石
533 + XXXX      = 特快使用券
536 + XXXX      = 爆率卡
537 + XXXX      = 黑板
538 + XXXX      = 寶貝龍進化石
539 + XXXX      = 震動喇叭
540 + XXXX      = 改名卡
542 + XXXX      = 優惠券
543 + XXXX      = 角色卡
545 + XXXX      = 包裹商人，高級快樂百寶券
547 + XXXX      = 商人遙控器
549 + XXXX      = 黃金萬能鑰匙
550 + XXXX      = 道具延長，魔法沙漏
551 + XXXX      = 原地復活
552 + XXXX      = 宿命剪刀
555 + XXXX      = 項鏈擴充
557 + XXXX      = 金錘子
559 + XXXX      = 裝備許可證
561 + XXXX      = 捲軸成功率提升卡
4.Etc
400 + XXXX      = ~
5.Install
301 + XXXX      = 椅子
302 + XXXX      = 椅子
304 + XXXX      = 分解機
306 + XXXX      = 星岩
308 + XXXX      = 背包名片夾
309 + XXXX      = 拼圖
310 + XXXX      = 航海
311 + XXXX      = 召喚物（騎士團那個鳥之類的）
360 + XXXX      = 進化系統
370 + XXXX      = 稱號
390 + XXXX      = 字母數字

---

**#2楼**

从0开始学习