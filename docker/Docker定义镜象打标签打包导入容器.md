### 一、将容器打包成镜象
~~~
[root@docker01 ~]# docker commit -p b1
sha256:eca5494e83b5bab80cfe7012e21e2163b00da1ca743f4a1a91fc8e321f6c80dd
[root@docker01 ~]# docker images
REPOSITORY               TAG                 IMAGE ID            CREATED             SIZE
<none>                   <none>              eca5494e83b5        9 seconds ago       1.2MB
nginx                    1.17-alpine         bfba26ca350c        33 hours ago        20.5MB
nginx                    1.17.0-alpine       bfba26ca350c        33 hours ago        20.5MB
redis                    4-alpine            8ce91e22cd3f        3 weeks ago         35.5MB
postgres                 latest              587aa1d0e586        3 weeks ago         312MB
mysql                    latest              990386cbd5c0        3 weeks ago         443MB
busybox                  latest              64f5d945efcc        3 weeks ago         1.2MB
debian                   latest              8d31923452f8        4 weeks ago         101MB
quay.io/coreos/flannel   v0.11.0-amd64       ff281650a721        4 months ago        52.6MB


[root@docker01 ~]# docker tag --help

Usage:	docker tag SOURCE_IMAGE[:TAG] TARGET_IMAGE[:TAG]

~~~
### 二、镜象打标签
~~~
Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
[root@docker01 ~]# docker tag eca5494e83b5 zhangguohan/busybox_http:v0.1-1

[root@docker01 ~]# docker images
REPOSITORY                 TAG                 IMAGE ID            CREATED              SIZE
zhangguohan/busybox_http   v0.1-1              eca5494e83b5        About a minute ago   1.2MB
nginx                      1.17-alpine         bfba26ca350c        33 hours ago         20.5MB
nginx                      1.17.0-alpine       bfba26ca350c        33 hours ago         20.5MB
redis                      4-alpine            8ce91e22cd3f        3 weeks ago          35.5MB
postgres                   latest              587aa1d0e586        3 weeks ago          312MB
mysql                      latest              990386cbd5c0        3 weeks ago          443MB
busybox                    latest              64f5d945efcc        3 weeks ago          1.2MB
debian                     latest              8d31923452f8        4 weeks ago          101MB
quay.io/coreos/flannel     v0.11.0-amd64       ff281650a721        4 months ago         52.6MB

~~~
### 三、同一个版本打多个标签
~~~
[root@docker01 ~]# docker tag zhangguohan/busybox_http:v0.1-1 zhangguhan/buxysbox:latest
[root@docker01 ~]# docker images
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
zhangguhan/buxysbox        latest              eca5494e83b5        2 minutes ago       1.2MB
zhangguohan/busybox_http   v0.1-1              eca5494e83b5        2 minutes ago       1.2MB
nginx                      1.17-alpine         bfba26ca350c        33 hours ago        20.5MB
nginx                      1.17.0-alpine       bfba26ca350c        33 hours ago        20.5MB
redis                      4-alpine            8ce91e22cd3f        3 weeks ago         35.5MB
postgres                   latest              587aa1d0e586        3 weeks ago         312MB
mysql                      latest              990386cbd5c0        3 weeks ago         443MB
busybox                    latest              64f5d945efcc        3 weeks ago         1.2MB
debian                     latest              8d31923452f8        4 weeks ago         101MB
quay.io/coreos/flannel     v0.11.0-amd64       ff281650a721        4 months ago        52.6MB
~~~~

### 四、制作镜象打包标签及修改镜象启动参数
[root@docker01 ~]# docker commit -a "zahng<asdf@qq.com>" -c 'CMD ["/bin/httpd","-f", "-h","/data/html/"]' -p b1 zhangguohan/busybox_http:v0.1-2
sha256:cdeabfa3c261008736a6a3bfc68ca18ef03fe4003562d2719319ced09cdbd813


#### 五、推送镜象到hub.docker.com
~~~
[root@docker01 ~]# docker login
Login with your Docker ID to push and pull images from Docker Hub. If you don't have a Docker ID, head over to https://hub.docker.com to create one.
Username: zhangguohan
Password: 
WARNING! Your password will be stored unencrypted in /root/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
[root@docker01 ~]# docker push zhangguohan/busybox_http:v0.1-2
The push refers to repository [docker.io/zhangguohan/busybox_http]
6197406ee64d: Pushed 
d1156b98822d: Pushed 
v0.1-2: digest: sha256:8831c21b3b898c6c42ccc8d30089d3a77865e4467d0670a12b2db1e9eac4048c size: 734
[root@docker01 ~]# 
~~~

### 镜象打包及导入
~~~

[root@docker01 ~]# docker ps -a
CONTAINER ID        IMAGE                             COMMAND                  CREATED             STATUS                     PORTS               NAMES
9edec9441529        zhangguohan/busybox_http:v0.1-2   "/bin/httpd -f -h /d…"   2 hours ago         Up 2 hours                                     b3
5b4e0e3f3874        zhangguohan/busybox_http:v0.1-1   "sh"                     2 hours ago         Exited (127) 2 hours ago                       b2
8e72c4f94fd0        busybox                           "sh"                     3 hours ago         Exited (0) 2 hours ago                         b1
1e5ca7762447        redis:4-alpine                    "docker-entrypoint.s…"   11 hours ago        Up 11 hours                6379/tcp            kvstor1
0c9f67461e33        nginx:1.17-alpine                 "nginx -g 'daemon of…"   11 hours ago        Up 11 hours                80/tcp              web1
50bbdf842e92        debian                            "echo 'Hello world'"     3 days ago          Exited (0) 3 days ago                          recursing_sammet
a9e2bed353ae        debian                            "echo 'Hello world'"     3 days ago          Exited (0) 3 days ago                          determined_bose
[root@docker01 ~]# docker save -o httpd-image.gz zhangguohan/busybox_http:v0.1-2  busybox
[root@docker01 ~]# ll
total 1412
-rw-------. 1 root root    1261 May 21 06:00 anaconda-ks.cfg
-rw-------  1 root root 1440256 Jun  6 05:48 httpd-image.gz
[root@docker01 ~]# 
~~~



### 导入打包镜象文件
~~~
[root@docker01 ~]# scp httpd-image.gz  docker02:/tmp/
root@docker02's password: 
httpd-image.gz                                                                                                                                                                  100% 1407KB  10.1MB/s   00:00    
[root@docker01 ~]# ssh docker02
root@docker02's password: 
Last login: Thu Jun  6 05:26:30 2019 from 108.88.3.112
[root@localhost ~]# ll
total 4
-rw-------. 1 root root 1261 May 21 06:00 anaconda-ks.cfg
[root@localhost ~]# cd /tmp/
[root@localhost tmp]# ll
total 1408
-rw------- 1 root root 1440256 Jun  6 05:49 httpd-image.gz
drwx------ 3 root root      17 Jun  6 04:14 systemd-private-8160adb66c2b469b89854036306e971f-chronyd.service-nqkKb6
[root@localhost tmp]# docker load
requested load from stdin, but stdin is empty
[root@localhost tmp]# docker load --help

Usage:	docker load [OPTIONS]

Load an image from a tar archive or STDIN

Options:
  -i, --input string   Read from tar archive file, instead of STDIN
  -q, --quiet          Suppress the load output
[root@localhost tmp]# docker load -i httpd-image.gz 
d1156b98822d: Loading layer [==================================================>]  1.416MB/1.416MB
6197406ee64d: Loading layer [==================================================>]   5.12kB/5.12kB
Loaded image: zhangguohan/busybox_http:v0.1-2
Loaded image: busybox:latest
[root@localhost tmp]#
~~~

