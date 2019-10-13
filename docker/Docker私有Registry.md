
### 安装Docker Registry


~~~

[root@docker02 ~]# yum install docker-registry
Loaded plugins: fastestmirror
Loading mirror speeds from cached hostfile
 * base: mirrors.163.com
 * extras: mirrors.aliyun.com
 * updates: mirrors.aliyun.com
base                                                                                   | 3.6 kB  00:00:00     
docker-ce-stable                                                                       | 3.5 kB  00:00:00     
extras                                                                                 | 3.4 kB  00:00:00     
updates                                                                                | 3.4 kB  00:00:00     
updates/7/x86_64/primary_db                                                            | 5.7 MB  00:00:01     
Package docker-registry is obsoleted by docker-distribution, trying to install docker-distribution-2.6.2-2.git48294d9.el7.x86_64 instead
Resolving Dependencies
--> Running transaction check
---> Package docker-distribution.x86_64 0:2.6.2-2.git48294d9.el7 will be installed
--> Finished Dependency Resolution

Dependencies Resolved

==============================================================================================================
 Package                        Arch              Version                             Repository         Size
==============================================================================================================
Installing:
 docker-distribution            x86_64            2.6.2-2.git48294d9.el7              extras            3.5 M

Transaction Summary
==============================================================================================================
Install  1 Package

Total download size: 3.5 M
Installed size: 12 M
Is this ok [y/d/N]: y
Downloading packages:
docker-distribution-2.6.2-2.git48294d9.el7.x86_64.rpm                                  | 3.5 MB  00:00:01     
Running transaction check
Running transaction test
Transaction test succeeded
Running transaction
  Installing : docker-distribution-2.6.2-2.git48294d9.el7.x86_64                                          1/1 
  Verifying  : docker-distribution-2.6.2-2.git48294d9.el7.x86_64                                                                                                                         1/1 

Installed:
  docker-distribution.x86_64 0:2.6.2-2.git48294d9.el7                                                                                                                                        

Complete!
[root@docker02 ~]# 


查看安装docker-distribution位置

[root@docker02 ~]# rpm -ql docker-distribution
/etc/docker-distribution/registry/config.yml
/usr/bin/registry
/usr/lib/systemd/system/docker-distribution.service
/usr/share/doc/docker-distribution-2.6.2
/usr/share/doc/docker-distribution-2.6.2/AUTHORS
/usr/share/doc/docker-distribution-2.6.2/CONTRIBUTING.md
/usr/share/doc/docker-distribution-2.6.2/LICENSE
/usr/share/doc/docker-distribution-2.6.2/MAINTAINERS
/usr/share/doc/docker-distribution-2.6.2/README.md
/var/lib/registry
[root@docker02 ~]# 



[root@docker02 ~]# more /etc/docker-distribution/registry/config.yml
version: 0.1
log:
  fields:
    service: registry
storage:
    cache:
        layerinfo: inmemory
    filesystem:
        rootdirectory: /var/lib/registry
http:
    addr: :5000
[root@docker02 ~]#


启动私有仓库进程服务


[root@docker02 ~]# systemctl start docker-distribution
[root@docker02 ~]# netstat -tnl
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State      
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN     
tcp        0      0 127.0.0.1:25            0.0.0.0:*               LISTEN     
tcp6       0      0 :::2375                 :::*                    LISTEN     
tcp6       0      0 :::5000                 :::*                    LISTEN     
tcp6       0      0 :::22                   :::*                    LISTEN     
tcp6       0      0 ::1:25                  :::*                    LISTEN     
[root@docker02 ~]# 

~~~


### 将本地镜象推送到私有 Docker Registry


~~~

将原来本地镜象打标签为私有Registry地址


[root@docker01 ~]# docker tag tankhttpd:v0.3-9 docker02:5000/tankhttpd:v0.3-9
[root@docker01 ~]# docker image ls
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
tankhttpd                  v0.3-9              32e45e219bf1        13 hours ago        20.5MB
docker02:5000/tankhttpd    v0.3-9              32e45e219bf1        13 hours ago        20.5MB


直接推送失败，默认只支的https，需要在客户端开启忽略安装

[root@docker01 ~]#  docker push docker02:5000/tankhttpd:v0.3-9 
The push refers to repository [docker02:5000/tankhttpd]
Get https://docker02:5000/v2/: http: server gave HTTP response to HTTPS client
[root@docker01 ~]# 






[root@docker01 ~]#  docker push docker02:5000/tankhttpd:v0.3-9 
The push refers to repository [docker02:5000/tankhttpd]
f9ce0a1a9f71: Pushed 
77bf126bc52f: Pushed 
402522b96a27: Pushed 
f1b5933fe4b5: Pushed 
v0.3-9: digest: sha256:9dadf7d0419c3f8e8c4a1df734d0d16a45b8e04725cd008225f1d5de2f1bbcfd size: 1153
[root@docker01 ~]# 


~~~



### 使用私有Restriy

~~~

添加Restriy  URL 为不安全的

[root@docker02 ~]# more /etc/docker/daemon.json 
{
  "registry-mirrors": ["https://a4gh5fxo.mirror.aliyuncs.com"],
  "bip": "10.0.0.1/16",
    "insecure-registries": ["docker02:5000"]
}
[root@docker02 ~]#  systemctl  restart docker


[root@docker02 ~]# docker image ls
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
zhangguohan/busybox_http   v0.1-2              cdeabfa3c261        6 days ago          1.2MB
busybox                    latest              64f5d945efcc        4 weeks ago         1.2MB

拉取私有镜象


[root@docker02 ~]# docker pull docker02:5000/tankhttpd:v0.3-9
v0.3-9: Pulling from tankhttpd
e7c96db7181b: Pull complete 
f0e40e45c95e: Pull complete 
ffdc533597a9: Pull complete 
fe8e8f2b7074: Pull complete 
Digest: sha256:9dadf7d0419c3f8e8c4a1df734d0d16a45b8e04725cd008225f1d5de2f1bbcfd
Status: Downloaded newer image for docker02:5000/tankhttpd:v0.3-9
[root@docker02 ~]# docker image ls
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
docker02:5000/tankhttpd    v0.3-9              32e45e219bf1        15 hours ago        20.5MB
zhangguohan/busybox_http   v0.1-2              cdeabfa3c261        6 days ago          1.2MB
busybox                    latest              64f5d945efcc        4 weeks ago         1.2MB
 

使有用镜象

[root@docker02 ~]# docker run --name tank02 --rm -P  docker02:5000/tankhttpd:v0.3-9




Harbor 开源私有镜象软件学习后期


~~~