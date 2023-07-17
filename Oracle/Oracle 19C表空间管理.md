### Oracle 19C表空间管理

#### 1、查看表空间信息

~~~
SQL> set linesize 999;
SQL> select con_id,tablespace_name,contents,logging,extent_management,allocation_type,segment_space_management from cdb_tablespaces order by 1;

    CON_ID TABLESPACE_NAME		  CONTENTS		LOGGING   EXTENT_MAN ALLOCATIO SEGMEN
---------- ------------------------------ --------------------- --------- ---------- --------- ------
	 1 SYSTEM			  PERMANENT		LOGGING   LOCAL      SYSTEM    MANUAL
	 1 NEWSCM_TEMP			  TEMPORARY		NOLOGGING LOCAL      UNIFORM   MANUAL
	 1 NEWSCM			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 1 SYSAUX			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 1 TEMP 			  TEMPORARY		NOLOGGING LOCAL      UNIFORM   MANUAL
	 1 UNDOTBS1			  UNDO			LOGGING   LOCAL      SYSTEM    MANUAL
	 1 USERS			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 3 SYSTEM			  PERMANENT		LOGGING   LOCAL      SYSTEM    MANUAL
	 3 USERS			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 3 SYSAUX			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 3 UNDOTBS1			  UNDO			LOGGING   LOCAL      SYSTEM    MANUAL
	 3 TEMP 			  TEMPORARY		NOLOGGING LOCAL      UNIFORM   MANUAL
	 4 USERS			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 4 TEMP 			  TEMPORARY		NOLOGGING LOCAL      UNIFORM   MANUAL
	 4 UNDOTBS1			  UNDO			LOGGING   LOCAL      SYSTEM    MANUAL
	 4 SYSAUX			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 4 SYSTEM			  PERMANENT		LOGGING   LOCAL      SYSTEM    MANUAL
	 5 SALESPLUS			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 5 TEMP 			  TEMPORARY		NOLOGGING LOCAL      UNIFORM   MANUAL
	 5 UNDOTBS1			  UNDO			LOGGING   LOCAL      SYSTEM    MANUAL
	 5 SYSAUX			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 5 EBANK			  PERMANENT		LOGGING   LOCAL      SYSTEM    AUTO
	 5 SYSTEM			  PERMANENT		LOGGING   LOCAL      SYSTEM    MANUAL

23 rows selected.

SQL> 

~~~

 

#### 1.2查看表空间文件信息

~~~
SQL> col tablespace_name for a10
SQL> col file_name for a60;

SQL> select con_id,tablespace_name,file_id,file_name from cdb_data_files order by 1;

    CON_ID TABLESPACE	 FILE_ID FILE_NAME
---------- ---------- ---------- ------------------------------------------------------------
	 1 SYSTEM	       1 /opt/oracle/oradata/ORCLCDB/system01.dbf
	 1 SYSAUX	       3 /opt/oracle/oradata/ORCLCDB/sysaux01.dbf
	 1 NEWSCM	      13 /opt/oracle/oradata/ORCLCDB/newscm_01.dbf
	 1 USERS	       7 /opt/oracle/oradata/ORCLCDB/users01.dbf
	 1 UNDOTBS1	       4 /opt/oracle/oradata/ORCLCDB/undotbs01.dbf
	 3 SYSTEM	       9 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/system01.dbf
	 3 SYSAUX	      10 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/sysaux01.dbf
	 3 UNDOTBS1	      11 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/undotbs01.dbf
	 3 USERS	      12 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/users01.dbf
	 4 SYSTEM	      20 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/system01.dbf
	 4 SYSAUX	      21 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/sysaux01.dbf
	 4 UNDOTBS1	      22 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/undotbs01.dbf
	 4 USERS	      23 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/user01.dbf
	 5 SYSTEM	      28 /opt/oracle/oradata/ORCLCDB/salesplus/system01.dbf
	 5 SYSAUX	      29 /opt/oracle/oradata/ORCLCDB/salesplus/sysaux01.dbf
	 5 EBANK	      32 /opt/oracle/oradata/ORCLCDB/salesplus/sebank_01.dbf
	 5 SALESPLUS	      31 /opt/oracle/oradata/ORCLCDB/salesplus/salesplus_01.dbf
	 5 UNDOTBS1	      30 /opt/oracle/oradata/ORCLCDB/salesplus/undotbs01.dbf

18 rows selected.

SQL> 
SQL> select con_id,tablespace_name,file_id,file_name from cdb_temp_files order by 1;

    CON_ID TABLESPACE	 FILE_ID FILE_NAME
---------- ---------- ---------- ------------------------------------------------------------
	 1 TEMP 	       1 /opt/oracle/oradata/ORCLCDB/temp01.dbf
	 1 NEWSCM_TEM	       4 /opt/oracle/oradata/ORCLCDB/newscm_temp.dbf
	   P

	 3 TEMP 	       3 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/temp01.dbf
	 4 TEMP 	       5 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/temp012023-03-30_06-17-
				 00-601-AM.dbf

	 5 TEMP 	       6 /opt/oracle/oradata/ORCLCDB/salesplus/temp012023-03-30_06-17
				 -00-601-AM.dbf


SQL>
~~~

### 二、表空间管理

#### 1、创建一个临时表空间组，并设置成默认表空间

~~~


SQL> set linesize 999;
SQL> select con_id, tablespace_name,file_id,file_name from cdb_data_files order by 1;

    CON_ID TABLESPACE_NAME		     FILE_ID FILE_NAME
---------- ------------------------------ ---------- ------------------------------------------------------------------------------------------
	 1 SYSTEM				   1 /opt/oracle/oradata/ORCLCDB/system01.dbf
	 1 SYSAUX				   3 /opt/oracle/oradata/ORCLCDB/sysaux01.dbf
	 1 NEWSCM				  13 /opt/oracle/oradata/ORCLCDB/newscm_01.dbf
	 1 USERS				   7 /opt/oracle/oradata/ORCLCDB/users01.dbf
	 1 UNDOTBS1				   4 /opt/oracle/oradata/ORCLCDB/undotbs01.dbf
	 3 SYSTEM				   9 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/system01.dbf
	 3 USERS				  12 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/users01.dbf
	 3 UNDOTBS1				  11 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/undotbs01.dbf
	 3 SYSAUX				  10 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/sysaux01.dbf
	 4 SYSTEM				  20 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/system01.dbf
	 4 SYSAUX				  21 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/sysaux01.dbf

    CON_ID TABLESPACE_NAME		     FILE_ID FILE_NAME
---------- ------------------------------ ---------- ------------------------------------------------------------------------------------------
	 4 UNDOTBS1				  22 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/undotbs01.dbf
	 4 USERS				  23 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/user01.dbf
	 5 SYSTEM				  28 /opt/oracle/oradata/ORCLCDB/salesplus/system01.dbf
	 5 SYSAUX				  29 /opt/oracle/oradata/ORCLCDB/salesplus/sysaux01.dbf
	 5 EBANK				  32 /opt/oracle/oradata/ORCLCDB/salesplus/sebank_01.dbf
	 5 SALESPLUS				  31 /opt/oracle/oradata/ORCLCDB/salesplus/salesplus_01.dbf
	 5 UNDOTBS1				  30 /opt/oracle/oradata/ORCLCDB/salesplus/undotbs01.dbf

18 rows selected.

SQL> 
SQL> select con_id, tablespace_name,file_id,file_name from cdb_temp_files order by 1;

    CON_ID TABLESPACE_NAME		     FILE_ID FILE_NAME
---------- ------------------------------ ---------- ------------------------------------------------------------------------------------------
	 1 TEMP 				   1 /opt/oracle/oradata/ORCLCDB/temp01.dbf
	 1 NEWSCM_TEMP				   4 /opt/oracle/oradata/ORCLCDB/newscm_temp.dbf
	 3 TEMP 				   3 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/temp01.dbf
	 4 TEMP 				   5 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/temp012023-03-30_06-17-00-601-AM.dbf
	 5 TEMP 				   6 /opt/oracle/oradata/ORCLCDB/salesplus/temp012023-03-30_06-17-00-601-AM.dbf

SQL> 




SQL> col PROPERTY_VALUE for a30;
SQL> SELECT property_name,property_value FROM database_properties where property_name='DEFAULT_TEMP_TABLESPACE';

PROPERTY_NAME		       PROPERTY_VALUE
------------------------------ ------------------------------
DEFAULT_TEMP_TABLESPACE        TEMP

SQL> 




CREATE TEMPORARY TABLESPACE temp1 
TEMPFILE '/opt/oracle/oradata/ORCLCDB/temp101.dbf' 
SIZE 200M
AUTOEXTEND ON
  5  TABLESPACE GROUP temp_grp;

Tablespace created.

CREATE TEMPORARY TABLESPACE temp2 
TEMPFILE '/opt/oracle/oradata/ORCLCDB/temp201.dbf' 
SIZE 200M
AUTOEXTEND ON
  5  TABLESPACE GROUP temp_grp;

Tablespace created.

SQL> select con_id, tablespace_name,file_id,file_name from cdb_temp_files order by 1;

    CON_ID TABLESPACE_NAME		     FILE_ID FILE_NAME
---------- ------------------------------ ---------- ------------------------------------------------------------------------------------------
	 1 NEWSCM_TEMP				   4 /opt/oracle/oradata/ORCLCDB/newscm_temp.dbf
	 1 TEMP2				   8 /opt/oracle/oradata/ORCLCDB/temp201.dbf
	 1 TEMP1				   7 /opt/oracle/oradata/ORCLCDB/temp101.dbf
	 1 TEMP 				   1 /opt/oracle/oradata/ORCLCDB/temp01.dbf
	 3 TEMP 				   3 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/temp01.dbf
	 4 TEMP 				   5 /opt/oracle/oradata/ORCLCDB/ORCLPDB2/temp012023-03-30_06-17-00-601-AM.dbf
	 5 TEMP 				   6 /opt/oracle/oradata/ORCLCDB/salesplus/temp012023-03-30_06-17-00-601-AM.dbf

7 rows selected.

SQL> 



SQL> ALTER DATABASE DEFAULT TEMPORARY TABLESPACE temp_grp;

Database altered.

SQL>  SELECT property_name,property_value FROM database_properties where property_name='DEFAULT_TEMP_TABLESPACE';

PROPERTY_NAME		       PROPERTY_VALUE
-----

------------------------- ------------------------------
DEFAULT_TEMP_TABLESPACE        TEMP_GRP

SQL> 

~~~

#### 2.2 永久表空间

~~~~


SQL> conn sys/sys@orclpdb1 as sysdba;
Connected.
CREATE TABLESPACE myts 
DATAFILE '/opt/oracle/oradata/ORCLCDB/ORCLPDB1/myts.dbf' 
SIZE 20M
AUTOEXTEND ON;
  2    3    4  
Tablespace created.





SQL> set linesize 999;
SQL> select  tablespace_name,file_id,file_name from dba_data_files order by 1;

TABLESPACE_NAME 		  FILE_ID FILE_NAME
------------------------------ ---------- ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
MYTS				       13 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/myts.dbf
SYSAUX				       10 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/sysaux01.dbf
SYSTEM					9 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/system01.dbf
UNDOTBS1			       11 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/undotbs01.dbf
USERS				       12 /opt/oracle/oradata/ORCLCDB/ORCLPDB1/users01.dbf

SQL> 



SQL>  ALTER DATABASE DEFAULT  TABLESPACE myts;

Database altered.



SQL> col PROPERTY_NAME for a30;
SQL> col PROPERTY_VALUE for a30;
SQL> SELECT property_name,property_value FROM database_properties where property_name='DEFAULT_PERMANENT_TABLESPACE';

PROPERTY_NAME		       PROPERTY_VALUE
------------------------------ ------------------------------
DEFAULT_PERMANENT_TABLESPACE   MYTS

SQL> 


~~~~



#### 2.3 新建用户给定默认表空间使用



~~~


Copyright (c) 1982, 2019, Oracle.  All rights reserved.

SQL> conn sys/sys@orclpdb1 as sysdba;
Connected.
GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE TO damo 
  2    IDENTIFIED BY oracle;

Grant succeeded.

SQL> exit
Disconnected from Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production
Version 19.3.0.0.0
[oracle@760d698d1d85 admin]$ sqlplus /nolog

SQL*Plus: Release 19.0.0.0.0 - Production on Sat Jul 15 02:57:16 2023
Version 19.3.0.0.0

Copyright (c) 1982, 2019, Oracle.  All rights reserved.

SQL> conn damo/oracle@orclpdb1 
Connected.
SQL> create table test(id int);

Table created.


SQL> insert into  test values(1);

1 row created.

SQL> insert into  test values(1);

1 row created.

SQL> select * from test;

	ID
----------
	 1
	 1
	 1

SQL> 



~~~

#### 2.4 新建一个bitfile表空间：





~~~~
SQL> conn sys/sys@orclpdb1 as sysdba;
Connected.
SQL>  CREATE BIGFILE TABLESPACE bigtbs 
DATAFILE '/opt/oracle/oradata/ORCLCDB/ORCLPDB1/bigtbs.dbf' SIZE 100M
AUTOEXTEND ON NEXT 100M MAXSIZE UNLIMITED;

Tablespace created

SQL>
SQL> set linesize 999;
SQL> /

TABLESPACE FILE_NAME						 USED_MB     MAX_MB
---------- -------------------------------------------------- ---------- ----------
BIGTBS	   /opt/oracle/oradata/ORCLCDB/ORCLPDB1/bigtbs.dbf	     100   33554432

SQL> 

BIGTBS	   /opt/oracle/oradata/ORCLCDB/ORCLPDB1/bigtbs.dbf	     100   33554432

SQL> SELECT D.TABLESPACE_NAME, D.FILE_NAME,D.BYTES/1024/1024 USED_MB,
       D.MAXBYTES/1024/1024 MAX_MB
  3  FROM DBA_DATA_FILES D;



SELECT D.TABLESPACE_NAME, D.FILE_NAME,D.BYTES/1024/1024 USED_MB,
       D.MAXBYTES/1024/1024 MAX_MB
FROM DBA_temp_FILES D JOIN DBA_TABLESPACES T 
ON D.TABLESPACE_NAME = T.TABLESPACE_NAME
WHERE T.BIGFILE = 'YES';




~~~~

