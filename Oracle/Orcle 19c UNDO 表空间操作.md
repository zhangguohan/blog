### Orcle 19c UNDO 表空间操作

#### 一、新建测试表

~~~~
[oracle@760d698d1d85 ~]$ sqlplus sys/sys@orclpdb1 as sysdba;

SQL*Plus: Release 19.0.0.0.0 - Production on Sat Jul 15 07:55:42 2023
Version 19.3.0.0.0

Copyright (c) 1982, 2019, Oracle.  All rights reserved.


Connected to:
Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production
Version 19.3.0.0.0

SQL> create table test(id number,name char(20));

Table created.

SQL> insert into test values(1,'Oracle');

1 row created.

SQL> insert into test values(2,'DBA');

1 row created.

SQL> commit;

Commit complete.

SQL> 

~~~~



#### 1.2 模拟长时间查询

~~~
### session 1

SQL> select current_scn from v$database;

CURRENT_SCN
-----------
    2288674

SQL> 

SQL>  variable rfc refcursor;
SQL> execute open :rfc for select * from test;

PL/SQL procedure successfully completed.

SQL> 




### session 2

SQL> select * from test;

	ID NAME
---------- --------------------
	 1 Oracle
	 2 DBA

SQL> update test set name = 'OCM' where id=2;

1 row updated.

SQL> select * from test;

	ID NAME
---------- --------------------
	 1 Oracle
	 2 OCM

SQL> 

 


###  serssion 3


[oracle@760d698d1d85 ~]$ sqlplus sys/sys@orclpdb1 as sysdba;

SQL*Plus: Release 19.0.0.0.0 - Production on Sat Jul 15 08:31:47 2023
Version 19.3.0.0.0

Copyright (c) 1982, 2019, Oracle.  All rights reserved.


Connected to:
Oracle Database 19c Enterprise Edition Release 19.0.0.0.0 - Production
Version 19.3.0.0.0

SQL> select * from test;

	ID NAME
---------- --------------------
	 1 Oracle
	 2 DBA

SQL> 

#session 2 commit 后


SQL> select * from test;

	ID NAME
---------- --------------------
	 1 Oracle
	 2 OCM

SQL> 



＃＃＃ 看查要ｓｅｓｓｉｏｎ　１


SQL> print :rfc

	ID NAME
---------- --------------------
	 1 Oracle
	 2 DBA

SQL> 


~~~

