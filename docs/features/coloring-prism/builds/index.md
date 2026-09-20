# 构建索引（2026-09-17）

## 服务端

| 项 | 值 |
| --- | --- |
| 命令 | `D:\software\apache-maven-3.9.16\bin\mvn.cmd -pl gms-server -am -DskipTests package` |
| 结果 | BUILD SUCCESS |
| 产物 | `gms-server/target/BeiDou.jar` |
| 大小 | 118719854 |
| SHA256 | `6D06F5C862AD7F8F65E34FDCDEB5F006A42583E56FDB584355A6E9E9D15140FB` |
| 日志 | `builds/mvn-package.log` |

## 插件

| 项 | 值 |
| --- | --- |
| 命令 | `E:\project\BeiDou-ijl15\build_once.bat`（强制重编 `weapontint.cpp`） |
| 产物 | `BeiDou-ijl15\out\Release\ijl15.dll` |
| 部署 | `E:\MXD\BeiDou-Client_S9\ijl15.dll`（PostBuild xcopy） |
| 大小 | 1993728 |
| SHA256 | `9B37BBF6223B01467B266301BB8C484AD29BA0848837CFBA5976BA4D8E23ABC7` |
| 备注 | ExecaveStub SKIP（stub 非 `53 E8`，布局漂移；与染色无关） |
