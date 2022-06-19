## Centos 7 Firewall防火墙配置
- Centos 7 里iptables和firewalld共存
- firewalld的底层调用的iptables,建立在iptables之上
- firewalld在使用上要比iptalbe更人性化
- firewalld使用区域为9种 ： /usr/lib/firewalld/zones


## 1.1 启动firewalld防火墙
```
[root@localhost zones]# systemctl status firewalld
● firewalld.service - firewalld - dynamic firewall daemon
   Loaded: loaded (/usr/lib/systemd/system/firewalld.service; enabled; vendor preset: enabled)
   Active: active (running) since Tue 2022-01-11 01:44:51 EST; 1 day 20h ago
     Docs: man:firewalld(1)
 Main PID: 728 (firewalld)
   CGroup: /system.slice/firewalld.service
           └─728 /usr/bin/python2 -Es /usr/sbin/firewalld --nofork --nopid

Jan 11 01:44:47 localhost.localdomain systemd[1]: Starting firewalld - dynamic firewall daemon...
Jan 11 01:44:51 localhost.localdomain systemd[1]: Started firewalld - dynamic firewall daemon.
Jan 11 01:44:51 localhost.localdomain firewalld[728]: WARNING: AllowZoneDrifting is enabled. This i...ow.
Hint: Some lines were ellipsized, use -l to show in full.
[root@localhost zones]# 


```

## 1.2 查看默认区域权限
```
[root@localhost zones]# firewall-cmd  --list-all
public (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: dhcpv6-client ssh
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost zones]# 


```
## 1.3 查看活跃区域
```
[root@localhost zones]# firewall-cmd  --get-active-zones
public
  interfaces: enp0s3
[root@localhost zones]#

```


### 1.4 修改默认区域

```

[root@localhost zones]# firewall-cmd  --set-default-zone=home
success
[root@localhost zones]# firewall-cmd  --get-active-zones
home
  interfaces: enp0s3
[root@localhost zones]# 


```
## 2 Firewalld中的区域与接口
- 一个网卡只能属于一个zone 不能同时属于多个zone
- 一个zone可以对应多个网卡接口
- 任何配置了一个网络接口的zone,就是一个活动区域
- 从外访问服务咕咕内部如果没有添加规则模式是阻止的
- 从服务器内部访问服务器外部默认是允许的
- 想让规则永久生效加入--permanent选项
- 每次更改规则后需执行 firewall-cmd --reload(动态加载新规则)


### 2.1 改变默认接口
```
[root@localhost zones]# firewall-cmd  --zone=home --change-interface=enp0s3
success
[root@localhost zones]# firewall-cmd  --list-all
home (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: dhcpv6-client mdns samba-client ssh
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost zones]# 


[root@localhost zones]# firewall-cmd  --zone=home --add-interface=enp0s8
success
[root@localhost zones]# firewall-cmd  --list-all
home (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3 enp0s8
  sources: 
  services: dhcpv6-client mdns samba-client ssh
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost zones]# 

```

### 2.2 添加活动区域端口及移除

```
[root@localhost zones]#  firewall-cmd  --zone=public --add-port=80/tcp
success
[root@localhost zones]# firewall-cmd  --list-all
public (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: dhcpv6-client ssh
  ports: 80/tcp
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost zones]#


[root@localhost zones]#  firewall-cmd  --zone=public --remove-port=80/tcp
success
[root@localhost zones]# firewall-cmd  --list-all
public (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: dhcpv6-client ssh
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost zones]# 


永久生效：使用参数 --permanent
[root@localhost zones]#  firewall-cmd  --reload
[root@localhost zones]#  firewall-cmd  --zone=public --add-port=80/tcp --permanent
success
[root@localhost zones]# 


```

### 2.3 添加活动区域服务及移除
```
[root@localhost services]#  firewall-cmd  --zone=public --add-service=nginx --permanent
success
[root@localhost services]# firewall-cmd  --reload
success
[root@localhost services]# firewall-cmd  --list-all
public (active)
  target: default
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: dhcpv6-client nginx ssh
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost services]# 



```

### 2.4 多活动区域场景

- trust区域：设置为允许白名单IP访问
- Drop区域：设置为默认区域



### 2.4.1 设轩默认区域为drop
```
[root@localhost services]# firewall-cmd  --set-default-zone=drop 
success
[root@localhost services]# firewall-cmd --list-all
You're performing an operation over default zone ('drop'),
but your connections/interfaces are in zone 'home,public' (see --get-active-zones)
You most likely need to use --zone=home option.

drop
  target: DROP
  icmp-block-inversion: no
  interfaces: 
  sources: 
  services: 
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 

[root@localhost services]#   firewall-cmd  --zone=drop --change-interface=enp0s3

success
[root@localhost services]# firewall-cmd --list-all
drop (active)
  target: DROP
  icmp-block-inversion: no
  interfaces: enp0s3
  sources: 
  services: 
  ports: 
  protocols: 
  masquerade: no
  forward-ports: 
  source-ports: 
  icmp-blocks: 
  rich rules: 
	
[root@localhost services]# 
```

### 2.4.2 在trusted添加指定IP地址

```

[root@localhost services]#  firewall-cmd --zone=trusted --add-source=108.88.3.131/32  --permanent
success
[root@localhost services]# firewall-cmd --list-all-zone
block
[root@localhost services]# firewall-cmd --reload


```