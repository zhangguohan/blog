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


#### 简单Dockerfile--例4 WORKDIR、VOLUME命令

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



#### 简单Dockerfile--例5 EXPOSE命令
~~~
EXPOSE 默认待暴露通过宿主机访问的端口，另外，也可以通过-p 指定端口

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

EXPOSE 80/tcp


生成镜象

[root@docker01 img1]# docker build -t tankhttpd:v0.1-6 ./
Sending build context to Docker daemon   1.04MB
Step 1/8 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/8 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/8 : copy index.html /data/www/html/
 ---> Using cache
 ---> 731d4b288991
Step 4/8 : copy yum.repos.d /etc/yum.repos.d/
 ---> Using cache
 ---> 4c563c17c1dc
Step 5/8 : WORKDIR /usr/local/
 ---> Using cache
 ---> d3bcc064822e
Step 6/8 : ADD nginx-1.14.2.tar.gz  ./src/
 ---> Using cache
 ---> 8b38b6814f97
Step 7/8 : VOLUME /data/mysql/
 ---> Using cache
 ---> 9cfa80c743d5
Step 8/8 : EXPOSE 80/tcp
 ---> Running in 38dfbffba611
Removing intermediate container 38dfbffba611
 ---> d92f40830f84
Successfully built d92f40830f84
Successfully tagged tankhttpd:v0.1-6
[root@docker01 img1]# 

默认不对通过宿主机对外访问：

[root@docker01 img1]# docker run --name tank7 --rm  tankhttpd:v0.1-6 /bin/httpd -f -h /data/www/html/


需要使用-P开放所有待开放端口，注意是动态分配
[root@docker01 img1]# docker run --name tank7 --rm  -P tankhttpd:v0.1-6 /bin/httpd -f -h /data/www/html/

[root@docker01 ~]# docker port tank7
80/tcp -> 0.0.0.0:32768
[root@docker01 ~]# 


~~~


#### 简单Dockerfile--例6 ENV 命令


~~~

编写Dockerfile 添加ENV 

[root@docker01 img1]#  more Dockerfile 
#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
ENV DOC_ROOT=/data/www/html/

copy index.html $DOC_ROOT
copy yum.repos.d /etc/yum.repos.d/
#ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/

WORKDIR /usr/local/

ADD nginx-1.14.2.tar.gz  ./src/

VOLUME /data/mysql/

EXPOSE 80/tcp


生成测试镜象

[root@docker01 img1]# docker build -t tankhttpd:v0.1-8 ./

Sending build context to Docker daemon   1.04MB
Step 1/9 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/9 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/9 : ENV DOC_ROOT=/data/www/html/
 ---> Running in 65239a1b56e7
Removing intermediate container 65239a1b56e7
 ---> f530f9a186a3
Step 4/9 : copy index.html $DOC_ROOT
 ---> aa6dea2a9635
Step 5/9 : copy yum.repos.d /etc/yum.repos.d/
 ---> 6106e2cbd659
Step 6/9 : WORKDIR /usr/local/
 ---> Running in c554532ef73b
Removing intermediate container c554532ef73b
 ---> 0cd2cbab0c1a
Step 7/9 : ADD nginx-1.14.2.tar.gz  ./src/
 ---> cadfdb1a69e8
Step 8/9 : VOLUME /data/mysql/
 ---> Running in 1f6d2a9146c2
Removing intermediate container 1f6d2a9146c2
 ---> 8596bb9eb5f0
Step 9/9 : EXPOSE 80/tcp
 ---> Running in ea5dfaaf6758
Removing intermediate container ea5dfaaf6758
 ---> 971471810773
Successfully built 971471810773
Successfully tagged tankhttpd:v0.1-8

检查看镜象是否使用到evn定义
[root@docker01 img1]# docker run --name tank8 --rm  -P tankhttpd:v0.1-8 ls /data/www/html/
index.html
[root@docker01 img1]# 

~~~




#### 简单Dockerfile--例7 RUN 命令

~~~

 RUN命令是指在build阶段执行的命令



 编辑Dockerfile 添加RUN命令

[root@docker01 img1]#  more Dockerfile 
#Description : test image
FROM busybox:latest
MAINTAINER "tank<tank.zhang@gzgi.com>"
#LABLE MAINTAINER="tank<tank.zhang@gzgi.com>"
ENV DOC_ROOT=/data/www/html/

copy index.html $DOC_ROOT
copy yum.repos.d /etc/yum.repos.d/
ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/

WORKDIR /usr/local/

#ADD nginx-1.14.2.tar.gz  ./src/

VOLUME /data/mysql/

EXPOSE 80/tcp
RUN cd /usr/local/src && \
	tar -xf nginx-1.14.2.tar.gz 

[root@docker01 img1]# 



生成镜象文件


[root@docker01 img1]# docker build -t tankhttpd:v0.1-9 ./
Sending build context to Docker daemon   1.04MB
Step 1/10 : FROM busybox:latest
 ---> 64f5d945efcc
Step 2/10 : MAINTAINER "tank<tank.zhang@gzgi.com>"
 ---> Using cache
 ---> c0d1ae2de1e2
Step 3/10 : ENV DOC_ROOT=/data/www/html/
 ---> Using cache
 ---> f530f9a186a3
Step 4/10 : copy index.html $DOC_ROOT
 ---> Using cache
 ---> aa6dea2a9635
Step 5/10 : copy yum.repos.d /etc/yum.repos.d/
 ---> Using cache
 ---> 6106e2cbd659
Step 6/10 : ADD http://nginx.org/download/nginx-1.14.2.tar.gz  /usr/local/src/
Downloading  1.015MB/1.015MB
 ---> Using cache
 ---> 699f8542cc07
Step 7/10 : WORKDIR /usr/local/
 ---> Using cache
 ---> 6205890b3146
Step 8/10 : VOLUME /data/mysql/
 ---> Using cache
 ---> 7aa5617370d5
Step 9/10 : EXPOSE 80/tcp
 ---> Using cache
 ---> e5bb6a849e21
Step 10/10 : RUN cd /usr/local/src && 	tar -xf nginx-1.14.2.tar.gz
 ---> Running in acce8d2087f0
Removing intermediate container acce8d2087f0
 ---> 710bd2ac73c7
Successfully built 710bd2ac73c7
Successfully tagged tankhttpd:v0.1-9
[root@docker01 img1]# 

检查生成容器

[root@docker01 img1]# docker run --name tank89 --rm -it  -P tankhttpd:v0.1-8 
/usr/local # cd src/
/usr/local/src # ls
nginx-1.14.2
/usr/local/src # 



~~~


#### 简单Dockerfile--例8 CMD 命令

~~~

     CMD为默认运行程序，可以存多个CMD，但只有一种有效



编写Dockerfile

[root@docker01 img2]# vi dockerfile

FROM busybox
LABEL maintainer="tank <tank.zhang@gzgi.com>" app="httpd"
ENV WEB_DOC_ROOT="/data/web/html/"
RUN mkdir -p ${WEB_DOC_ROOT} && \ 
 echo "<h1> busybox httpd server.</h1>" >${WEB_DOC/ROOT}/index.html

CMD /bin/httpd -f -h ${WEB_DOC_ROOT}


生成镜象文件

[root@docker01 img2]# docker build -t tankhttpd:v0.2-2 ./
Sending build context to Docker daemon  2.048kB
Step 1/5 : FROM busybox
 ---> 64f5d945efcc
Step 2/5 : LABEL maintainer="tank <tank.zhang@gzgi.com>" app="httpd"
 ---> Using cache
 ---> 5cd809c3d589
Step 3/5 : ENV WEB_DOC_ROOT="/data/web/html/"
 ---> Using cache
 ---> f1fb0b3dc269
Step 4/5 : RUN mkdir -p ${WEB_DOC_ROOT} &&  echo "<h1> busybox httpd server.</h1>" >${WEB_DOC/ROOT}/index.html
 ---> Running in 21a8d7a7226c
Removing intermediate container 21a8d7a7226c
 ---> 1105e31ec992
Step 5/5 : CMD /bin/httpd -f -h ${WEB_DOC_ROOT}
 ---> Running in a1561a843a7b
Removing intermediate container a1561a843a7b
 ---> cac8c8a8224d
Successfully built cac8c8a8224d
Successfully tagged tankhttpd:v0.2-2


使用镜象生成容器（注意由于使用CMD启动，则没有启动bash进程）

[root@docker01 img2]# docker run --name tank02 -it --rm -P tankhttpd:v0.2-2

[root@docker01 ~]# docker ps -a
CONTAINER ID        IMAGE                             COMMAND                  CREATED             STATUS                      PORTS               NAMES
1d6d3e06a1f6        tankhttpd:v0.2-2                  "/bin/sh -c '/bin/ht…"   30 seconds ago      Up 26 seconds                                   tank02
1aefb7c7f977        f1fb0b3dc269                      "/bin/sh -c 'mkdir $…"   3 minutes ago       Exited (1) 3 minutes ago                        vigorous_darwin


可以使用exec 容器内执行/bin/sh

[root@docker01 ~]# docker exec -it tank02 /bin/sh
/ # ps -ef
PID   USER     TIME  COMMAND
    1 root      0:00 /bin/httpd -f -h /data/web/html/
   19 root      0:00 /bin/sh
   25 root      0:00 ps -ef
/ # 


#### 简单Dockerfile--例9 ENTRYPOINT  命令

- 类似CMD指令的功能，用于为容器指定默认运行程序，从而使得容器像一上单独的可执行程序
- 以CMD不同的是，由ENTRYPOINT启动的程序不会被docker run 命令指定的参数覆盖，而且，这些命令行参数会当作参数传递给ENTRYPOINT 指定的程序，


编写Dockerfile 文件

[root@docker01 img3]# more Dockerfile 
FROM nginx:1.17-alpine
LABEL maintainer="tank.zhang <tank.zhang@gzgi.com>"
ENV NGX_DOC_ROOT="/data/web/html/"
ADD index.html ${NGX_DOC_ROOT}/
ADD entrypoint.sh /bin/

CMD ["/usr/sbin/nginx","-g","daemon off;"]
ENTRYPOINT ["/bin/entrypoint.sh"]

编写entrypoint.sh文件

[root@docker01 img3]# more entrypoint.sh 
#!/bin/sh
#
cat > /etc/nginx/conf.d/www.conf <<EOF
server{
	server_name ${HOSTNAME};
	listen ${IP:-0.0.0.0}:${PORT:-80};
	root ${WEB_DOC_ROOT:-/usr/share/nginx/html};


}
EOF

exec "$@"

[root@docker01 img3]# more index.html 
sfomlasdkfjsaldjf 
[root@docker01 img3]# 


[root@docker01 img3]# docker run --name tank06 --rm -P  tankhttpd:v0.3-9 

[root@docker01 img2]# docker exec -it tank06 /bin/sh
/ # netstat -tnl
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       
tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      
/ # ps -ef
PID   USER     TIME  COMMAND
    1 root      0:00 nginx: master process /usr/sbin/nginx -g daemon off;
    9 nginx     0:00 nginx: worker process
   10 nginx     0:00 nginx: worker process
   11 nginx     0:00 nginx: worker process
   12 nginx     0:00 nginx: worker process
   13 root      0:00 /bin/sh
   19 root      0:00 ps -ef
/ # 



使用docker run 变量


[root@docker01 img3]# docker run --name tank06 -e "PORT=8080" --rm -P  tankhttpd:v0.3-9 


[root@docker01 img2]# docker exec -it tank06 /bin/sh
/ # netstat -tnl
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       
tcp        0      0 0.0.0.0:8080            0.0.0.0:*               LISTEN      
tcp        0      0 0.0.0.0:80              0.0.0.0:*               LISTEN      
/ # 



~~~