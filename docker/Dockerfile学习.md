### 自制镜象


#### 简单Dockerfile--例1 COPY 复制文件
~~~


编写Dockerfile文件

[root@docker01 img1]# vi Dockerfile 

#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
copy index.html /data/www/html/

生成测试文件
[root@docker01 img1]# vi index.html 

this is tesx page

                                 
生成镜象指标签

[root@docker01 img1]# docker build -t tankhttpd:v0.1-1 ./
Sending build context to Docker daemon  3.072kB
Step 1/3 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/3 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Running in 39ec20f08f3b
Removing intermediate container 39ec20f08f3b
 ---> c0d1ae2de1e2
Step 3/3 : copy index.html /data/www/html/
 ---> 731d4b288991
Successfully built 731d4b288991
Successfully tagged tankhttpd:v0.1-1
[root@docker01 img1]# 


查看生成镜象

[root@docker01 img1]# docker image list
REPOSITORY                 TAG                 IMAGE ID            CREATED             SIZE
tankhttpd                  v0.1-1              731d4b288991        2 minutes ago       1.2MB
zhangguohan/busybox_http   v0.1-2              cdeabfa3c261        5 days ago          1.2MB
<none>                     <none>              8c2353aadb61        5 days ago          1.2MB
zhangguohan/busybox_http   v0.1-1              eca5494e83b5        5 days ago          1.2MB
nginx                      1.17-alpine         bfba26ca350c        6 days ago          20.5MB
nginx                      1.17.0-alpine       bfba26ca350c        6 days ago          20.5MB
redis                      4-alpine            8ce91e22cd3f        4 weeks ago         35.5MB
postgres                   latest              587aa1d0e586        4 weeks ago         312MB
mysql                      latest              990386cbd5c0        4 weeks ago         443MB
busybox                    latest              64f5d945efcc        4 weeks ago         1.2MB
debian                     latest              8d31923452f8        4 weeks ago         101MB
quay.io/coreos/flannel     v0.11.0-amd64       ff281650a721        4 months ago        52.6MB
[root@docker01 img1]# 

检查看镜象是否包含COPY内容

[root@docker01 img1]# docker run --name tank1 --rm  tankhttpd:v0.1-1 cat /data/www/html/index.html
this is tesx page
[root@docker01 img1]# 
~~~
		
#### 简单Dockerfile--例2 COPY 复制目录

~~~

编辑Dockerfile 文件

[root@docker01 img1]# vi Dockerfile 

#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
copy index.html /data/www/html/
copy yum.repos.d /etc/yum.repos.d/



生成镜象指标签

[root@docker01 img1]# docker build -t tankhttpd:v0.1-2 ./
Sending build context to Docker daemon  24.06kB
Step 1/4 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/4 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/4 : copy index.html /data/www/html/
 ---> Using cache
 ---> 731d4b288991
Step 4/4 : copy yum.repos.d /etc/yum.repos.d/
 ---> 4c563c17c1dc
Successfully built 4c563c17c1dc
Successfully tagged tankhttpd:v0.1-2

检查看镜象是否包含COPY内容

[root@docker01 img1]# docker run --name tank3 --rm  tankhttpd:v0.1-2 ls /etc/yum.repos.d/
CentOS-Base.repo
CentOS-CR.repo
CentOS-Debuginfo.repo
CentOS-Media.repo
CentOS-Sources.repo
CentOS-Vault.repo
CentOS-fasttrack.repo
docker-ce.repo

~~~

#### 简单Dockerfile--例3 ADD命令

~~~
  
- ADD命令类似以COPY命令，ADD支持使用TAR文件和URL路径
   -ADD <src>...<dest>


编辑Dockerfile 添加ADD命令

[root@docker01 img1]# vi Dockerfile 

#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
copy index.html /data/www/html/
copy yum.repos.d /etc/yum.repos.d/
ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/


生成镜象指标签 

[root@docker01 img1]# docker build -t tankhttpd:v0.1-3 ./
Sending build context to Docker daemon  24.06kB
Step 1/5 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/5 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/5 : copy index.html /data/www/html/
 ---> Using cache
 ---> 731d4b288991
Step 4/5 : copy yum.repos.d /etc/yum.repos.d/
 ---> Using cache
 ---> 4c563c17c1dc
Step 5/5 : ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/
Downloading [==================================================>]  1.015MB/1.015MB
 ---> 42e3fe9117b2
Successfully built 42e3fe9117b2
Successfully tagged tankhttpd:v0.1-3

检查看镜象是否包含ADD内容（由于使用URL源，则不会解压在镜象）

[root@docker01 img1]# docker run --name tank4 --rm  tankhttpd:v0.1-3 ls /usr/local/src
nginx-1.14.2.tar.gz

[root@docker01 img1]# vi  Dockerfile 
#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
copy index.html /data/www/html/
copy yum.repos.d /etc/yum.repos.d/
#ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/
ADD nginx-1.14.2.tar.gz  /usr/local/src/



检查看镜象是否包含ADD内容（由于使用本地，则解压在镜象）
[root@docker01 img1]# vi Dockerfile 



生成镜象指标签 
[root@docker01 img1]# docker build -t tankhttpd:v0.1-4 ./

Sending build context to Docker daemon   1.04MB
Step 1/5 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/5 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/5 : copy index.html /data/www/html/
 ---> Using cache
 ---> 731d4b288991
Step 4/5 : copy yum.repos.d /etc/yum.repos.d/
 ---> Using cache
 ---> 4c563c17c1dc
Step 5/5 : ADD nginx-1.14.2.tar.gz  /usr/local/src/
 ---> c682e74d9a29
Successfully built c682e74d9a29
Successfully tagged tankhttpd:v0.1-4



检查看镜象是否包含ADD内容（由于使用本地源，会解压在镜象）
[root@docker01 img1]# docker run --name tank5 --rm  tankhttpd:v0.1-4 ls /usr/local/src
nginx-1.14.2
[root@docker01 img1]# 
~~~


#### 简单Dockerfile--例3 WORKDIR、VOLUME命令

WORKDIR 可以多处定议，只对后面的生效

~~~
[root@docker01 img1]# vi Dockerfile 

#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
copy index.html /data/www/html/
copy yum.repos.d /etc/yum.repos.d/
#ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/

WORKDIR /usr/local/

ADD nginx-1.14.2.tar.gz  ./src/

VOLUME /data/mysql/


生成镜象

[root@docker01 img1]# docker build -t tankhttpd:v0.1-5 ./

Sending build context to Docker daemon   1.04MB
Step 1/7 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/7 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/7 : copy index.html /data/www/html/
 ---> Using cache
 ---> 731d4b288991
Step 4/7 : copy yum.repos.d /etc/yum.repos.d/
 ---> Using cache
 ---> 4c563c17c1dc
Step 5/7 : WORKDIR /usr/local/
 ---> Running in 4e757d09062b
Removing intermediate container 4e757d09062b
 ---> d3bcc064822e
Step 6/7 : ADD nginx-1.14.2.tar.gz  ./src/
 ---> 8b38b6814f97
Step 7/7 : VOLUME /data/mysql/
 ---> Running in 0e8c5b2c4eae
Removing intermediate container 0e8c5b2c4eae
 ---> 9cfa80c743d5
Successfully built 9cfa80c743d5
Successfully tagged tankhttpd:v0.1-5

检查看镜象是否包含WORKDIR\VOLUME 内容

[root@docker01 img1]# docker run --name tank6 --rm  tankhttpd:v0.1-5 mount
...........
cgroup on /sys/fs/cgroup/freezer type cgroup (ro,nosuid,nodev,noexec,relatime,freezer)
cgroup on /sys/fs/cgroup/pids type cgroup (ro,nosuid,nodev,noexec,relatime,pids)
mqueue on /dev/mqueue type mqueue (rw,nosuid,nodev,noexec,relatime)
/dev/mapper/centos-root on /data/mysql type xfs (rw,relatime,attr2,inode64,noquota)
~~~







