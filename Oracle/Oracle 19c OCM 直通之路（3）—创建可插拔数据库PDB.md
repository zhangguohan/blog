# Oracle 19c OCM 直通之路（3）—创建可插拔数据库PDB



### 一、案例需求新建一个 orclpdb2

#### 1.1、查看pdb状态

~~~~
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> 
SQL> conn / as sysdba;
Connected.
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
SQL> select file_name from cdb_data_files;

FILE_NAME
--------------------------------------------------------------------------------
/opt/oracle/oradata/ORCLCDB/ORCLPDB1/system01.dbf
/opt/oracle/oradata/ORCLCDB/ORCLPDB1/sysaux01.dbf
/opt/oracle/oradata/ORCLCDB/ORCLPDB1/undotbs01.dbf
/opt/oracle/oradata/ORCLCDB/ORCLPDB1/users01.dbf
/opt/oracle/oradata/ORCLCDB/system01.dbf
/opt/oracle/oradata/ORCLCDB/sysaux01.dbf
/opt/oracle/oradata/ORCLCDB/undotbs01.dbf
/opt/oracle/oradata/ORCLCDB/users01.dbf
/opt/oracle/oradata/ORCLCDB/newscm_01.dbf

9 rows selected.

SQL> alter session set container=pdb$seed;

Session altered.

SQL> select file_name from cdb_data_files;

FILE_NAME
--------------------------------------------------------------------------------
/opt/oracle/oradata/ORCLCDB/pdbseed/system01.dbf
/opt/oracle/oradata/ORCLCDB/pdbseed/sysaux01.dbf
/opt/oracle/oradata/ORCLCDB/pdbseed/undotbs01.dbf

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
	 3 ORCLPDB1			  READ WRITE NO
SQL> 
~~~

~~~~

#### 1.2、新建一个 orclpdb2

~~~


## 新建一个ORCLPDB2目录
[oracle@b84d833e11d3 ORCLCDB]$ mkdir ORCLPDB2        
[oracle@b84d833e11d3 ORCLCDB]$ 


## 使用file_name_convert方式新建orclpdb2

SQL> create pluggable database orclpdb2
  2  admin user pdb2admin identified by oracle roles=(connect)
  3  file_name_convert=('/opt/oracle/oradata/ORCLCDB/pdbseed/','/opt/oracle/oradata/ORCLCDB/ORCLPDB2/');

Pluggable database created.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  MOUNTED
SQL> alter pluggable database orclpdb2 open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
SQL> 


## 修改TNS文件

[oracle@b84d833e11d3 admin]$ more tnsnames.ora 
ORCLCDB=localhost:1521/ORCLCDB
ORCLPDB1= 
  (DESCRIPTION = 
    (ADDRESS = (PROTOCOL = TCP)(HOST = 0.0.0.0)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = ORCLPDB1)
    )
  )


### 以下为orclpdb2
ORCLPDB2=
  (DESCRIPTION =
    (ADDRESS = (PROTOCOL = TCP)(HOST = 0.0.0.0)(PORT = 1521))
    (CONNECT_DATA =
      (SERVER = DEDICATED)
      (SERVICE_NAME = ORCLPDB2)
    )
  )


SQL> conn sys/gzgi_2005@orclpdb2 as sysdba;
Connected.
SQL> show pdb
SP2-0158: unknown SHOW option "pdb"
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 5 ORCLPDB2			  READ WRITE NO
SQL> select file_name from cdb_data_files;

FILE_NAME
--------------------------------------------------------------------------------
/opt/oracle/oradata/ORCLCDB/ORCLPDB2/system01.dbf
/opt/oracle/oradata/ORCLCDB/ORCLPDB2/sysaux01.dbf
/opt/oracle/oradata/ORCLCDB/ORCLPDB2/undotbs01.dbf

SQL> 


~~~

  

#### 1.3、创建一个用户表空间表并设置为默认表空间

~~~
SQL> conn sys/gzgi_2005@orclpdb2 as sysdba;
Connected.

SQL> create tablespace users datafile '/opt/oracle/oradata/ORCLCDB/ORCLPDB2/user01.dbf' size 50m;

Tablespace created.

SQL> alter pluggable database default tablespace users;

Pluggable database altered.

SQL> 

~~~



#### 1.4 克隆本地的PDB

~~~
SQL> conn / as sysdba;
Connected.
SQL> show pdbs

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
SQL> alter pluggable database orclpdb2 close immediate;

Pluggable database altered.

SQL> alter pluggable database orclpdb2 open read only;

Pluggable database altered.

SQL> show pdbs

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ ONLY  NO
SQL>  ! mkdir /opt/oracle/oradata/ORCLCDB/ORCLPDB3


SQL> create pluggable database orclpdb3 from orclpdb2 
  2  file_name_convert=('/opt/oracle/oradata/ORCLCDB/ORCLPDB2/','/opt/oracle/oradata/ORCLCDB/ORCLPDB3/');

Pluggable database created.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ ONLY  NO
	 5 ORCLPDB3			  MOUNTED
SQL> alter pluggable database orclpdb2 close immediate;

Pluggable database altered.


SQL> alter pluggable database all open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
	 5 ORCLPDB3			  READ WRITE NO
SQL> 

~~~

#### 1.5、删除一个PDB数据库

~~~
SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
	 5 ORCLPDB3			  READ WRITE NO
SQL> alter pluggable database orclpdb3 close immediate;

Pluggable database altered.

SQL> drop pluggable database orclpdb3 including datafiles;

Pluggable database dropped.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
SQL> 



~~~

#### 1.6 可拔插件数据库的使用

~~~
SQL>  CREATE PLUGGABLE DATABASE salesplus 
                ADMIN USER salesplus IDENTIFIED BY salesplus
   FILE_NAME_CONVERT = ('pdbseed','salesplus');

Pluggable database created.


SQL> alter pluggable database salesplus open;

Pluggable database altered.

SQL> show pdbs;

    CON_ID CON_NAME			  OPEN MODE  RESTRICTED
---------- ------------------------------ ---------- ----------
	 2 PDB$SEED			  READ ONLY  NO
	 3 ORCLPDB1			  READ WRITE NO
	 4 ORCLPDB2			  READ WRITE NO
	 5 SALESPLUS			  READ WRITE NO


SQL> alter session set container=salesplus;

Session altered.

SQL> GRANT CONNECT, DBA TO salesplus;

Grant succeeded.

SQL> 


SQL>  create tablespace  salesplus
logging 
datafile '/opt/oracle/oradata/ORCLCDB/salesplus/salesplus_01.dbf' 
size 200m 
autoextend on 
next 200m maxsize 25480m  
extent management local;

SQL> alter pluggable database default tablespace salesplus;

create tablespace  ebank
logging 
datafile '/opt/oracle/oradata/ORCLCDB/salesplus/sebank_01.dbf' 
size 200m 
autoextend on 
next 200m maxsize 25480m  
extent management local;

SQL> conn / as sysdba;
Connected.
SQL> alter session set container=salesplus;

Session altered.

SQL> SELECT * FROM DBA_DIRECTORIES WHERE directory_name = 'BACKUP';

OWNER
--------------------------------------------------------------------------------
DIRECTORY_NAME
--------------------------------------------------------------------------------
DIRECTORY_PATH
--------------------------------------------------------------------------------
ORIGIN_CON_ID
-------------
SYS
BACKUP
/opt/oracle/ordata/backup
	    5


SQL> DROP DIRECTORY backup;

Directory dropped.

SQL> CREATE DIRECTORY backup AS '/opt/oracle/oradata/backup';

Directory created.
SQL>  GRANT READ,WRITE ON DIRECTORY backup TO salesplus;


SQL> exit
Disconnected from Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production


Version 19.3.0.0.0
[oracle@b84d833e11d3 backup]$ impdp salesplus/salesplus@salesplus directory=backup  dumpfile=SALESPLUS202307.DMP logfile=imp.log 

~~~

