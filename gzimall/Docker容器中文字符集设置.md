# Docker容器中文字符集设置

一、设置容器内中文字符集为zh_CN.GBK

```
[root@localhost ~]# more Dockerfile
FROM centos:7 

EXPOSE 8080

RUN yum install kde-l10n-Chinese -y
#RUN yum install glibc-common -y
RUN  localedef  -f GBK -i zh_CN zh_CN.GBK
RUN export LANG=zh_CN.GBK
RUN echo "export LANG=zh_CN.GBK" >> /etc/locale.conf
ENV LANG zh_CN.GBK
ENV LC_ALL zh_CN.GBK
[root@localhost ~]# docker build -t resin:iemstestv-v20 ./
```

二、设置容器内中文字符集为zh_CN.UTF-8

```
[root@localhost ~]# more Dockerfile-utf-8 
FROM centos:7 

EXPOSE 8080

RUN yum install kde-l10n-Chinese -y
RUN yum install glibc-common -y
RUN localedef -c -f UTF-8 -i zh_CN zh_CN.utf8
RUN export LANG=zh_CN.UTF-8
RUN echo "export LANG=zh_CN.UTF-8" >> /etc/locale.conf
ENV LANG zh_CN.UTF-8
ENV LC_ALL zh_CN.UTF-8 


[root@localhost ~]# docker build -t resin:iemstestv-v21 ./
```