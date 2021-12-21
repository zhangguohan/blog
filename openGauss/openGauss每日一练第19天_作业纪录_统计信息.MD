
  ## 心得体会  

  今天学习了openGauss收集统计信息、打印执行计划、垃圾收集和checkpoint。

## 课后作业  

## 1.创建分区表，并用generate_series(1,N)函数对表插入数据


```
omm=# CREATE TABLE products
omm-#  (
omm(# omm(#     product_name             CHAR(20)              NOT NULL,
    product_id           INTEGER               NOT NULL,
omm(#     CA_STREET_NUMBER          CHAR(10)    
omm(# ) PARTITION BY RANGE(product_id)
omm-# omm(#   PARTITION p1 VALUES LESS THAN (3000),
 ( 
omm(#    PARTITION p2 VALUES LESS THAN (5000) ,
omm(#     PARTITION p3 VALUES LESS THAN (MAXVALUE)
omm(#  );


omm=# with dd as (select generate_series(1,299999) as product_id,substr(md5(random()::text), 0, 10),substr(md5(random()::text), 0, 10)) insert into products select * from dd;
INSERT 0 299999

omm=# select count(*)from products;
 count  
--------
 299999
(1 row)

omm=# 


```


## 2. 收集表统计信息
```
omm=# analyze VERBOSE products;   
           
INFO:  analyzing "public.products"(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 26 of 26 pages, containing 2999 live rows and 0 dead rows; 310 rows in sample, 2999 estimated total rows(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 17 of 17 pages, containing 2000 live rows and 0 dead rows; 203 rows in sample, 2000 estimated total rows(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 2479 of 2479 pages, containing 295000 live rows and 0 dead rows; 29487 rows in sample, 295000 estimated total rows(gaussdb pid=1)
ANALYZE
omm=# 

```


## 3.显示简单查询的执行计划；建立索引并显示有索引条件的执行计划

```
omm=# explain analyze select * from products where product_id < 1000;
                                                         QUERY PLAN                                                          
-----------------------------------------------------------------------------------------------------------------------------
 Partition Iterator  (cost=0.00..4590.99 rows=1020 width=36) (actual time=0.023..0.858 rows=999 loops=1)
   Iterations: 1
   ->  Partitioned Seq Scan on products  (cost=0.00..4590.99 rows=1020 width=36) (actual time=0.017..0.697 rows=999 loops=1)
         Filter: (product_id < 1000)
         Rows Removed by Filter: 2000
         Selected Partitions:  1
 Total runtime: 0.972 ms
(7 rows)



omm=# create index products_inx on products(product_id);
CREATE INDEX
omm=# explain analyze select * from products where product_id < 1000;
                                                          QUERY PLAN                                                          
------------------------------------------------------------------------------------------------------------------------------
 Index Scan using products_inx on products  (cost=0.00..28.11 rows=1020 width=36) (actual time=0.149..0.489 rows=999 loops=1)
   Index Cond: (product_id < 1000)
 Total runtime: 0.620 ms
(3 rows)

omm=# 



```



## 4.更新表数据，并做垃圾收集

```
                            ^
omm=# update products set product_name = 'tank' where product_id <1000;
UPDATE 999


omm=# analyze VERBOSE products; 


INFO:  analyzing "public.products"(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 34 of 34 pages, containing 2999 live rows and 999 dead rows; 404 rows in sample, 2999 estimated total rows(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 17 of 17 pages, containing 2000 live rows and 0 dead rows; 202 rows in sample, 2000 estimated total rows(gaussdb pid=1)
INFO:  ANALYZE INFO : "products": scanned 2479 of 2479 pages, containing 295000 live rows and 0 dead rows; 29394 rows in sample, 295000 estimated total rows(gaussdb pid=1)
ANALYZE
omm=# 

```



## 5.清理数据

```
omm=# truncate table products;              
TRUNCATE TABLE
omm=# drop table products;
DROP TABLE
omm=# 


```