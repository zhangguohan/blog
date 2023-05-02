# Windows系统的ECS实例Time_Wait连接不释放导致访问外网失败

**问题描述**
Windows系统的ECS实例可以ping通外网，但是无法访问外部的网站或者应用。

**问题原因**
默认Windows Server 2008版本以后，动态端口的数量为16384个（从49152起始，到65536结束），如果服务器对外有大量连接，而根据TCP默认的Time Wait Delay时间为4分钟，这会导致大量连接在断开后处于Time_Wait状态，无法快速释放给其它连接使用，会导致端口耗尽。

**解决方案**
阿里云提醒您：

```
通过管理终端登录Windows系统的ECS实例中，在CMD界面执行如下命令，查看当前动态端口配置。
netsh int ipv4 show dynamicport tcp
执行如下命令，增大动态端口数量，无需重启即可生效。
netsh int ipv4 set dynamicport tcp start=1025 num=60000
如果以上配置不能完全解决，可以通过修改注册表降低Time Wait时间，最低为30秒。打开注册表，定位到HKLM\SYSTEM\CurrentControlSet\Services\Tcpip\Parameters，新增键值TcpTimedWaitDelay，类型REG_DWORD , 设置为十进制30。
```

注意：注册表修改需要对Windows操作系统有一定了解，为了避免注册表误操作带来的操作系统问题或者可能的数据丢失，请您操作注册表前，务必对系统盘和数据盘创建快照以避免可能的数据丢失。并且需要重启生效。