# openGauss每日一练第3天_作业纪录_学习心得体会

  **心得体会：**  今天学习了数据库的创建、修改数据库连接数，重命名数据库，删除数据库、连接指向数据库。

## 课后作业

1、分别创建名为tpcc1和tpcc2的数据库
```
omm=# create database tpcc1;
CREATE DATABASE
omm=# create database tpcc2;
CREATE DATABASE



```

2、将tpcc1数据库重命名为tpcc10


```
omm=# alter database tpcc1 rename to tpcc10;
ALTER DATABASE
omm=# 



```

3.分别使用\l和\l+两个元命令查看数据库信息

```


omm=# \l
                              List of databases
   Name    | Owner | Encoding |   Collate   |    Ctype    | Access privileges 
-----------+-------+----------+-------------+-------------+-------------------
 music2    | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 omm       | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 postgres  | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 template0 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +
           |       |          |             |             | omm=CTc/omm
 template1 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +
           |       |          |             |             | omm=CTc/omm
 tpcc10    | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 tpcc2     | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
(7 rows)

omm=# \l+
                                                               List of databases
   Name    | Owner | Encoding |   Collate   |    Ctype    | Access privileges | Size  | Tablespace |                Descriptio
n                 
-----------+-------+----------+-------------+-------------+-------------------+-------+------------+--------------------------
------------------
           |       |          |             |             | omm=CTc/omm       |       |            | 
 template1 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +| 10 MB | pg_default | unmodifiable empty databa
se
           |       |          |             |             | omm=CTc/omm       |       |            | 
 tpcc10    | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 |                   | 10 MB | pg_default | 
 tpcc2     | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 |                   | 10 MB | pg_default | 
(7 rows)

 music2    | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 |                   | 10 MB | pg_default | 
 omm       | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 |                   | 10 MB | pg_default | 
 postgres  | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 |                   | 10 MB | pg_default | default administrative co
nnection database
 template0 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +| 10 MB | pg_default | default template for new 
databases
omm=# 



```

4.在数据库tpcc2中创建customer表，字段自定义


```
tpcc2=# \c tpcc2
Non-SSL connection (SSL connection is recommended when requiring high-security)
You are now connected to database "tpcc2" as user "omm".
tpcc2=# create table customer(id integer,name char(20));
CREATE TABLE
tpcc2=# \dt customer
                          List of relations
 Schema |   Name   | Type  | Owner |             Storage              
--------+----------+-------+-------+----------------------------------
 public | customer | table | omm   | {orientation=row,compression=no}
(1 row)

tpcc2=# 



```


5.删除新创建的数据库

```

omm=# drop database tpcc10;
DROP DATABASE
omm=# drop database tpcc2;
DROP DATABASE
omm=# \l
                              List of databases
   Name    | Owner | Encoding |   Collate   |    Ctype    | Access privileges 
-----------+-------+----------+-------------+-------------+-------------------
 music2    | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 omm       | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 postgres  | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | 
 template0 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +
           |       |          |             |             | omm=CTc/omm
 template1 | omm   | UTF8     | en_US.UTF-8 | en_US.UTF-8 | =c/omm           +
           |       |          |             |             | omm=CTc/omm
(5 rows)

omm=# 

```


6.退出gsql程序

```


omm=# \q
omm@modb:~$ 

```







