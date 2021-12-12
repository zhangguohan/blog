# openGauss每日一练第7天_作业纪录_学习心得体会

  **心得体会：**  今天学习openGauss表空间管理、表空间用于管理数据对象，与磁盘上的一个目录对应。

## 课后作业

1、创建表空间，表空间tspc1使用相对路径指定所在目录，表空间tspc2指定owner为Lucy


```
1.1、新建一个tspc1 表空间

omm=# CREATE TABLESPACE tspc1 RELATIVE LOCATION 'tablespace/tspc1_1';
CREATE TABLESPACE
1.2、 新建一个Lucy 用户
omm=# CREATE ROLE Lucy IDENTIFIED BY 'Lucy@789';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
omm=# CREATE ROLE

1.3、新建一个tspc2 表空间
omm=# CREATE TABLESPACE tspc2 RELATIVE LOCATION 'tablespace/tspc2_1';
CREATE TABLESPACE

1.4、将表空间tspc2指定owner为Lucy

omm=# ALTER TABLESPACE tspc2  OWNER TO Lucy;
ALTER TABLESPACE

1.5 查看表空间属性
omm=# \db
           List of tablespaces
    Name    | Owner |      Location      
------------+-------+--------------------
 pg_default | omm   | 
 pg_global  | omm   | 
 tspc1      | omm   | tablespace/tspc1_1
 tspc2      | lucy  | tablespace/tspc2_1
(4 rows)

omm=# 


```

2.在表空间tspc1中建表，并使用视图pg_tables查看信息

```
2.1 新建表指定存储表空间

omm=# create table test_t1(id int, name char(30)) tablespace tspc1;
CREATE TABLE

2.2 插入测试数据


omm=# insert into test_t1 values(2,'tank');
INSERT 0 1
omm=# insert into test_t1 values(3,'zhang');
INSERT 0 1

2.3 查看测试数据        
omm=# select * from test_t1;
 id |              name              
----+--------------------------------
  2 | tank                          
  3 | zhang                         
(2 rows)

2.4 查看test_t1存储状态
omm=# select * from pg_tables where tablename = 'test_t1';
 schemaname | tablename | tableowner | tablespace | hasindexes | hasrules | hastriggers | tablecreator |            created   
         |         last_ddl_time         
------------+-----------+------------+------------+------------+----------+-------------+--------------+----------------------
---------+-------------------------------
 public     | test_t1   | omm        | tspc1      | f          | f        | f           | omm          | 2021-12-07 11:49:09.3
91199+08 | 2021-12-07 11:49:09.391199+08
(1 row)

omm=# 
```

3.重命名tspc1，修改tspc2的用户为Lily，使用\db查看表空间信息

```
3.1 重命名tspc1
omm=# ALTER TABLESPACE tspc1  RENAME TO tspc3;


omm=# CREATE ROLE Lily IDENTIFIED BY 'Lily@789';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE

3.2 修改tspc2的用户为Lily

omm=# ALTER TABLESPACE tspc2 OWNER TO Lily;
ALTER TABLESPACE
omm=# 


3.3 使用\db查看表空间信息
omm=# \db
           List of tablespaces
    Name    | Owner |      Location      
------------+-------+--------------------
 pg_default | omm   | 
 pg_global  | omm   | 
 tspc2      | lily  | tablespace/tspc2_1
 tspc3      | omm   | tablespace/tspc1_1
(4 rows)

omm=# 
```

4.删除表空间


```

omm=# drop table test_t1;
DROP TABLE
omm=# drop tablespace if exists tspc1;
DROP TABLESPACE
omm=# drop tablespace if exists tspc2;
DROP TABLESPACE
omm=# \db
      List of tablespaces
    Name    | Owner | Location 
------------+-------+----------
 pg_default | omm   | 
 pg_global  | omm   | 
(2 rows)

omm=# 




```

