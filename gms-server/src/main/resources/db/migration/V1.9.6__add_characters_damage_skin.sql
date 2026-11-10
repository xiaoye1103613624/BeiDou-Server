-- ============================================================
-- 伤害皮肤：每角色可选伤害数字外观（客户端读取 Effect/DamageSkin.img 资源）。
-- 业务背景：攻击者造成伤害时，服务端在 DAMAGE_MONSTER(0xF6) 包尾部下发其
--           皮肤 id；登录时通过专用包 DAMAGE_SKIN(0xF7) 下发本人皮肤 id。
--           客户端 DamageNumber 按 id 切换字形（0=默认 BasicEff，资源缺失回退）。
-- 影响：characters 表新增 damage_skin 列；Character / CharactersDO 同步存取；
--       PacketCreator.damageMonster 追加 skinId；新增 PacketCreator.damageSkin。
-- 幂等：Flyway 按版本顺序执行一次；本脚本仅新增列，不改动既有数据。
-- 备注：列值 0 表示默认皮肤；非 0 为 DamageSkin.img 下对应子节点 id。
-- ============================================================

ALTER TABLE `characters`
    ADD COLUMN `damage_skin` INT(11) NOT NULL DEFAULT 0 COMMENT '玩家选择的伤害皮肤ID（0=默认BasicEff）';
