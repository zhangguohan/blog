
### Oracleo数据库Rmanl备份配置

#### 3.1.4 ARCHIVELOG模式之间的切换

   一旦将数据库配置为在ARCHIEVLOG模式下运行，则可以非常容易在实数据库在NOARCHIVELOG模式和ARCHIVELOG模式之间的转换。为了将数据库置于ARCHIVELOGS模式必须首先使用如下一种命令以一致的方式关闭数据库：shutdown 命令，shutdown immediate命令或shutdown transaction 命令。一旦正常闭了数据库，就可执行start mount 命令来加载这个数据库；然后执行alter database archivelog将数据库置于ARCHIVELOG模式，然后使用alter database open打开数据库。
  
   如果希望数据库跳出ARCHIVELOG模式，则可用采用相反的过程，首先关闭数据库，然后startup mount加载数据库，然后执行alter database noarchivelog置于NOARCHIVELOG模式，然后打开数据库，alter database open

#### 3.1.5 将数据库置于ARCHIVELOG模式

##### 步骤1、查看现有FRA配置

```
SQL> show parameter db_recovery_file;

NAME				     TYPE	 VALUE
------------------------------------ ----------- ------------------------------
db_recovery_file_dest		     string	 /u01/app/oracle/fast_recovery_
						 area
db_recovery_file_dest_size	     big integer 4122M
SQL> 

默认已经配置

```
##### 步骤2、新建归档日志保存目录
```
mdir -p /u01/backup/archivelogs/
```
现在，定义两个归档日志目标目录，其中一个是FRA，设置数据库参数文件
LOG_ARCHIVE_DEST_1参数，让它指向预先定的文件系统，该文件系统将是第一个归档日志目录，由于要配置LOG_ARCHIVE_DEST_1使用1=RA，因此需要使用USE_DB_RECOVERY_FILE_DEST参数来设置 LOG_ARCHIVE_DET_10参数以指向FRA，使用show parameter命令来验证设置是否正确。

##### 步骤3、设置数据库归档目录
~~~
SQL> alter system set log_archive_dest_1='location=/u01/backup/archivelogs/';

System altered.

SQL> alter system set log_archive_dest_10='location=USE_DB_RECOVERY_FILE_DEST';

System altered.
~~~
##### 步骤4、将数据库置与ARCHIVELOG模式
~~~
SQL> shutdown immediate;


Database dismounted.
ORACLE instance shut down.
SQL> startup mount;
ORACLE instance started.

Total System Global Area 1185853440 bytes
Fixed Size		    2227784 bytes
Variable Size		  452985272 bytes
Database Buffers	  721420288 bytes
Redo Buffers		    9220096 bytes
Database mounted.

SQL> alter database archivelog;

Database altered.

SQL> alter database open;

Database altered.
~~~~

##### 步骤5、查看数据库配置参数

~~~

SQL> show parameter;
....

log_archive_dest		     string
log_archive_dest_1		     string	 location=/u01/backup/archivelo
						 gs/
log_archive_dest_10		     string	 location=USE_DB_RECOVERY_FILE_
						 DEST
log_archive_dest_11		     string
~~~
 

#### 3.1.6 将数据库置于NOARCHIVELOG模式
~~~

SQL> startup mount;
ORACLE instance started.

Total System Global Area 1185853440 bytes
Fixed Size		    2227784 bytes
Variable Size		  452985272 bytes
Database Buffers	  721420288 bytes
Redo Buffers		    9220096 bytes
Database mounted.

SQL> alter database noarchivelog;

Database altered.

SQL> alter database open;

Database altered.
~~~