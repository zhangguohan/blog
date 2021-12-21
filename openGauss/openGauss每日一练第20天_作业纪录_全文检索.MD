
  ## 心得体会  

  今天学习了openGauss提供了两种数据类型用于支持全文检索。tsvector类型表示为文本搜索优化的文件格式，tsquery类型表示文本查询

## 课后作业  

## 1.用tsvector @@ tsquery和tsquery @@ tsvector完成两个基本文本匹配


```
omm=# SELECT  'a fat app sat on a tank and ates a dba rat' ::tsvector  @@ 'app & dba'::tsquery  AS RESULT;


omm=#  result 
--------
 t
(1 row)
omm=# 


omm=#  SELECT  'app & dba'::tsquery @@ 'a fat app sat on a tank and ates a dba rat'::tsvector  AS RESULT;
omm=#  result 
--------
 t
(1 row)
omm=# 

```


## 2. 创建表且至少有两个字段的类型为 text类型，在创建索引前进行全文检索
```
omm=# CREATE TABLE fruit (id int, body text, title text, last_mod_date date);
CREATE TABLE
omm=# INSERT INTO fruit  VALUES(1, 'This is appler  is the world s most asdf.', 'china', '2022-1-1');
INSERT 0 1
omm=# INSERT INTO  fruit  VALUES(2, ' orange is a rock band, formed in England in 1970 by multi-instrumentalists Dewey Bunnell, Dan Peek, and Gerry Beckley.', 'America', '2022-1-1');
INSERT 0 1
omm=# INSERT INTO fruit  VALUES(2, 'That is appler  is the world s most asdf.', 'china', '2022-1-1');
INSERT 0 1
omm=# 
omm=# INSERT INTO fruit  VALUES(4, 'hwere is appler  is the world s most asdf.', 'china', '2022-1-1');
INSERT 0 1


omm=# SELECT id, body, title FROM fruit WHERE to_tsvector(body) @@ to_tsquery('appler');
 id |                    body                    | title 
----+--------------------------------------------+-------
  1 | This is appler  is the world s most asdf.  | china
  2 | That is appler  is the world s most asdf.  | china
  4 | hwere is appler  is the world s most asdf. | china
(3 rows)

omm=# 

```


## 3.创建GIN索引

```

omm=#  CREATE INDEX fruit_idx_1 ON  fruit USING gin(to_tsvector('english', body));
omm=# CREATE INDEX

omm=# 



```



## 4.清理数据
```
omm=# drop table fruit;
DROP TABLE
omm=# 

```
