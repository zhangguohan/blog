# openGauss每日一练第1天_作业纪录_表的简单操作

## 课后作业

1、创建一个表products
```
omm=# drop table customer_t;
DROP TABLE
omm=# CREATE TABLE products
omm-# (  product_id             integer,   
omm(#   product_name          char(30),    
omm(#  category              char(20)   
omm(# ) ;
CREATE TABLE

omm=# \dt
 public | products | table | omm   | {orientation=row,compression=no}
(1 row)

omm=#                           List of relations
 Schema |   Name   | Type  | Owner |             Storage              
--------+----------+-------+-------+----------------------------------
omm=# 


```

2、向表中插入数据，采用一次插入一条和多条记录的方式


```

omm=# \dt
                          List of relations
 Schema |   Name   | Type  | Owner |             Storage              
--------+----------+-------+-------+----------------------------------
 public | products | table | omm   | {orientation=row,compression=no}
(1 row)

omm=# INSERT INTO products VALUES    
omm-# omm-# (1502, 'olympus camera', 'electrncs'),    
(1601, 'lamaze','toys'),    
omm-# (1700, 'wait interface', 'Books'),
omm-# (1666, 'harry potter', 'toys');
INSERT 0 4
omm=# 



```

3.查询表中所有记录及记录数

```


omm=# select * from products;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
       1666 | harry potter                   | toys                
(4 rows)

omm=# 



```



4.查询表中所有category记录，并将查询结果按升序排序

```
omm=# select category from products order by category;
       category       
----------------------
 Books               
 electrncs           
 toys                
 toys                
(4 rows)

omm=# 

```


5.查询表中category为toys的记录

```
omm=# select * from  products where category='toys';
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1601 | lamaze                         | toys                
       1666 | harry potter                   | toys                
(2 rows)

omm=# 

```


6.删除表products

```
omm=# drop table products;
DROP TABLE
omm=# 


```
