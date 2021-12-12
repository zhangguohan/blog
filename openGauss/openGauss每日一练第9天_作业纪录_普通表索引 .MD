# openGauss每日一练第9天_作业纪录_学习心得体会

  **心得体会：**  学习openGauss普通表索引，索引是对数据库表中一列或多列的值进行排序的一种结构，使用索引可快速访问数据库表中的特定信息。

**课后作业**

**1.创建表products, 分别为表创建一个unique索引1，指定b-tree索引2和表达式索引3**


***1.1 新建一个测试表products***
```
tank=# \c tank
Non-SSL connection (SSL connection is recommended when requiring high-security)
You are now connected to database "tank" as user "omm".
tank=# 
tank=# 
tank=# 
tank=# CREATE TABLE products
tank-# (
tank(# SM_SHIP_MODE_SK INTEGER NOT NULL,
tank(# SM_SHIP_MODE_ID CHAR(16) NOT NULL,
tank(# SM_TYPE CHAR(30),
tank(# SM_CODE CHAR(10),
tank(# SM_CARRIER CHAR(20),
tank(# SM_CONTRACT CHAR(20)
tank(# );
CREATE TABLE
tank=# 
```

***1.2 插入测试数据***
```
tank=# with dd as (
tank(# select generate_series(1,399999) as SM_SHIP_MODE_SK,substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 25),substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 20),substr(md5(random()::text), 0, 20)) insert into products select * from dd;
INSERT 0 399999
tank=# 

tank=# select count(SM_SHIP_MODE_SK) from products;
 count  
--------
 399999
(1 row)

tank=# select * from products limit 10;
 sm_ship_mode_sk | sm_ship_mode_id  |            sm_type             |  sm_code   |      sm_carrier      |     sm_contract      
-----------------+------------------+--------------------------------+------------+----------------------+----------------------
               1 | 5da2b86973       | 9525c79e84942874ba1a74d12      | fa22fc864f | cc556a9ce72fcb8014dd | 215f52418aa816ef81a4
               2 | 8bcd41a1a4       | 613544496a96684684998c63e      | 1a96609d8b | 6ade84b1e0be03be662a | d36323376efd757a9990
               3 | 61944c3c9f       | 639198865b44826df5d5008c8      | 60d1572f29 | 04ca97ce6af1c3fccba5 | 3e7412ddccea4c178d64
               4 | 531acbbf3e       | 9e9ecc02170c03dedad2c37fc      | a87bb66db0 | 33efa2eb0bb9df2d6554 | d1d6d39cdf0623f7688a
               5 | 06de90b5c3       | a74f44dc5159c8fcab760bc2b      | e99754aede | 183216279ee178a72afe | 95db713679e39dda37e9
               6 | 8473648fc6       | 72bdbb50ee88100add86fa8cc      | 9213dea373 | e13e7cd9be6222fad370 | 5dd7dd2f61f545bf2273
               7 | 6620aca1c6       | 1dc8776f710e11603f4491a57      | 9d745306a9 | d09faeead4447cedaa46 | 9b23c481f7734ac24db8
               8 | 70f8c55d77       | c49a6916d40263a780a821465      | 949409034e | d3532e6bf07145363b81 | 675a37b21bd5f1f6b56e
               9 | fee5e6eeac       | 715ae8cd48cd23913387db45c      | 66301a9406 | 166115ed5d0951947e00 | ad85b3ffc4b0bfa3f381
              10 | 6da2b2c1c7       | d687498efbe35f07bba280e59      | cde62f35d1 | 8cf345fdd290bf935e6a | 9f16310cec512bc7a8b8
(10 rows)

tank=# 

```

*** 1.3 新建一个unique索引1***

```
tank=# CREATE UNIQUE INDEX products_mode_t1_index1 ON products(SM_SHIP_MODE_SK);
CREATE INDEX

tank=# explain analyze select SM_SHIP_MODE_SK from products where SM_SHIP_MODE_SK < 5000;
                                                                QUERY PLAN                                                                 
-------------------------------------------------------------------------------------------------------------------------------------------
 Bitmap Heap Scan on products  (cost=2505.58..11069.24 rows=133333 width=4) (actual time=1.768..5.174 rows=4999 loops=1)
   Recheck Cond: (sm_ship_mode_sk < 5000)
   Heap Blocks: exact=87
   ->  Bitmap Index Scan on products_mode_t1_index1  (cost=0.00..2472.25 rows=133333 width=0) (actual time=1.662..1.662 rows=4999 loops=1)
         Index Cond: (sm_ship_mode_sk < 5000)
 Total runtime: 6.172 ms
(6 rows)

tank=# 

```
*** 1.4 指定b-tree索引2***
```

tank=# CREATE INDEX products_index4 ON products USING btree(SM_SHIP_MODE_SK);
CREATE INDEX
tank=# explain analyze select SM_SHIP_MODE_SK from products where SM_SHIP_MODE_SK < 5000;
                                                            QUERY PLAN                                                             
-----------------------------------------------------------------------------------------------------------------------------------
 Bitmap Heap Scan on products  (cost=2505.58..11069.24 rows=133333 width=4) (actual time=1.690..5.938 rows=4999 loops=1)
   Recheck Cond: (sm_ship_mode_sk < 5000)
   Heap Blocks: exact=87
   ->  Bitmap Index Scan on products_index4  (cost=0.00..2472.25 rows=133333 width=0) (actual time=1.588..1.588 rows=4999 loops=1)
         Index Cond: (sm_ship_mode_sk < 5000)
 Total runtime: 6.848 ms
(6 rows)

tank=# 



```
*** 1.5 表达式索引3***
```


tank=# CREATE INDEX products_t1_index2 ON products(SUBSTR(SM_CODE,1 ,4));
CREATE INDEX
tank=# 


```



**2.设置索引1不可用，修改索引2的表空间，重命名索引3**

*** 2.1 设置索引1不可用***

```
tank=# ALTER INDEX products_mode_t1_index1 UNUSABLE;
ALTER INDEX
tank=# 

```

*** 2.2 修改索引2的表空间***

```

tank=# CREATE TABLESPACE example0 RELATIVE LOCATION 'tablespace1/tablespace_0';
CREATE TABLESPACE
tank=# alter index products_t1_index2  set tablespace example0;
ALTER INDEX
tank=# 


```


**3.重建索引2和products的所有索引**


```

tank=# reindex table products;
REINDEX
tank=# 

```

**4.使用\d+和系统视图pg_indexes查看索引信息**

```

tank=# \d  products
           Table "public.products"
     Column      |     Type      | Modifiers 
-----------------+---------------+-----------
 sm_ship_mode_sk | integer       | not null
 sm_ship_mode_id | character(16) | not null
 sm_type         | character(30) | 
 sm_code         | character(10) | 
 sm_carrier      | character(20) | 
 sm_contract     | character(20) | 
Indexes:
    "products_mode_t1_index1" UNIQUE, btree (sm_ship_mode_sk) TABLESPACE pg_default UNUSABLE
    "products_index4" btree (sm_ship_mode_sk) TABLESPACE pg_default
    "products_t1_index2" btree (substr(sm_code::text, 1, 4)) TABLESPACE example0, tablespace "example0"

tank=# select * from pg_indexes where tablename = 'products';
 schemaname | tablename |        indexname        | tablespace |                                                  indexdef                                                   
------------+-----------+-------------------------+------------+-------------------------------------------------------------------------------------------------------------
 public     | products  | products_mode_t1_index1 |            | CREATE UNIQUE INDEX products_mode_t1_index1 ON products USING btree (sm_ship_mode_sk) TABLESPACE pg_default
 public     | products  | products_index4         |            | CREATE INDEX products_index4 ON products USING btree (sm_ship_mode_sk) TABLESPACE pg_default
 public     | products  | products_t1_index2      | example0   | CREATE INDEX products_t1_index2 ON products USING btree (substr((sm_code)::text, 1, 4)) TABLESPACE example0
(3 rows)

tank=# 


```

**5.删除索引、表和表空间**

***5.1 删除索引***
```
tank=# drop index products_mode_t1_index1;
DROP INDEX
tank=# 
tank=# drop index products_index4;
DROP INDEX
tank=# 
tank=# drop index products_t1_index2;
DROP INDEX
```
*** 5.2 删除表***
```
tank=# truncate table products;
TRUNCATE TABLE
tank=# drop table products;
DROP TABLE
```
*** 5.3 删附表空间***
```
tank=# drop tablespace example0; 
DROP TABLESPACE
tank=# 

```