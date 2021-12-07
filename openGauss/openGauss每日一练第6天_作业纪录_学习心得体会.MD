# openGauss每日一练第6天_作业纪录_学习心得体会

  **心得体会：**  今天学习openGauss创建模式、修改模式属性和删除模式、模式是一组数据库对象的集合，主要用于控制对数据库对象的访问,注意如果删除指定schema，需要加CASCADE，删除schema存在对象。

## 课后作业

1、创建一个名为tpcds的模式


```
tank=# create  schema tpcds;
CREATE SCHEMA
tank=# 


```

2.创建一个用户tim, 并将tpcds的owner修改为tim，且修改owner前后分别使用\dn+查看模式信息

```
tank=# create  schema tpcds;
CREATE SCHEMA
tank=# CREATE USER tim PASSWORD 'tank@123';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE
tank=# ALTER SCHEMA tpcds OWNER TO tim;  
ALTER SCHEMA
tank=# \dn+
                              List of schemas
    Name     | Owner | Access privileges |           Description            
-------------+-------+-------------------+----------------------------------
 cstore      | omm   |                   | reserved schema for DELTA tables
 dbe_perf    | omm   |                   | dbe_perf schema
 ds_new      | jack  |                   | 
 jack        | jack  |                   | 
 pkg_service | omm   |                   | pkg_service schema
 public      | omm   | omm=UC/omm       +| standard public schema
             |       | =U/omm            | 
 snapshot    | omm   |                   | snapshot schema
 tim         | tim   |                   | 
 tpcds       | tim   |                   | 
(9 rows)

tank=# 

```

3.重命名tpcds为tpcds1

```

tank=# alter schema tpcds rename to tpcds1;
ALTER SCHEMA
tank=# \dn
   List of schemas
    Name     | Owner 
-------------+-------
 cstore      | omm
 dbe_perf    | omm
 ds_new      | jack
 jack        | jack
 pkg_service | omm
 public      | omm
 snapshot    | omm
 tim         | tim
 tpcds1      | tim
(9 rows)

tank=# 

```

4.在模式tpcds1中建表customer、插入记录和查询记录


```
tank=# create table tpcds1.customer(id int, name char(30));
CREATE TABLE
tank=# insert into tpcds1.customer values(1 ,'tank');
INSERT 0 1
tank=# select * from  tpcds1.customer;
tank=#  id |              name              
----+--------------------------------
  1 | tank                          
(1 row)

tank=#


```


5.删除模式tpcds1

```
tank=# drop schema tpcds1;
ERROR:  cannot drop schema tpcds1 because other objects depend on it
DETAIL:  table tpcds1.customer depends on schema tpcds1
HINT:  Use DROP ... CASCADE to drop the dependent objects too.
tank=# drop schema tpcds1 CASCADE ;
NOTICE:  drop cascades to table tpcds1.customer
DROP SCHEMA
tank=# \dn
   List of schemas
    Name     | Owner 
-------------+-------
 cstore      | omm
 dbe_perf    | omm
 ds_new      | jack
 jack        | jack
 pkg_service | omm
 public      | omm
 snapshot    | omm
 tim         | tim
(8 rows)

tank=# 
```








