
  ## 心得体会  

  今天学习了行存储是指将表按行存储到硬盘分区上，列存储是指将表按列存储到硬盘分区上。默认情况下，创建的 表为行存储。行、列存储模型各有优劣，通常用于TP场景的数据库，默认使用行存储，仅对执行复杂查询且数据量大的AP场景时，才使用列存储

## 课后作业  

## 1.创建行存表和列存表，并批量插入10万条数据(行存表和列存表数据相同)
### 1.1创建行存表products1，并批量插入10万条数据

```


omm=# CREATE TABLE products1
omm-# (
omm(# SM_SHIP_MODE_SK INTEGER NOT NULL,
omm(# SM_SHIP_MODE_ID CHAR(16) NOT NULL,
omm(# SM_TYPE CHAR(30),
omm(# SM_CODE CHAR(10),
omm(# SM_CARRIER CHAR(20),
omm(# SM_CONTRACT CHAR(20)
omm(# );
CREATE TABLE
omm=# 
omm=# with dd as (
omm(# select generate_series(1,100000) as product_id,substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 10)) insert into products1 select * from dd;
INSERT 0 100000 

omm=# select count(*) from products1;
 count  
--------
 100000
(1 row)
```

### 1.2创建列存表products2，并批量插入10万条数据
```
omm=# 

omm=# CREATE TABLE products2 
omm-# (
omm(# SM_SHIP_MODE_SK INTEGER NOT NULL,
omm(# SM_SHIP_MODE_ID CHAR(16) NOT NULL,
omm(# SM_TYPE CHAR(30),
omm(# SM_CODE CHAR(10),
omm(# SM_CARRIER CHAR(20),
omm(# SM_CONTRACT CHAR(20)
omm(# ) WITH (ORIENTATION = COLUMN,COMPRESSION =MIDDLE );  
CREATE TABLE
omm=# 
omm=# with dd as (
omm(# select generate_series(1,100000) as product_id,substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 10)) insert into products2 select * from dd;
INSERT 0 100000
omm=# select count(*) from products2;
 count  
--------
 100000
(1 row)

omm=# \dt
                               List of relations
 Schema |    Name    | Type  | Owner |                 Storage                 
--------+------------+-------+-------+-----------------------------------------
 public | products1  | table | omm   | {orientation=row,compression=no}
 public | products2  | table | omm   | {orientation=column,compression=middle}
(3 rows)

omm=#   


```



## 2.对比行存表和列存表空间大小

```
omm=# \d+  
                                           List of relations
 Schema |    Name    | Type  | Owner |  Size   |                 Storage                 | Description 
--------+------------+-------+-------+---------+-----------------------------------------+-------------      | 
 public | products1  | table | omm   | 8272 kB | {orientation=row,compression=no}        | 
 public | products2  | table | omm   | 2152 kB | {orientation=column,compression=middle} | 
(3 rows)

omm=# 

```


## 3.对比查询一列和插入一行的速度
```
omm=# explain analyze insert into products1  values(122, 'tank', 'zhang');
                                           QUERY PLAN                                            
-------------------------------------------------------------------------------------------------
 [Bypass]
 Insert on products1  (cost=0.00..0.01 rows=1 width=0) (actual time=0.179..0.182 rows=1 loops=1)
   ->  Result  (cost=0.00..0.01 rows=1 width=0) (actual time=0.003..0.003 rows=1 loops=1)
 Total runtime: 0.507 ms
(4 rows)

omm=# explain analyze insert into products2  values(122, 'tank', 'zhang');
                                           QUERY PLAN                                            
-------------------------------------------------------------------------------------------------
 Insert on products2  (cost=0.00..0.01 rows=1 width=0) (actual time=0.745..0.748 rows=1 loops=1)
   ->  Result  (cost=0.00..0.01 rows=1 width=0) (actual time=0.002..0.002 rows=1 loops=1)
 Total runtime: 0.969 ms
(3 rows)

omm=# 

```


## 4.清理数据
```
omm=# drop table products1;
DROP TABLE
omm=# drop table products2;
DROP TABLE
omm=# 


```