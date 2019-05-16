
## CentOS7安装Oracle11g—静默安装

### 配置好Centos7 

```
cd /etc
mv yum.repos.d yum.repos.d.bak
mkdir yum.repos.d
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
yum clean all
yum makecache

```


### 一、安装依赖包


```
yum -y install binutils \
compat-libstdc++-33 \
elfutils-libelf \
elfutils-libelf-devel \
expat \
gcc \
gcc-c++ \
glibc \
glibc-common \
glibc-devel \
glibc-headers \
libaio \
libaio-devel \
libgcc \
libstdc++ \
libstdc++-devel \
make \
pdksh \
sysstat \
unixODBC \
unixODBC-devel

```
### 二、检查安装包

```
rpm -q \
binutils \
compat-libstdc++-33 \
elfutils-libelf \
elfutils-libelf-devel \
expat \
gcc \
gcc-c++ \
glibc \
glibc-common \
glibc-devel \
glibc-headers \
libaio \
libaio-devel \
libgcc \
libstdc++ \
libstdc++-devel \
make \
pdksh \
sysstat \
unixODBC \
unixODBC-devel | grep "not installed"


```

### 三、安装pdksh

```

wget -c http://vault.centos.org/5.11/os/x86_64/CentOS/pdksh-5.2.14-37.el5_8.1.x86_64.rpm

rpm -ivh pdksh-5.2.14-37.el5_8.1.x86_64.rpm

```

### 四、修改linux内核参数

```
[root@11g ~]# tail -13 /etc/sysctl.conf （在文件尾部插入如下内容）
#oracle 11G
fs.aio-max-nr = 1048576
fs.file-max = 6815744
kernel.shmall = 2097152
kernel.shmmax = 4294967295
kernel.shmmni = 4096
kernel.sem = 250 32000 100 128
net.ipv4.ip_local_port_range = 9000 65500
net.core.rmem_default = 262144
net.core.rmem_max = 4194304
net.core.wmem_default = 262144
net.core.wmem_max = 1048576
[root@11g ~]# /sbin/sysctl -p （不重启使参数生效）

```

### 五、关闭防火墙和selinux

```
[root@steelDB ~]# more /etc/selinux/config 

# This file controls the state of SELinux on the system.
# SELINUX= can take one of these three values:
#     enforcing - SELinux security policy is enforced.
#     permissive - SELinux prints warnings instead of enforcing.
#     disabled - No SELinux policy is loaded.
SELINUX=disabled
# SELINUXTYPE= can take one of three two values:
#     targeted - Targeted processes are protected,
#     minimum - Modification of targeted policy. Only selected processes are protected. 
#     mls - Multi Level Security protection.
SELINUXTYPE=targeted 

```

### 设置oracle用户打开文件的最大数
```
[root@11g ~]# tail -10 /etc/security/limits.conf （在文件尾部插入如下内容）
#Oracle 11g
oracle soft nproc 2047
oracle hard nproc 16384
oracle soft nofile 1024
oracle hard nofile 65536
oracle soft stack 10240
# End of file




vim /etc/profile
 
if [ $USER = "oracle" ]; then
    if [ $SHELL = "/bin/ksh" ]; then
        ulimit -p 16384
        ulimit -n 65536
    else
        ulimit -u 16384 -n 65536
    fi
fi


# 
source /etc/profile

```

### 修改登录配置

```

[root@11g ~]# tail -2 /etc/pam.d/login
#Oracle 11g
session required pam_limits.so

```


### 修改OS系统标识

```
vi /etc/redhat-release#修改成红色部分文字
redhat-7 

```

### 解压数据库安装包 

unzip linux.x64_11gR2_database_1of2.zip -d /data/database/  #解压文件1
unzip linux.x64_11gR2_database_2of2.zip -d /data/database/  #解压文件2

### 配置oracle用户环境变量

```
[root@11g ~]# su - oracle
[oracle@11g ~]$ tail -5 .bash_profile
#Oracle 11g
export ORACLE_SID=orcl                                  #数据库实例名
export ORACLE_BASE=/u01/app/oracle                      #Oracle安装目录
export ORACLE_HOME=$ORACLE_BASE/product/11.2.0/db_1     #Oracle家目录
export PATH=$PATH:$ORACLE_HOME/bin                      #Path搜索路径
[oracle@11g ~]$ source .bash_profile                    #刷新.bash_profile配置
[oracle@11g ~]$ echo $ORACLE_SID                        #测试.bash_profile是否生效
orcl

```

### 新建oracle安装目录

```
[root@11g ~]# mkdir -p /u01/app/oracle/product/11.2.0/db_1
[root@11g ~]# chown -R oracle:oinstall /u01
[root@11g ~]# chmod -R 755 /u01

```

### 执行静默安装

```

[oracle@steelDB database]$  ./runInstaller -silent -ignoreSysPrereqs -showProgress -responseFile /u01/soft/database/response/db_install.rsp 
正在启动 Oracle Universal Installer...

检查临时空间: 必须大于 120 MB。   实际为 49980 MB    通过
检查交换空间: 必须大于 150 MB。   实际为 16127 MB    通过
准备从以下地址启动 Oracle Universal Installer /tmp/OraInstall2019-05-16_02-21-42PM. 请稍候...[oracle@steelDB database]$ [WARNING] [INS-32055] 主产品清单位于 Oracle 基目录中。
   原因: 主产品清单位于 Oracle 基目录中。
   操作: Oracle 建议将此主产品清单放置在 Oracle 基目录之外的位置中。
[WARNING] [INS-30011] 输入的 ADMIN 口令不符合 Oracle 建议的标准。
   原因: Oracle 建议输入的口令应该至少长为 8 个字符, 至少包含 1 个大写字符, 1 个小写字符和 1 个数字 [0-9]。
   操作: 提供符合 Oracle 建议标准的口令。
[WARNING] [INS-13014] 目标环境不满足一些可选要求。
   原因: 不满足一些可选的先决条件。有关详细信息, 请查看日志。/tmp/OraInstall2019-05-16_02-21-42PM/installActions2019-05-16_02-21-42PM.log
   操作: 从日志 /tmp/OraInstall2019-05-16_02-21-42PM/installActions2019-05-16_02-21-42PM.log 中确定失败的先决条件检查列表。然后, 从日志文件或安装手册中查找满足这些先决条件的适当配置, 并手动进行修复。

[oracle@steelDB database]$ 可以在以下位置找到本次安装会话的日志:
 /u01/app/oracle/oraInventory/logs/installActions2019-05-16_02-21-42PM.log

准备 正在进行中。
..................................................   8% 完成。

准备成功。

复制文件 正在进行中。
[oracle@steelDB database]$ ........................................   13% 完成。
..................................................   19% 完成。
..................................................   24% 完成。
..................................................   29% 完成。
..................................................   35% 完成。
..................................................   40% 完成。
..................................................   45% 完成。
..................................................   51% 完成。
..................................................   56% 完成。
..................................................   61% 完成。
..................................................   66% 完成。
........................................
复制文件成功。

链接二进制文件 正在进行中。
..........
链接二进制文件成功。

安装程序文件 正在进行中。
..................................................   71% 完成。
..................................................   76% 完成。

安装程序文件成功。
[oracle@steelDB database]$ Oracle Database 11g 的 安装 已成功。
请查看 '/u01/app/oracle/oraInventory/logs/silentInstall2019-05-16_02-21-42PM.log' 以获取详细资料。

Oracle Net Configuration Assistant 正在进行中。
..................................................   86% 完成。

Oracle Net Configuration Assistant成功。

Oracle Database Configuration Assistant 正在进行中。
..................................................   95% 完成。

Oracle Database Configuration Assistant成功。

执行 Root 脚本 正在进行中。

以 root 用户的身份执行以下脚本:
	1. /u01/app/oracle/oraInventory/orainstRoot.sh
	2. /u01/app/oracle/product/11.2.0/db_1/root.sh


..................................................   100% 完成。

执行 Root 脚本成功。
Successfully Setup Software.


```

### 使用root执行Oracle脚本
```
root@steelDB ~]# /u01/app/oracle/oraInventory/orainstRoot.sh
更改权限/u01/app/oracle/oraInventory.
添加组的读取和写入权限。
删除全局的读取, 写入和执行权限。

更改组名/u01/app/oracle/oraInventory 到 oinstall.
脚本的执行已完成。
[root@steelDB ~]# /u01/app/oracle/product/11.2.0/db_1/root.sh
Check /u01/app/oracle/product/11.2.0/db_1/install/root_steelDB_2019-05-16_14-38-21.log for the output of root script

```



### 安装rlwrap工具 

```
wget -c https://github.com/andreyvit/rlwrap/archive/master.zip
unzip master.zip 
cd rlwrap-master/
./configure 
yum install readline-devel
./configure 
make
make install
```
###oracle用户添加环境
```
[oracle@steelDB ~]$ more .bash_profile 
# .bash_profile

# Get the aliases and functions
if [ -f ~/.bashrc ]; then
	. ~/.bashrc
fi

# User specific environment and startup programs

PATH=$PATH:$HOME/.local/bin:$HOME/bin

export PATH

#Oracle 11g
export ORACLE_SID=orcl                                  #数据库实例名
export ORACLE_BASE=/u01/app/oracle                      #Oracle安装目录
export ORACLE_HOME=$ORACLE_BASE/product/11.2.0/db_1     #Oracle家目录
export PATH=$PATH:$ORACLE_HOME/bin
alias sqlplus='rlwrap sqlplus' 
alias rman='rlwrap rman'	
[oracle@steelDB ~]$
 
```
#### 导入生产环境数据库

```
[root@steelDB backup]# su - oracle
上一次登录：四 5月 16 15:07:28 CST 2019pts/0 上
[oracle@steelDB ~]$ sqlplus /nolog

SQL*Plus: Release 11.2.0.3.0 Production on Thu May 16 15:16:43 2019

Copyright (c) 1982, 2011, Oracle.  All rights reserved.

SQL> conn / as sysdba;
Connected.
create tablespace IEBMS
logging 
datafile '/u01/app/oracle/oradata/orcl/IEBMS.DBF' 
size 512m 
autoextend on 
next 100m maxsize 20G  
  7  extent management local; 

Tablespace created.

create user IEBMS identified by IEBMS2019
  2  default tablespace  IEBMS ; 

User created.

SQL> grant connect,resource,dba to  IEBMS; 

Grant succeeded.

SQL> create or replace directory BACKUP as '/u01/backup';

Directory created.

SQL> grant read, write on directory BACKUP to IEBMS;

Grant succeeded.

SELECT * FROM dba_profiles s WHERE s.profile='DEFAULT' AND resource_name='FAILED_LOGIN_ATTEMPTS';

PROFILE 		       RESOURCE_NAME			RESOURCE
------------------------------ -------------------------------- --------
LIMIT
----------------------------------------
DEFAULT 		       FAILED_LOGIN_ATTEMPTS		PASSWORD
10


SQL>  SELECT * FROM dba_profiles s WHERE s.profile='DEFAULT' AND resource_name='PASSWORD_LIFE_TIME';

PROFILE 		       RESOURCE_NAME			RESOURCE
------------------------------ -------------------------------- --------
LIMIT
----------------------------------------
DEFAULT 		       PASSWORD_LIFE_TIME		PASSWORD
180


SQL> ALTER PROFILE DEFAULT LIMIT PASSWORD_LIFE_TIME UNLIMITED;

Profile altered.

SQL>  SELECT * FROM dba_profiles s WHERE s.profile='DEFAULT' AND resource_name='PASSWORD_LIFE_TIME';

PROFILE 		       RESOURCE_NAME			RESOURCE
------------------------------ -------------------------------- --------
LIMIT
----------------------------------------
DEFAULT 		       PASSWORD_LIFE_TIME		PASSWORD
UNLIMITED


SQL> alter system set processes=1500 scope=spfile;

System altered.

SQL> alter system set sessions=1500 scope=spfile;

System altered.

SQL> shutdown immedite;
SP2-0717: illegal SHUTDOWN option
SQL> shutdown immediate;
Database closed.
Database dismounted.
ORACLE instance shut down.
SQL> startup;
ORACLE instance started.

Total System Global Area 1185853440 bytes
Fixed Size		    2227784 bytes
Variable Size		  402653624 bytes
Database Buffers	  771751936 bytes
Redo Buffers		    9220096 bytes
Database mounted.
Database opened.
SQL> 





[oracle@steelDB backup]$ impdp IEBMS/IEBMS2019 directory=BACKUP DUMPFILE=IEBMS2019-20190516.DMP logfile=IEBMS-impdp-20160729.log





```


