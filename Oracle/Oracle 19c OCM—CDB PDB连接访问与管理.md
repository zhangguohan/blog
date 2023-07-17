## Oracle 19C 容器数据库的管理

### 一、查看是否为容器数据库

~~~sql
SQL> select name,cdb from v$database;

NAME	  CDB
--------- ---
ORCLCDB   YES

SQL> 

~~~



### 二、查看当前连接的容器

~~~
SQL> show con_name;

CON_NAME
------------------------------
CDB$ROOT
SQL> show con_id;

CON_ID
------------------------------
1
SQL> 
~~~



### 三、查看可拔插数据库PDB状态

~~~
SQL> desc v$pdbs;
 Name					   Null?    Type
 ----------------------------------------- -------- ----------------------------
 CON_ID 					    NUMBER
SQL> select name,con_id,open_mode from v$pdbs;

NAME				   CON_ID OPEN_MODE
------------------------------ ---------- ----------
PDB$SEED				2 READ ONLY
ORCLPDB1				3 READ WRITE

SQL> 


SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> 


~~~



### 四、连接到可插拔数据库pdb1

#### 1、本地连接

~~~
SQL> alter session set container=orclpdb1
  2  ;

Session altered.

SQL> show con_name;

CON_NAME
------------------------------
ORCLPDB1
SQL> 
SQL> show pdbs

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 3 ORCLPDB1			  READ WRITE NO
SQL> 
SQL> show con_name

CON_NAME
------------------------------
ORCLPDB1
SQL> 

~~~



#### 2、网络连接



~~~~
[oracle@b84d833e11d3 ~]$ more /opt/oracle/product/19c/dbhome_1/network/admin/tnsnames.ora 
ORCLCDB=localhost:1521/ORCLCDB
ORCLPDB1= 
  (DESCRIPTION = 
    (ADDRESS = (PROTOCOL = TCP)(HOST = 0.0.0.0)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = ORCLPDB1)
    )
  )
[oracle@b84d833e11d3 ~]$ 




SQL> conn sys/gzgi_2005@orclpdb1 as sysdba;
Connected.
SQL> show pds
SP2-0158: unknown SHOW option "pds"
SQL> show pdbs

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 3 ORCLPDB1			  READ WRITE NO
SQL> 

SQL> show con_name;

CON_NAME
------------------------------
ORCLPDB1


~~~~



#### 3、使用ezconnect 方式

~~~
[oracle@k8snode1:/home/oracle]$ sqlplus sys/gzgi_2005@108.88.3.64:1521/orclpdb1 as sysdba;

SQL*Plus: Release 19.0.0.0.0 - Production on Wed Jul 12 15:45:59 2023
Version 19.14.0.0.0

Copyright (c) 1982, 2021, Oracle.  All rights reserved.


Connected to:
Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production
Version 19.3.0.0.0

SQL> 

~~~

#### 4、切换回根容器

~~~
Connected.
SQL> alter session set container=cdb$root;

Session altered.

SQL> show pdbs

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> 
~~~

### 五、打开关闭 pdb

~~~
SQL> conn / as sysdba;
Connected.
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO

SQL> show con_name;

CON_NAME
------------------------------
CDB$ROOT
SQL> shutdown immediate;
Database closed.
Database dismounted.
ORACLE instance shut down.
SQL> 

SQL> alter database mount;

Database altered.

SQL> select instance_name,status from v$instance;

INSTANCE_NAME	 STATUS
---------------- ------------
ORCLCDB 	 MOUNTED

SQL> col name for a10;
SQL> select name,con_id,open_mode from v$pdbs;

NAME	       CON_ID OPEN_MODE
---------- ---------- ----------
PDB$SEED	    2 MOUNTED
ORCLPDB1	    3 MOUNTED


SQL> alter database open;

Database altered.

SQL> select instance_name,status from v$instance;

INSTANCE_NAME	 STATUS
---------------- ------------
ORCLCDB 	 OPEN

SQL> select name,con_id,open_mode from v$pdbs;

NAME	       CON_ID OPEN_MODE
---------- ---------- ----------
PDB$SEED	    2 READ ONLY
ORCLPDB1	    3 READ WRITE

## 关闭pdb
1、在pdbs中关闭pdbs数据库
SQL> conn sys/gzgi_2005@ORCLPDB1 as sysdba;
Connected.
SQL> show con_name;

CON_NAME
------------------------------
ORCLPDB1
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 3 ORCLPDB1			  READ WRITE NO
SQL> shutdown immediate;
Pluggable Database closed.
SQL> 
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 3 ORCLPDB1			  MOUNTED
SQL> 


2、在根容器中操作pdbs

SQL> conn / as sysdba;
Connected.
SQL> show con_name;

CON_NAME
------------------------------
CDB$ROOT
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  MOUNTED


SQL> alter pluggable database orclpdb1 open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> 



SQL> alter pluggable database orclpdb1 open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> alter pluggable database orclpdb1 close immediate;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  MOUNTED
SQL>

SQL> alter pluggable database orclpdb1 close immediate;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  MOUNTED
SQL> alter pluggable database all open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> 

~~~



### 六、自动启动pdb数据库状态修改、

~~~
SQL> alter pluggable database orclpdb1 save state;   //保存现在状态为默认启动项目

Pluggable database altered.

SQL> 


SQL> col con_name for a20;
SQL> col instance_name for a20;
SQL> col state for a20;


SQL> ⁢select con_name,instance_name,state from cdb_pdb_saved_states;

CON_NAME	     INSTANCE_NAME	  STATE
-------------------- -------------------- --------------------
ORCLPDB1	     ORCLCDB		  OPEN

SQL> 

~~~

### 七、查看数据文件

~~~
、

SQL> conn / as sysdba;
Connected.
SQL> show con_name;

CON_NAME
------------------------------
CDB$ROOT
SQL> col FILE_NAME for a50;


SQL> select con_id,file_id,file_name,tablespace_name from cdb_data_files;

    CON_ID    FILE_ID FILE_NAME 					 TABLESPACE_NAME
---------- ---------- -------------------------------------------------- ------------------------------
	 3	    9 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/system01.dbf  SYSTEM
	 3	   10 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/sysaux01.dbf  SYSAUX
	 3	   11 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/undotbs01.dbf UNDOTBS1
	 3	   12 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/users01.dbf	 USERS
	 1	    1 /opt/oracle/oradata/ORCLCDB/system01.dbf		 SYSTEM
	 1	    3 /opt/oracle/oradata/ORCLCDB/sysaux01.dbf		 SYSAUX
	 1	    4 /opt/oracle/oradata/ORCLCDB/undotbs01.dbf 	 UNDOTBS1
	 1	    7 /opt/oracle/oradata/ORCLCDB/users01.dbf		 USERS
	 1	   13 /opt/oracle/oradata/ORCLCDB/newscm_01.dbf 	 NEWSCM

9 rows selected.


### 查看临时文件
SQL> select con_id,file_id,file_name,tablespace_name from cdb_temp_files;

    CON_ID    FILE_ID FILE_NAME 					 TABLESPACE_NAME
---------- ---------- -------------------------------------------------- ------------------------------
	 3	    3 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/temp01.dbf	 TEMP
	 1	    4 /opt/oracle/oradata/ORCLCDB/newscm_temp.dbf	 NEWSCM_TEMP
	 1	    1 /opt/oracle/oradata/ORCLCDB/temp01.dbf		 TEMP

SQL> 


### 查当前连接数据库的文件
SQL> select file_id,file_name,tablespace_name from dba_data_files;

   FILE_ID FILE_NAME					      TABLESPACE_NAME
---------- -------------------------------------------------- ------------------------------
	 1 /opt/oracle/oradata/ORCLCDB/system01.dbf	      SYSTEM
	 3 /opt/oracle/oradata/ORCLCDB/sysaux01.dbf	      SYSAUX
	 4 /opt/oracle/oradata/ORCLCDB/undotbs01.dbf	      UNDOTBS1
	 7 /opt/oracle/oradata/ORCLCDB/users01.dbf	      USERS
	13 /opt/oracle/oradata/ORCLCDB/newscm_01.dbf	      NEWSCM

SQL> select file_id,file_name,tablespace_name from dba_temp_files;

   FILE_ID FILE_NAME					      TABLESPACE_NAME
---------- -------------------------------------------------- ------------------------------
	 4 /opt/oracle/oradata/ORCLCDB/newscm_temp.dbf	      NEWSCM_TEMP
	 1 /opt/oracle/oradata/ORCLCDB/temp01.dbf	      TEMP

SQL> 



## 查联机重做日志文件

SQL> col MEMBER for a50;
SQL> select * from v$logfile;

    GROUP# STATUS  TYPE 					      MEMBER						 IS_	 CON_ID
---------- ------- -------------------------------------------------- -------------------------------------------------- --- ----------
	 3	   ONLINE					      /opt/oracle/oradata/ORCLCDB/redo03.log		 NO	      0
	 2	   ONLINE					      /opt/oracle/oradata/ORCLCDB/redo02.log		 NO	      0
	 1	   ONLINE					      /opt/oracle/oradata/ORCLCDB/redo01.log		 NO	      0

SQL> 


## 查看控制文件
SQL> select * from v$controlfile;

STATUS	NAME						   IS_ BLOCK_SIZE FILE_SIZE_BLKS     CON_ID
------- -------------------------------------------------- --- ---------- -------------- ----------
	/opt/oracle/oradata/ORCLCDB/control01.ctl	   NO	    16384	    1142	  0

SQL> 

~~~

### 八、用户管理

   #### 8.1  创建公用用户

~~~

## 创建公用用户：
SQL> show parameter prefix

NAME				     TYPE						VALUE
------------------------------------ -------------------------------------------------- ------------------------------
common_user_prefix		     string						C##
os_authent_prefix		     string						ops$
private_temp_table_prefix	     string						ORA$PTT_
SQL> 

SQL> create user c##user01 identified by oracle2023;

User created.

SQL> select username,common,con_id from cdb_users where username in ('SYS','HR','C##USER01');

USERNAME					   COM	   CON_ID
-------------------------------------------------- --- ----------
SYS						   YES		1
C##USER01					   YES		1
SYS						   YES		3
C##USER01					   YES		3

SQL> 

为通用用户按权
SQL> conn / as sysdba;
Connected.
SQL> grant connect to c##user01 container=all;

Grant succeeded.

SQL> conn c##user01/oracle2023;
Connected.
SQL> 



~~~

#### 8.2创建一个本地用户



~~~~


SQL> conn sys/gzgi_2005@orclpdb1 as sysdba;
Connected.
SQL> show con_name;

CON_NAME
------------------------------
ORCLPDB1
SQL> create user user02 identified by oracle2023;

User created.

SQL> col USERNAME for a50;
SQL> select username,common,con_id from cdb_users where username in ('SYS','HR','C##USER01','USER02');

USERNAME					   COM	   CON_ID
-------------------------------------------------- --- ----------
SYS						   YES		3
C##USER01					   YES		3
USER02						   NO		3

SQL> 
为本用户授权：

SQL> conn sys/gzgi_2005@orclpdb1 as sysdba;
Connected.
SQL> grant connect to user02 container=current;

Grant succeeded.

SQL> conn user02/oracle2023@orclpdb1
Connected.
SQL> 



~~~~

