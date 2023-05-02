# DB2V9-客户端连接出错ERRORCODE=-4214

### 一、连接出错提示

```
[jcc][t4][201][11237][3.57.82] 发生了连接权限故障。原因：安全性机制不受支持。 ERRORCODE=-4214, SQLSTATE=28000
```

### 二、 查看数据库管理器认证

```
[root@db2 ~]# su - db2inst1
[db2inst1@db2 ~]$ db2 get dbm cfg | grep AUTHENTICATION 
 数据库管理器认证                       (AUTHENTICATION) = CLIENT
[db2inst1@db2 ~]$
```

### 三、 修改DB2数据库管理器认证为Server

```
[db2inst1@db2 ~]$ 
[db2inst1@db2 ~]$ db2 update dbm cfg using authentication server 

[db2inst1@db2 ~]$ db2 get dbm cfg | grep AUTHENTICATION 
 数据库管理器认证                       (AUTHENTICATION) = SERVER
```

### 四、重启数据库实例：

```
[db2inst1@db2 ~]$ db2stop
2017-10-23 10:49:36     0   0   SQL1064N  DB2STOP 处理成功。
SQL1064N  DB2STOP 处理成功。
[db2inst1@db2 ~]$ db2start
10/23/2017 10:49:41     0   0   SQL1063N  DB2START 处理成功。
SQL1063N  DB2START 处理成功。
```

标签: none