
## 场景一

### 1.1 今天将 postgresql版本由于11升级到13，应用无法正常连接，提示：  

```       
Long Message:
不支援 10 验证类型。请核对您已经组态 pg_hba.conf 文件包含客户端的IP位址或网路区段，以及驱动程序所支援的验证架构模式已被支援。

Details:
   Type: org.postgresql.util.PSQLException
   SQL State: 08004
```

### 1.2 pg_hba.conf配置如下：

```
指定授权网段：


host all all 108.88.3.0/24 scram-sha-256


```
 
## 解决方法

 下载使用最新的JDBC包postgresql-42.3.1.jar，应用正常连接。








