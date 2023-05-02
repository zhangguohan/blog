# Centos7的 systemd服务管理

## 一、安装systemctl 命令参数补合

```
 #yum install bash-completion
```

## 二、systemtct服务参数使用

### 2.1列出所有服务类型的选项

```
[root@nginx-lb www]# systemctl --type service  list-unit-files 
UNIT FILE                                     STATE   
arp-ethers.service                            disabled
auditd.service                                enabled 
autovt@.service                               enabled 
blk-availability.service                      disabled
brandbot.service                              static  
chrony-dnssrv@.service                        static  
chrony-wait.service                           disable
```

### 2.2 重启服务

```
[root@nginx-lb www]# systemctl restart vsftpd.service 
[root@nginx-lb www]# systemctl status vsftpd.service 
* vsftpd.service - Vsftpd ftp daemon
   Loaded: loaded (/usr/lib/systemd/system/vsftpd.service; disabled; vendor preset: disabled)
   Active: active (running) since 二 2022-01-11 10:56:55 CST; 11s ago
  Process: 16236 ExecStart=/usr/sbin/vsftpd /etc/vsftpd/vsftpd.conf (code=exited, status=0/SUCCESS)
 Main PID: 16237 (vsftpd)
   CGroup: /system.slice/vsftpd.service
           `-16237 /usr/sbin/vsftpd /etc/vsftpd/vsftpd.conf

1月 11 10:56:55 nginx-lb.com systemd[1]: Starting Vsftpd ftp daemon...
1月 11 10:56:55 nginx-lb.com systemd[1]: Started Vsftpd ftp daemon.
[root@nginx-lb www]# 
```

### 2.3 关于target

- 启动级别的概念
- 理解为目标或组
- target间可以相互关联

#### 2.3.1查看当前的启动级别

```
[root@nginx-lb ~]# systemctl get-default
multi-user.target
[root@nginx-lb ~]# 
```

#### 2.3.2 设轩置当前的启动级别

```
[root@nginx-lb ~]# systemctl  set-default graphical.target 
```

#### 2.3.3 查看当前启动级别运行的服务

```
[root@nginx-lb ~]# systemctl list-dependencies 
default.target
* |-auditd.service
* |-chronyd.service
* |-crond.service
* |-dbus.service
* |-irqbalance.service
* |-mysqld.service
* |-network.service
* |-NetworkManager.service
* |-php-fpm.service
* |-plymouth-quit-wait.service
* |-plymouth-quit.service
* |-postfix.service
```

### 2.4 关于service服务单元

```
查看服务状态： systemctl  status nginx
查重新加服务配置：  systemctl  reload nginx
重启服务：systemctl  restart nginx
启动服务：systemctl  start nginx   
停止服务：systemctl  stop nginx
```

### 2.4.1 自定义服务启动

```
用户软件包安装的单元：/usr/lib/systemd/system

系统管理员安装的单元：  /etc/systemd/system
```

### 2.4.2 测试脚本程序：

```
[root@localhost system]# more /opt/work/test.sh 
 #!/bin/bash
echo $$ > /var/run/tank.pid

while : 
do

    echo "tank "$(date) >>/tmp/tank.log
    sleep 2 

done
```

### 2.4.3 添加自定义服务脚本：

```
[root@localhost system]# 



[root@localhost system]# more tank.service 
[Unit]
Description=print datae
Documentation=http://nginx.org
After=network-online.target remote-fs.target nss-lookup.target
Wants=network-online.target

[Service]
Type=simple
PIDFile=/var/run/tank.pid
ExecStart= /bin/sh    /opt/work/test.sh
ExecStop=/bin/sh -c "/bin/kill -s TERM $(/bin/cat /var/run/tank.pid)"

[Install]
WantedBy=multi-user.target
[root@localhost system]# 


[root@localhost system]#  systemctl daemon-reload 
[root@localhost system]#  systemctl start tank.service 

root      7459  7329  0 04:09 pts/0    00:00:00 grep --color=auto tank
[root@localhost system]# ps -ef |grep test
root      7443     1  0 04:09 ?        00:00:00 /bin/sh /opt/work/test.sh
root      7473  7329  0 04:09 pts/0    00:00:00 grep --color=auto test
[root@localhost system]# 
```

### 2.5 关于Systemd 定时任务器

制定新定时任务 = service服务单元+ timer定时单元、

分类：单调定时器、实时定时器

#### 2.5.1 新建一个实时定时器调用tank.service服务

```
[root@localhost system]# more tank.timer 
[Unit]
Description=timer
Documentation=http://nginx.org

[Timer]
Unit=tank.service
OnCalendar=2022-01-12 04:45:00 

[Install]
WantedBy=multi-user.target
[root@localhost system]# 
```

#### 2.5.2 查看定时任务执行情况

```
[root@localhost system]# systemctl  status tank.timer 
● tank.timer - timer
   Loaded: loaded (/usr/lib/systemd/system/tank.timer; disabled; vendor preset: disabled)
   Active: active (running) since Wed 2022-01-12 04:36:05 EST; 24min ago
     Docs: http://nginx.org

Jan 12 04:36:05 localhost.localdomain systemd[1]: Stopped timer.
Jan 12 04:36:05 localhost.localdomain systemd[1]: Stopping timer.
Jan 12 04:36:05 localhost.localdomain systemd[1]: Started timer.
[root@localhost system]# 
```

### 2.6 关于Systemd 日志管理Journal

journal: systemd自己提供的日志工具和服务

配置文件：/etc/systemd/journald.conf

查看日志命令 ： journalctl

查看最后面20行 ： journalctl -n 20

查看err级别日志： journalctl -p err

```
[root@localhost ad2f10d8b2894a89b64acdcc7f9169a0]# journalctl  -p err
-- Logs begin at Tue 2022-01-11 01:44:31 EST, end at Wed 2022-01-12 05:19:25 EST. --
Jan 11 01:44:35 localhost.localdomain kernel: [drm:vmw_host_log [vmwgfx]] *ERROR* Failed to send host log
Jan 11 01:44:35 localhost.localdomain kernel: [drm:vmw_host_log [vmwgfx]] *ERROR* Failed to send host log
Jan 12 05:19:25 localhost.localdomain systemd[1]: Failed to start The Apache HTTP Server.
```

查看指定单元的日志：journalctl -u

```
[root@localhost ad2f10d8b2894a89b64acdcc7f9169a0]# journalctl  -u tank.service
-- Logs begin at Tue 2022-01-11 01:44:31 EST, end at Wed 2022-01-12 05:19:25 EST. --
Jan 11 08:27:15 localhost.localdomain systemd[1]: Started print datae.
Jan 11 08:28:14 localhost.localdomain systemd[1]: Stopping print datae...
Jan 11 08:28:14 localhost.localdomain systemd[1]: Stopped print datae.
Jan 12 04:09:11 localhost.localdomain systemd[1]: Started print datae.
Jan 12 04:10:01 localhost.localdomain systemd[1]: Stopping print datae...
Jan 12 04:10:01 localhost.localdomain systemd[1]: Stopped print datae.
Jan 12 04:45:30 localhost.localdomain systemd[1]: Started print datae.
Jan 12 05:02:03 localhost.localdomain systemd[1]: Stopping print datae...
Jan 12 05:02:03 localhost.localdomain systemd[1]: Stopped print datae.
[root@localhost ad2f10d8b2894a89b64acdcc7f9169a0]# 
```