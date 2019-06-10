#### 正常新建默认容器

~~~

[root@docker01 ~]# docker run --name t1 -it --rm busybox:latest
/ # ifconfig 
eth0      Link encap:Ethernet  HWaddr 02:42:AC:11:00:04  
          inet addr:172.17.0.4  Bcast:172.17.255.255  Mask:255.255.0.0
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

/ #


~~~


#### 指定brige网络容器
~~~
[root@docker01 ~]# docker run --name t1 -it --network bridge  --rm busybox:latest
/ # ifconfig 
eth0      Link encap:Ethernet  HWaddr 02:42:AC:11:00:04  
          inet addr:172.17.0.4  Bcast:172.17.255.255  Mask:255.255.0.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:5 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:426 (426.0 B)  TX bytes:0 (0.0 B)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

/ #

~~~

#### 建立封闭式容器
~~~
[root@docker01 ~]# docker run --name t1 -it --network none  --rm busybox:latest
/ # ifconfig 
lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

/ #  
~~~
#### 指定容器主机名及DNS服务地址
~~~
[root@docker01 ~]# docker run --name t1 -it --network bridge  -h t1.gzgi.com --dns 114.114.114.114 --rm busybox:latest
/ # more /etc/resolv.conf 
nameserver 114.114.114.114
/ # 
~~~
#### 注入容器内Hosts文件
~~~
[root@docker01 ~]# docker run --name t1 -it --network bridge  -h t1.gzgi.com --dns 114.114.114.114 --add-host www.qq.com:192.168.1.1 --rm busybox:latest
/ # more /etc/hosts 
127.0.0.1       localhost
::1     localhost ip6-localhost ip6-loopback
fe00::0 ip6-localnet
ff00::0 ip6-mcastprefix
ff02::1 ip6-allnodes
ff02::2 ip6-allrouters
192.168.1.1     www.qq.com
172.17.0.4      t1.gzgi.com t1
/ #
~~~

#### 开放容器内部网络端口

~~~


[root@docker01 ~]# docker run --name myweb1  -p 80 -d zhangguohan/busybox_http:v0.1-2
0775b47f046d8b90fda0d99101c9ed69ceda49ec6b314f2d30917b1b58500cf6
[root@docker01 ~]# 

[root@docker01 ~]# docker ps -a
CONTAINER ID        IMAGE                             COMMAND                  CREATED             STATUS                    PORTS                   NAMES
0775b47f046d        zhangguohan/busybox_http:v0.1-2   "/bin/httpd -f -h /d…"   51 seconds ago      Up 47 seconds             0.0.0.0:32768->80/tcp   myweb1
9edec9441529        zhangguohan/busybox_http:v0.1-2   "/bin/httpd -f -h /d…"   3 days ago          Up 3 days                                         b3
5b4e0e3f3874        zhangguohan/busybox_http:v0.1-1   "sh"                     3 days ago          Exited (127) 3 days ago                           b2
8e72c4f94fd0        busybox                           "sh"                     3 days ago          Exited (0) 3 days ago                             b1
1e5ca7762447        redis:4-alpine                    "docker-entrypoint.s…"   3 days ago          Up 3 days                 6379/tcp                kvstor1
0c9f67461e33        nginx:1.17-alpine                 "nginx -g 'daemon of…"   3 days ago          Up 3 days                 80/tcp                  web1
50bbdf842e92        debian                            "echo 'Hello world'"     7 days ago          Exited (0) 7 days ago                             recursing_sammet
a9e2bed353ae        debian                            "echo 'Hello world'"     7 days ago          Exited (0) 7 days ago                             determined_bose


[root@docker01 ~]# iptables -t nat -vnL
Chain PREROUTING (policy ACCEPT 129 packets, 10351 bytes)

。。。。。。。。。略

Chain DOCKER (2 references)
 pkts bytes target     prot opt in     out     source               destination         
    0     0 RETURN     all  --  docker0 *       0.0.0.0/0            0.0.0.0/0           
    0     0 DNAT       tcp  --  !docker0 *       0.0.0.0/0            0.0.0.0/0            tcp dpt:32768 to:172.17.0.4:80


[root@docker01 ~]# docker port myweb1 
80/tcp -> 0.0.0.0:32770
[root@docker01 ~]# 

~~~


#### 提定IP地址映射开放对外IP

~~~
[root@docker01 ~]# docker run --name myweb1 --rm  -p 108.88.3.112::80  zhangguohan/busybox_http:v0.1-2


[root@docker01 ~]#  docker port myweb1
80/tcp -> 108.88.3.112:32768
[root@docker01 ~]#
~~~



#### 提定端口映射开放
~~~
[root@docker01 ~]# docker run --name myweb1 --rm  -p 80:80  zhangguohan/busybox_http:v0.1-2

[root@docker01 ~]#  docker port myweb1
80/tcp -> 0.0.0.0:80
[root@docker01 ~]# 
[root@docker01 ~]# docker run --name myweb1 --rm  -p 108.88.3.112:8080:80  zhangguohan/busybox_http:v0.1-2

[root@docker01 ~]#  docker port myweb1
80/tcp -> 108.88.3.112:8080
[root@docker01 ~]# 
~~~

#### 使用联合容器网络

~~~

此种网络方式只共享网络名称空间，只共享lo 内部网络

[root@docker01 ~]# docker run --name b1 --rm  -it busybox

/ # ifconfig 
eth0      Link encap:Ethernet  HWaddr 02:42:AC:11:00:04  
          inet addr:172.17.0.4  Bcast:172.17.255.255  Mask:255.255.0.0
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:7 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:586 (586.0 B)  TX bytes:0 (0.0 B)

lo        Link encap:Local Loopback  
          inet addr:127.0.0.1  Mask:255.0.0.0
          UP LOOPBACK RUNNING  MTU:65536  Metric:1
          RX packets:0 errors:0 dropped:0 overruns:0 frame:0
          TX packets:0 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:0 (0.0 B)  TX bytes:0 (0.0 B)

[root@docker01 ~]# docker run --name b4 --rm  --network container:b1  -it busybox
/ # ifconfig 
eth0      Link encap:Ethernet  HWaddr 02:42:AC:11:00:04  
          inet addr:172.17.0.4  Bcast:172.17.255.255  Mask:255.255.0.0
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

/ # 
~~~

#### 使用宿主机网络名称空间

~~~
[root@docker01 ~]# docker run --name b4 --rm  --network host  -it busybox
/ # ifconfig
docker0   Link encap:Ethernet  HWaddr 02:42:E0:E7:40:56  
          inet addr:172.17.0.1  Bcast:172.17.255.255  Mask:255.255.0.0
          inet6 addr: fe80::42:e0ff:fee7:4056/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:25 errors:0 dropped:0 overruns:0 frame:0
          TX packets:35 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:0 
          RX bytes:3048 (2.9 KiB)  TX bytes:2550 (2.4 KiB)

enp0s3    Link encap:Ethernet  HWaddr 08:00:27:6D:54:6C  
          inet addr:108.88.3.112  Bcast:108.88.3.255  Mask:255.255.255.0
          inet6 addr: fe80::ecbb:8b50:8b48:204d/64 Scope:Link
          UP BROADCAST RUNNING MULTICAST  MTU:1500  Metric:1
          RX packets:3715950 errors:0 dropped:0 overruns:0 frame:0
          TX packets:66522 errors:0 dropped:0 overruns:0 carrier:0
          collisions:0 txqueuelen:1000 
          RX bytes:563852918 (537.7 MiB)  TX bytes:9135838 (8.7 MiB)

~~~


#### 自定义Docker桥接网络IP段



~~~

[root@docker02 docker]# sudo systemctl stop docker

[root@docker02 docker]# vi /etc/docker/daemon.json 
{
  "registry-mirrors": ["https://a4gh5fxo.mirror.aliyuncs.com"],
  "bip": "10.0.0.1/16"
}

[root@docker02 docker]# sudo systemctl start docker

[root@docker02 docker]# ifconfig 
docker0: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
        inet 10.0.0.1  netmask 255.255.0.0  broadcast 10.0.255.255
        ether 02:42:f1:d8:49:b0  txqueuelen 0  (Ethernet)
        RX packets 0  bytes 0 (0.0 B)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 0  bytes 0 (0.0 B)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0


~~~


#### 开启Doceker服务开启远程管理

~~~

 添加TCP访问

[root@docker02 system]# more /usr/lib/systemd/system/docker.service 
[Unit]
Description=Docker Application Container Engine
Documentation=https://docs.docker.com
BindsTo=containerd.service
After=network-online.target firewalld.service containerd.service
Wants=network-online.target
Requires=docker.socket

[Service]
Type=notify
# the default is not to use systemd for cgroups because the delegate issues still
# exists and systemd currently does not support the cgroup feature set required
# for containers run by docker
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock --containerd=/run/containerd/co
ntainerd.sock
ExecReload=/bin/kill -s HUP $MAINPID
TimeoutSec=0
RestartSec=2
Restart=always



重启docker

[root@docker02 system]# systemctl daemon-reload 
[root@docker02 system]# systemctl restart docker 
[root@docker02 system]# netstat -tnl
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State      
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN     
tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN     
tcp6       0      0 :::2375                 :::*                    LISTEN     
tcp6       0      0 :::22                   :::*                    LISTEN     
tcp6       0      0 ::1:25                  :::*                    LISTEN     
[root@docker02 system]# 





开通防火墙端口：2375

[root@docker02 system]# firewall-cmd --zone=public --add-port=2375/tcp --permanent 
success
[root@docker02 system]# firewall-cmd --zone=public --query-port=2375/tcp
no
[root@docker02 system]# firewall-cmd --reload
success
[root@docker02 system]# firewall-cmd --zone=public --query-port=2375/tcp
yes


远程管理：
[root@docker01 run]# docker -H tcp://108.88.3.143:2375 ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
[root@docker01 run]# docker -H tcp://108.88.3.143:2375 image ls
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
zhangguohan/busybox_http   v0.1-2              cdeabfa3c261        3 days ago          1.2MB
busybox                    latest              64f5d945efcc        4 weeks ago         1.2MB
[root@docker01 run]# 


~~~

### 新建容器网络对象
~~~
[root@docker01 run]# docker network create -d bridge --subnet "172.18.0.0/16" --gateway "172.18.0.1" mybr0
47e12578dc6492ea7e071c04b966700e846b7cd49d6a8c439a195a1e1752c547
[root@docker01 run]# docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
56a410c4ecfd        bridge              bridge              local
bc18eea006f6        host                host                local
47e12578dc64        mybr0               bridge              local
173843b1cf94        none                null                local
[root@docker01 run]#

[root@docker01 run]# ifconfig 
br-47e12578dc64: flags=4099<UP,BROADCAST,MULTICAST>  mtu 1500
        inet 172.18.0.1  netmask 255.255.0.0  broadcast 172.18.255.255
        ether 02:42:e6:e9:e5:84  txqueuelen 0  (Ethernet)
        RX packets 3754111  bytes 566438325 (540.1 MiB)
        RX errors 0  dropped 0  overruns 0  frame 0
        TX packets 67800  bytes 9310323 (8.8 MiB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0

~~~

