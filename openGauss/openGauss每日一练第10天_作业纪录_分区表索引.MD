# openGauss每日一练第10天_作业纪录_学习心得体会

  **心得体会：**  学习openGauss分区表索引、本地索引和全局索引、指定索引表空间。

**课后作业**

**1.创建范围分区表products, 为表创建分区表索引1，不指定索引分区的名称，创建分区表索引2，并指定索引分区的名称，创建GLOBAL分区索引3**


***1.1 新创建索引表空间***
```
tank=#  CREATE TABLESPACE example1 RELATIVE LOCATION 'tablespace1/tablespace_1';
 LOCATION 'tablespace4/tablespace_4';CREATE TABLESPACE
tank=# CREATE TABLESPACE example2 RELATIVE LOCATION 'tablespace2/tablespace_2';
CREATE TABLESPACE
tank=#  CREATE TABLESPACE example3 RELATIVE LOCATION 'tablespace3/tablespace_3';
CREATE TABLESPACE
tank=# CREATE TABLESPACE example4 RELATIVE LOCATION 'tablespace4/tablespace_4';
CREATE TABLESPACE
```
***1.2 新创建分区表products***
```
tank=# CREATE TABLE products
tank-# (
tank(#       product_id           INTEGER               NOT NULL,
tank(#     product_name             CHAR(20)              NOT NULL,
tank(#     CA_STREET_NUMBER          CHAR(10)    
tank(# 
tank(# )
tank-# TABLESPACE example1
tank-# PARTITION BY RANGE(product_id)
tank-# ( 
tank(#    PARTITION p1 VALUES LESS THAN (3000),
tank(#    PARTITION p2 VALUES LESS THAN (5000) TABLESPACE example1,
tank(#    PARTITION p3 VALUES LESS THAN (MAXVALUE) TABLESPACE example2
tank(# );
CREATE TABLE
tank=# 

```

***1.3 插入测试数据***
```
tank=# with dd as (
tank(# select generate_series(1,299999) as product_id,substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 10)) insert into products select * from dd;
INSERT 0 299999
tank=# select count(*)from products;
 count  
--------
 299999
(1 row)

tank=# select *from products limit 10
tank-# ;
 product_id |     product_name     | ca_street_number 
------------+----------------------+------------------
          1 | 0f8432c8c5           | 665fe1be8e
          2 | eb9c99153e           | b7bc217293
          3 | c4f6620bf1           | aed70212cc
          4 | b664430f43           | c3cd8b1433
          5 | 0a2191bb74           | e989b819b7
          6 | 0b79ad38c9           | 78056a0cc6
          7 | dc50c2d260           | 1386176cba
          8 | 3744dd6676           | 68818f0e0d
          9 | a13e890760           | c2dffd88d5
         10 | 00f3c9ae52           | 45f1a199cb
(10 rows)

tank=# 




```

*** 1.4 为表创建分区表索引1，不指定索引分区的名称***

```
tank=# CREATE INDEX products_p1_index1 ON products(product_id) LOCAL; 
CREATE INDEX

tank=#  explain analyze select * from products where product_id < 400;
                                                                     QUERY PLAN                                                                      
-----------------------------------------------------------------------------------------------------------------------------------------------------
 Partition Iterator  (cost=700.74..2071.57 rows=42386 width=132) (actual time=0.394..0.641 rows=399 loops=1)
   Iterations: 1
   ->  Partitioned Bitmap Heap Scan on products  (cost=700.74..2071.57 rows=42386 width=132) (actual time=0.365..0.512 rows=399 loops=1)
         Recheck Cond: (product_id < 400)
         Heap Blocks: exact=4
         Selected Partitions:  1
         ->  Partitioned Bitmap Index Scan on products_p1_index1  (cost=0.00..690.14 rows=42386 width=0) (actual time=0.326..0.326 rows=399 loops=1)
               Index Cond: (product_id < 400)
               Selected Partitions:  1
 Total runtime: 0.944 ms
(10 rows)

tank=# 


```
*** 1.5 创建分区表索引2，并指定索引分区的名称***
```
tank=# CREATE INDEX products_index2 ON products(product_id) LOCAL
tank-# (
tank(#     PARTITION product_id_index1,
tank(#     PARTITION product_id_index2 TABLESPACE example3,
tank(#     PARTITION product_id_index3 TABLESPACE example4
tank(# ) 
tank-# TABLESPACE example2;
CREATE INDEX
tank=# 
tank=# explain analyze select * from products where product_id <30000 and product_id >3000;
                                                                       QUERY PLAN                                                                       
--------------------------------------------------------------------------------------------------------------------------------------------------------
 Partition Iterator  (cost=0.00..882.79 rows=26527 width=36) (actual time=0.274..34.533 rows=26999 loops=1)
   Iterations: 2
   ->  Partitioned Index Scan using products_index2 on products  (cost=0.00..882.79 rows=26527 width=36) (actual time=0.456..28.216 rows=26999 loops=2)
         Index Cond: ((product_id < 30000) AND (product_id > 3000))
         Selected Partitions:  2..3
 Total runtime: 38.111 ms
(6 rows)

tank=# 




```
*** 1.6 创建GLOBAL分区索引3***
```
tank=# CREATE INDEX products_p1_index1_index3 ON products(product_id) GLOBAL;
ERROR:  Global and local partition index should not be on same column


tank=# CREATE INDEX products_p1_index1_index3 ON products(product_name) GLOBAL;
CREATE INDEX
tank=# 


```


**2.在分区表索引1上，修改分区表索引的表空间，重命名分区表索引**

*** 2.1 在分区表索引1上，修改分区表索引的表空间***

```
tank=# ALTER INDEX  products_p1_index1 MOVE PARTITION   p1_product_id_idx TABLESPACE example3;
ALTER INDEX


tank=# \d+ producs
Did not find any relation named "producs".
tank=# \d+ products
                               Table "public.products"
      Column      |     Type      | Modifiers | Storage  | Stats target | Description 
------------------+---------------+-----------+----------+--------------+-------------
 product_id       | integer       | not null  | plain    |              | 
 product_name     | character(20) | not null  | extended |              | 
 ca_street_number | character(10) |           | extended |              | 
Indexes:
    "products_index2" btree (product_id) LOCAL(PARTITION product_id_index1 TABLESPACE example3, PARTITION product_id_index2 TABLESPACE example3, PARTITION product_id_index3 TABLESPACE example4)  TABLESPACE example2, tablespace "example2"
    "products_p1_index1" btree (product_id) LOCAL(PARTITION p1_product_id_idx TABLESPACE **example3**, PARTITION p2_product_id_idx, PARTITION p3_product_id_idx)  TABLESPACE pg_default
    "products_p1_index1_index3" btree (product_name) TABLESPACE pg_default
Range partition by(product_id)
Number of partition: 3 (View pg_partition to check each partition range.)
Has OIDs: no
Tablespace: "example1"
Options: orientation=row, compression=no

tank=# 


```

*** 2.2 重命名分区表索引***

```

tank=# ALTER INDEX  products_p1_index1 RENAME PARTITION   p1_product_id_idx to p1_product_id_idx_rename;
ALTER INDEX
tank=# \d+ products
                               Table "public.products"
      Column      |     Type      | Modifiers | Storage  | Stats target | Description 
------------------+---------------+-----------+----------+--------------+-------------
 product_id       | integer       | not null  | plain    |              | 
 product_name     | character(20) | not null  | extended |              | 
 ca_street_number | character(10) |           | extended |              | 
Indexes:
    "products_index2" btree (product_id) LOCAL(PARTITION product_id_index1 TABLESPACE example3, PARTITION product_id_index2 TABLESPACE example3, PARTITION product_id_index3 TABLESPACE example4)  TABLESPACE example2, tablespace "example2"
    "products_p1_index1" btree (product_id) LOCAL(PARTITION p1_product_id_idx_rename TABLESPACE example3, PARTITION p2_product_id_idx, PARTITION p3_product_id_idx)  TABLESPACE pg_default
    "products_p1_index1_index3" btree (product_name) TABLESPACE pg_default
Range partition by(product_id)
Number of partition: 3 (View pg_partition to check each partition range.)
Has OIDs: no
Tablespace: "example1"
Options: orientation=row, compression=no

tank=# 

```


**3.在分区表索引2上，重建单个索引分区和分区上的所有索引**

***3.1  在分区表索引2上，重建单个索引分区***
```

tank=# reindex index  products_index2 PARTITION product_id_index2;
REINDEX
tank=# 

```

***3.2  在分区表索引2上，分区上的所有索引***


```
tank=# reindex table products PARTITION  p1;
REINDEX
tank=# 


```
**4.使用\d+、系统视图pg_indexes和pg_partition查看索引信息**

```

tank=# \d+ products;
                               Table "public.products"
      Column      |     Type      | Modifiers | Storage  | Stats target | Description 
------------------+---------------+-----------+----------+--------------+-------------
 product_id       | integer       | not null  | plain    |              | 
 product_name     | character(20) | not null  | extended |              | 
 ca_street_number | character(10) |           | extended |              | 
Indexes:
    "products_index2" btree (product_id) LOCAL(PARTITION product_id_index1 TABLESPACE example3, PARTITION product_id_index2 TABLESPACE example3, PARTITION product_id_index3 TABLESPACE example4)  TABLESPACE exam
ple2, tablespace "example2"
    "products_p1_index1" btree (product_id) LOCAL(PARTITION p1_product_id_idx_rename TABLESPACE example3, PARTITION p2_product_id_idx, PARTITION p3_product_id_idx)  TABLESPACE pg_default
    "products_p1_index1_index3" btree (product_name) TABLESPACE pg_default
Range partition by(product_id)
Number of partition: 3 (View pg_partition to check each partition range.)
Has OIDs: no
Tablespace: "example1"
Options: orientation=row, compression=no

tank=# select * from pg_partition;
         relname          | parttype | parentid | rangenum | intervalnum | partstrategy | relfilenode | reltablespace | relpages | reltuples | relallvisible | reltoastrelid | reltoastidxid | indextblid | indisu
sable | reldeltarelid | reldeltaidx | relcudescrelid | relcudescidx | relfrozenxid | intspnum | partkey | intervaltablespace | interval | boundaries | transit |                    reloptions                    
 | relfrozenxid64 
--------------------------+----------+----------+----------+-------------+--------------+-------------+---------------+----------+-----------+---------------+---------------+---------------+------------+-------
------+---------------+-------------+----------------+--------------+--------------+----------+---------+--------------------+----------+------------+---------+--------------------------------------------------
-+----------------
 p2_product_id_idx        | x        |    16920 |        0 |           0 | n            |       16922 |             0 |        8 |      2000 |             0 |             0 |             0 |      16918 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
 p3_product_id_idx        | x        |    16920 |        0 |           0 | n            |       16923 |             0 |      814 |    295000 |             0 |             0 |             0 |      16919 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
 products                 | r        |    16913 |        0 |           0 | r            |           0 |         16909 |        0 |         0 |             0 |             0 |             0 |          0 | t     
      |             0 |           0 |              0 |            0 | 0            |          | 1       |                    |          |            |         | {orientation=row,compression=no,wait_clean_gpi=n}
 |              0
 p1                       | p        |    16913 |        0 |           0 | r            |       16917 |         16909 |       26 |      2999 |             0 |             0 |             0 |          0 | t     
      |             0 |           0 |              0 |            0 | 13908        |          |         |                    |          | {3000}     |         | {orientation=row,compression=no}                 
 |          13908
 p2                       | p        |    16913 |        0 |           0 | r            |       16918 |         16909 |       17 |      2000 |             0 |             0 |             0 |          0 | t     
      |             0 |           0 |              0 |            0 | 13908        |          |         |                    |          | {5000}     |         | {orientation=row,compression=no}                 
 |          13908
 p3                       | p        |    16913 |        0 |           0 | r            |       16919 |         16910 |     2479 |    295000 |             0 |             0 |             0 |          0 | t     
      |             0 |           0 |              0 |            0 | 13908        |          |         |                    |          | {NULL}     |         | {orientation=row,compression=no}                 
 |          13908
 product_id_index3        | x        |    16924 |        0 |           0 | n            |       16927 |         16912 |      814 |    295000 |             0 |             0 |             0 |      16919 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
 product_id_index2        | x        |    16924 |        0 |           0 | n            |       16931 |         16911 |        8 |      2000 |             0 |             0 |             0 |      16918 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
 p1_product_id_idx_rename | x        |    16920 |        0 |           0 | n            |       16932 |         16911 |       11 |      2999 |             0 |             0 |             0 |      16917 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
 product_id_index1        | x        |    16924 |        0 |           0 | n            |       16933 |         16911 |       11 |      2999 |             0 |             0 |             0 |      16917 | t     
      |             0 |           0 |              0 |            0 | 0            |          |         |                    |          |            |         |                                                  
 |              0
(10 rows)

tank=#  select * from pg_indexes where tablename = 'products';
 schemaname | tablename |         indexname         | tablespace |                                                                                                                    indexdef                    
                                                                                                 
------------+-----------+---------------------------+------------+------------------------------------------------------------------------------------------------------------------------------------------------
-------------------------------------------------------------------------------------------------
 public     | products  | products_p1_index1        |            | CREATE INDEX products_p1_index1 ON products USING btree (product_id) LOCAL(PARTITION p1_product_id_idx_rename TABLESPACE example3, PARTITION p2
_product_id_idx, PARTITION p3_product_id_idx)  TABLESPACE pg_default
 public     | products  | products_index2           | example2   | CREATE INDEX products_index2 ON products USING btree (product_id) LOCAL(PARTITION product_id_index1 TABLESPACE example3, PARTITION product_id_i
ndex2 TABLESPACE example3, PARTITION product_id_index3 TABLESPACE example4)  TABLESPACE example2
 public     | products  | products_p1_index1_index3 |            | CREATE INDEX products_p1_index1_index3 ON products USING btree (product_name) TABLESPACE pg_default
(3 rows)



```

**5.删除索引、表和表空间**


```

tank=#  drop index products_p1_index1;
DROP INDEX
tank=#  drop index products_index2 ;
DROP INDEX
tank=#  drop index products_p1_index1_index3;
DROP INDEX

tank=# truncate table products;
TRUNCATE TABLE
tank=# drop table products;
DROP TABLE
tank=# drop tablespace example1;
DROP TABLESPACE
tank=# drop tablespace example2;
DROP TABLESPACE
tank=# drop tablespace example3;
DROP TABLESPACE
tank=# drop tablespace example4;
DROP TABLESPACE
tank=# 


``
