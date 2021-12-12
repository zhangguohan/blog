# openGauss每日一练第11天_作业纪录_学习心得体会

  ## 心得体会  

  今天学习了openGauss视图与基本表不同，是一个虚拟的表。数据库中仅存放视图的定义，而不存放视图对应的数据，这些数据仍存放在原来的基本表中。

## 课后作业  

## 1.为系统表PG_DATABASE创建视图，重命名视图并修改owner为jim


### 1.1 为系统表PG_DATABASE创建视图
```
omm=# create view  das_view as select * from pg_database;
CREATE VIEW
omm=# 

```
### 1.2 重命名视图为 das_view
```
omm=# alter view das_view rename to das_vies_v2;
ALTER VIEW
omm=# 

```

### 1.3 修改das_vies_v2的owner为jim
```
omm=# create user jim password 'tankd!@#x9';
NOTICE:  The encrypted password contains MD5 ciphertext, which is not secure.
CREATE ROLE
omm=# alter view das_vies_v2 owner to jim;
ALTER VIEW

```

## 2.创建一个用户表student，并在用户表上创建视图，修改视图schema

### 2.1创建一个用户表student，并在用户表上创建视图  
```
omm=# CREATE TABLE  student
omm-# (   id            integer,      
omm(#    name               char(8) 
omm(# ) ;
CREATE TABLE

omm=# insert into student values(1,'tank');
INSERT 0 1
omm=# 
omm=# 
omm=# insert into student values(2,'tank');
INSERT 0 1
omm=# 
omm=# 
omm=# insert into student values(3,'tank');
INSERT 0 1

omm=# create view student_view as select * from student;
CREATE VIEW

```
### 2.2 修改 student_view 视图schema  

```
omm=# create schema tank;
CREATE SCHEMA
omm=# ALTER VIEW   student_view SET schema tank;
ALTER VIEW
omm=# 

```

## 3.使用pg_views查看视图信息

```
omm=# select * from pg_views where viewname = 'student_view';
 schemaname |   viewname   | viewowner |       definition        
------------+--------------+-----------+-------------------------
 tank       | student_view | omm       | SELECT  * FROM student;
(1 row)

omm=# 
 
```



## 4.删除视图、表、用户
```
omm=# drop view tank.student_view;
DROP VIEW
omm=# 
omm=# drop table student;
DROP TABLE
omm=# drop schema tank;
DROP SCHEMA



```

