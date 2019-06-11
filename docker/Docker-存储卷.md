### Docker存储卷使用


#### 新建一个docke 管理卷
~~~
[root@docker01 ~]# docker run --name b2 -it -v /data busybox 
/ # ll

/ # ls
bin   data  dev   etc   home  proc  root  sys   tmp   usr   var

~~~

#### 查看宿主机本地共享目录
~~~

[root@docker01 ~]# docker inspect b2
[
    {
        "Id": "3cb2011de3292bf1b3d7d0fce4385faacb18ad49576842cc8b14d2d51b10e2c1",
        "Created": "2019-06-10T09:11:53.319513864Z",
        "Path": "sh",
        "Args": [],
        "State": {
            "Status": "running",
            "Running": true,
            "Paused": false,
            "Restarting": false,

.......................
        "Mounts": [
            {
                "Type": "volume",
                "Name": "81d9526d1f1ad8addd65900b25dd023618dc1f2d3c22a33c57f9a877935cdcda",
                "Source": "/var/lib/docker/volumes/81d9526d1f1ad8addd65900b25dd023618dc1f2d3c22a33c57f9a877935cdcda/_data",
                "Destination": "/data",
                "Driver": "local",
                "Mode": "",
                "RW": true,
                "Propagation": ""
            }

~~~
#### 指定绑定宿主机目录

~~~
[root@docker01 _data]# docker run --name b2 --rm -it -v /data/volumes/d2:/data busybox 
/ # cd /data/
/data # ll
sh: ll: not found
/data # ls
/data # touch kk
/data # exit
[root@docker01 _data]# cd /data/volumes/d2/
[root@docker01 d2]# ll
total 0
-rw-r--r-- 1 root root 0 Jun 10 05:26 kk
[root@docker01 d2]# 



[root@docker01 ~]# docker inspect -f {{.Mounts}} b2
[{bind  /data/volumes/d2 /data   true rprivate}]
[root@docker01 ~]# 
[root@docker01 ~]# docker inspect -f {{.NetworkSettings.IPAddress}} b2
172.17.0.4
[root@docker01 ~]# 

~~~~


#### 指定绑定宿主机目录多个、带复制容器巻配置

~~~

[root@docker01 ~]# docker exec -it b4  /bin/sh
/ # cd /data/

/data # ls
kk
/data # vi kk 
/data # exit
[root@docker01 ~]# docker exec -it b2  /bin/sh
/ # cd /data/
/data # ll
/bin/sh: ll: not found
/data # more kk 
iasdfasf d4

/data # 

~~~



#### 指定镜像启动邦定模板，建立容器

~~~~
[root@docker01 ~]# docker run --name infracon -it -v /data/infracon:/data/web/html busybox
/ # exit

[root@docker01 ~]# docker run --name nginx --network container:infracon --volumes-from infracon -it busybox
/ # if
ifconfig   ifdown     ifenslave  ifplugd    ifup
/ # ifconfig 
eth0      Link encap:Ethernet  HWaddr 02:42:AC:11:00:07  
          inet addr:172.17.0.7  Bcast:172.17.255.255  Mask:255.255.0.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:8 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:656 (656.0 B)  TX bytes:0 (0.0 B)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

/ # exit


~~~

