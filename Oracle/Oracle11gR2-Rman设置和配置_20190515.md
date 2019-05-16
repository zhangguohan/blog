
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
LOG_ARCHIVE_DEST_1参数，让
 