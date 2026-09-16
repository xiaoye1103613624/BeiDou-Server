# 构建索引

## 插件 BeiDou-ijl15

```text
MSBuild E:\project\BeiDou-ijl15\ezorsia.sln /p:Configuration=Release /p:Platform=x86
```

- 产物：`E:\project\BeiDou-ijl15\out\Release\ijl15.dll`
- 部署：PostBuild → `E:\MXD\BeiDou-Client_S9\ijl15.dll`
- 本次：2026-09-16b，SHA256 `DA5CD734A9477FAE5991F63E79549B97233C39B7465CE053B0417A5C6E719317`
- Stamp：`MOUNT_BP20_OFF_ROW_PET_RED_20260916b` / Addon `ADDON_PET2_POUCH_RED_FIX_20260916`

## 服务端

```text
# 需 JDK 17+（本机构建用 IntelliJ JBR 25）
mvn -pl gms-server -DskipTests package
```

- 产物：`gms-server/target/BeiDou.jar`（已于 2026-09-16 用 JBR 25 打通）
- WZ/String 变更随 jar 内资源发布。
