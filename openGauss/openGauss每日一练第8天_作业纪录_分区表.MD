# openGauss每日一练第8天_作业纪录_分区表

  **心得体会：**  学习了openGauss分区表是把逻辑上的一张表根据某种方案分成几张物理块进行存储，这张逻辑上的表称之为分区表，物理块称之为分区。分区表是一张逻辑表，不存储数据，数据实际是存储在分区上的。

**课后作业**
**1.创建一个含有5个分区的范围分区表store，在每个分区中插入记录**
```
1.1 新建一个5个分区的分区表store

tank=# create table store
tank-# (
tank(#         id int not null,
tank(#         name CHAR(20)
tank(# )
tank-# partition by range (id)
tank-# (
tank(#         partition update_store_p0 values less than (50),
tank(#         partition update_store_p1 values less than (100),
tank(#         partition update_store_p2 values less than (150),
tank(#         partition update_store_p3 values less than (200),
tank(#         partition update_store_p4 values less than (250)
tank(# );
CREATE TABLE
tank=# 

1.2 分别在每个分区中写入数据


tank=# insert into store values(222,'tank223');
INSERT 0 1
tank=# insert into store values(23,'tank');
INSERT 0 1
tank=# 
tank=# insert into store values(53,'tank53');
INSERT 0 1
tank=# 
tank=# insert into store values(103,'tank103');
INSERT 0 1
tank=# 
tank=# insert into store values(183,'tank183');
INSERT 0 1
tank=# 
tank=# insert into store values(222,'tank223');
INSERT 0 1
tank=# 



tank=# select * from store;
 id  |         name         
-----+----------------------
  23 | tank                
  53 | tank53              
 103 | tank103             
 183 | tank183             
 222 | tank223             
(5 rows)



```
     


**2.查看分区1上的数据**

```

tank=# select * from store partition (update_store_p0);
 id |         name         
----+----------------------
 23 | tank                
(1 row)

tank=# 



```

**3.重命名分区2**

```


tank=# alter table store rename partition update_store_p1 to update_store_p1_1;
ALTER TABLE
tank=# 




```

**4.删除分区5**

```
tank=# alter table store drop partition update_store_p4;
ALTER TABLE
tank=# 


```


**5.增加分区6**

```
tank=# alter table store  add partition update_table_p6 values less than (300);
ALTER TABLE
tank=# 



```



**6.在系统表pg_partition中查看分区信息**


```


tank=# select * from pg_partition;
      relname      | parttype | parentid | rangenum | intervalnum | partstrategy | relfilenode | reltablespace | relpages | reltuples | relallvisible | reltoastrelid | reltoastidxid | indextblid | indisusable |
 reldeltarelid | reldeltaidx | relcudescrelid | relcudescidx | relfrozenxid | intspnum | partkey | intervaltablespace | interval | boundaries | transit |                    reloptions                     | relf
rozenxid64 
-------------------+----------+----------+----------+-------------+--------------+-------------+---------------+----------+-----------+---------------+---------------+---------------+------------+-------------+
---------------+-------------+----------------+--------------+--------------+----------+---------+--------------------+----------+------------+---------+---------------------------------------------------+-----
-----------
 store             | r        |    16843 |        0 |           0 | r            |           0 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 0            |          | 1       |                    |          |            |         | {orientation=row,compression=no,wait_clean_gpi=n} |     
         0
 update_store_p3   | p        |    16843 |        0 |           0 | r            |       16853 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 0            |          |         |                    |          | {200}      |         | {orientation=row,compression=no}                  |     
     13826
 update_store_p2   | p        |    16843 |        0 |           0 | r            |       16854 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 0            |          |         |                    |          | {150}      |         | {orientation=row,compression=no}                  |     
     13826
 update_store_p0   | p        |    16843 |        0 |           0 | r            |       16856 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 0            |          |         |                    |          | {50}       |         | {orientation=row,compression=no}                  |     
     13826
 update_store_p1_1 | p        |    16843 |        0 |           0 | r            |       16855 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 0            |          |         |                    |          | {100}      |         | {orientation=row,compression=no}                  |     
     13826
 update_table_p6   | p        |    16843 |        0 |           0 | r            |       16858 |             0 |        0 |         0 |             0 |             0 |             0 |          0 | t           |
             0 |           0 |              0 |            0 | 13838        |          |         |                    |          | {300}      |         | {orientation=row,compression=no}                  |     
     13838
(6 rows)

tank=# \d+ store
                            Table "public.store"
 Column |     Type      | Modifiers | Storage  | Stats target | Description 
--------+---------------+-----------+----------+--------------+-------------
 id     | integer       | not null  | plain    |              | 
 name   | character(20) |           | extended |              | 
Range partition by(id)
Number of partition: 5 (View pg_partition to check each partition range.)
Has OIDs: no
Options: orientation=row, compression=no

tank=# 


```

**7.删除分区表**


```
tank=# drop table store;
DROP TABLE
tank=# 



```
