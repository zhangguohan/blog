# openGauss每日一练第2天_作业纪录_学习心得体会

  **心得体会：**  今天练习了表的增(insert into )、删(delete from )、改(update )、查(select)及使用Create table name AS 动态复制表方法。

## 课后作业

1、创建一个表products
```
omm=# CREATE TABLE products
(  product_id             integer,   
 product_name          char(30),    
category              char(20)   
）；



omm=# 


```

2、向表中插入数据，采用一次插入一条和多条记录的方式


```


omm=# INSERT INTO products VALUES    
omm-# omm-# (1502, 'olympus camera', 'electrncs'),    
(1601, 'lamaze','toys'),    
omm-# (1700, 'wait interface', 'Books'),
omm-# (1666, 'harry potter', 'toys');
INSERT 0 4
omm=# 



```

3.获取表中一条记录、三条记录和所有记录

```
omm=# select * from products limit 1;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
(1 row)

omm=# select * from products limit 3; 
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
(3 rows)

omm=# select * from products;                  
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
       1666 | harry potter                   | toys                
(4 rows)






```



4.将满足product_id > 1600的记录的product_id更新为product_id – 1000，并查看products中所有记录是否更新成功

```
omm=# select * from products;                          
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
       1666 | harry potter                   | toys                
(4 rows)

omm=# update products set product_id = -1000 where product_id >1600;
UPDATE 3

omm=# select * from products;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
      -1000 | lamaze                         | toys                
      -1000 | wait interface                 | Books               
      -1000 | harry potter                   | toys                
(4 rows)



```


5.删除category为toys的所有记录，并查看products中数据是否删除成功

```

omm=# delete from products where category = 'toys';
DELETE 2
omm=# select * from products;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
      -1000 | wait interface                 | Books               
(2 rows)

omm=# 

```


6.删除products中所有数据，并查看数据是否删除成功

```

omm=# delete from products;
DELETE 2
omm=# select * from products;
 product_id | product_name | category 
------------+--------------+----------
(0 rows)

omm=# 


```


7.删除表products

```
omm=# drop table products;
DROP TABLE
omm=#   



```


**扩展学习 动态复制表**

```


omm=# create table products as select * from products_bak;
INSERT 0 4
omm=# select * from products;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
       1666 | harry potter                   | toys                
(4 rows)

omm=# select * from products_bak;
 product_id |          product_name          |       category       
------------+--------------------------------+----------------------
       1502 | olympus camera                 | electrncs           
       1601 | lamaze                         | toys                
       1700 | wait interface                 | Books               
       1666 | harry potter                   | toys                
(4 rows)

omm=# 

```


