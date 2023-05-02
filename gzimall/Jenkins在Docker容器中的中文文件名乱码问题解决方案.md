## Jenkins在Docker容器中的中文文件名乱码问题解决方案

## 默认初始化安装出现SVN获取文件中文乱码

1、主机为Centos7 安装Docker

2、出现问题现象为Jenkins控制台采用SVN 获取中文文件名出现？？？？，造成中文文件无法下载

## 解决方案

1、修改Jenkins下Jenkins.sh

```
root@adc79a310e15:/# more /usr/local/bin/jenkins.sh 
在文件开头设置环境变量
#! /bin/bash 


export LANG=C.UTF-8
export JAVA_OPTS="-Dsun.jnu.encoding=UTF-8 -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"
```

2、在Jenkins控制界面设置言语变量

```
2.1 系统管理---> 系统配置--> 全局属性-->添加 值LANG为C.UTF-8


2.2 系统管理---> 系统配置--> 全局属性-->添加 JAVA_TOOL_OPTIONS值为:-Dfile.encoding=UTF-8

2.3 重启Docker中jenkins容器
```

3 、如采用远程节点节点发部，需要在对应节点列表中设置

```
配置从节点的LANG=zh_CN.UTF-8，相关节点操作系统编码或启动节点脚本也需要为保持一致LANG=zh_CN.UTF-8，
```

4、以上即可完美解决SVN下载中文乱码问题