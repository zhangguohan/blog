# Oracle监听器—动态注册

# 

- 

注册就是将数据库作为一个服务注册到监听程序。客户端不需要知道数据库名和实例名，只需要知道该数据库对外提供的服务名就可以申请连接到数据库。这个服务名可能与实例名一样，也有可能不一样。

注册分：

1. 静态注册
2. 动态注册

动态注册

没有设置service_names值的情况
动态注册是向监听注册的首选方法。初始化参数local_listener会告知实例应当进行联系的、从而能找到注册实例的监听器的网络地址。

在实例启动时，PMON进程会使用local_listener参数来定位一个监听器，并向其通知实例的instance_name，service_names。

如果该service_names没有设定值，数据库将拼接db_name和db_domain的值来注册自己。如果该service_names设定了值，可以使用完全限定的名称（比如 orcl.oracle.com)或缩写的名称（比如orcl）。

```
SQL> show parameter service_names
 
NAME                                 TYPE        VALUE
------------------------------------ ----------- ------
service_names                        string      


SQL> show parameter db_name;
 
NAME                                 TYPE        VALUE
------------------------------------ ----------- -------
db_name                              string      ora11g
 

SQL> show parameter db_domain;
 
NAME                                 TYPE        VALUE
------------------------------------ ----------- -------
db_domain                            string


LSNRCTL> status
正在连接到 (DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=Tough)(PORT=1521)))
LISTENER 的 STATUS
------------------------
别名                      LISTENER
版本                      TNSLSNR for 32-bit Windows: Version 10.2.0.1.0 - Production
启动日期                  07-4月 -2014 12:58:45
正常运行时间              0 天 0 小时 31 分 53 秒
跟踪级别                  off
安全性                    ON: Local OS Authentication
SNMP                      OFF
监听程序参数文件          c:\oracle\product\10.2.0\db_1\network\admin\listener.ora
监听程序日志文件          c:\oracle\product\10.2.0\db_1\network\log\listener.log
监听端点概要...
  (DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=Tough)(PORT=1521)))
服务摘要..
服务 "ora11g" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "ora11gXDB" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "ora11g_XPT" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "test" 包含 1 个例程。
  例程 "ora11g", 状态 UNKNOWN, 包含此服务的 1 个处理程序...
```

我们没有设置service_names，则数据库拼接db_name和db_domain的值以及默认1521端口来来注册自己——这就是动态注册。

如果没有显式设置service_names和instance_name的值，那么仅当数据库在监听器运行之后启动时，动态注册才会发生；在这种情况下，如果监听器后来发生了重启，动态注册信息将会丢失。显然，最好在所有的数据库启动之前先启动监听器，这样就会避免没有显式设置 service_names和instance_name的值时，若重启监听器带来的动态注册信息丢失的情况。

如果监听器在默认端口1521上运行，则完全不需要配置动态注册。所有实例将自动查看相应端口上本机的监听器，如果找到就进行注册。

但如果监听器不在主机名标识地址上的默认端口运行，则必须通过设置local_listener参数来重新注册来指定监听器的位置。

```
SQL> alter system set local_listener='(ADDRESS = (PROTOCOL = TCP)(HOST = Tough)(PORT = 1530))';
 
System altered

SQL> alter system register;

System altered
```

设置了service_names值的情况

```
SQL> alter system set service_names='a,b';
 
System altered

 

LSNRCTL> status
正在连接到 (DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=Tough)(PORT=1521)))
LISTENER 的 STATUS
------------------------
别名                      LISTENER
版本                      TNSLSNR for 32-bit Windows: Version 10.2.0.1.0 - Production
启动日期                  07-4月 -2014 14:14:12
正常运行时间              0 天 0 小时 13 分 17 秒
跟踪级别                  off
安全性                    ON: Local OS Authentication
SNMP                      OFF
监听程序参数文件          c:\oracle\product\10.2.0\db_1\network\admin\listener.ora
监听程序日志文件          c:\oracle\product\10.2.0\db_1\network\log\listener.log
监听端点概要...
  (DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=Tough)(PORT=1521)))
服务摘要..
服务 "PLSExtProc" 包含 1 个例程。
  例程 "PLSExtProc", 状态 UNKNOWN, 包含此服务的 1 个处理程序...
服务 "a" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "b" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "ora11g" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "ora11gXDB" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "ora11g_XPT" 包含 1 个例程。
  例程 "ora11g", 状态 READY, 包含此服务的 1 个处理程序...
服务 "test" 包含 1 个例程。
  例程 "ora11g", 状态 UNKNOWN, 包含此服务的 1 个处理程序...
命令执行成功
```

标签: none