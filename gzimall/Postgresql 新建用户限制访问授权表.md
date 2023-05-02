## Postgresql 新建用户限制访问授权表

### 1、新建用户

```
create user user_test with password 'test_123!';
```

### 2、限制连接数

```
alter user user_test connection limit 10;
```

### 3、要看用角色权限

```
SELECT * FROM pg_roles WHERE rolname = 'user_test';
```

### 4、授权访问schema

```
GRANT USAGE ON SCHEMA test_schema TO user_test;
```

### 5、指定访问表，读、写、更新、

```
GRANT SELECT, UPDATE, INSERT ON qis_badticket_wqip  TO user_test;
```

### 6、指定访问表查询权限

```
GRANT SELECT ON wqis_badrecord_view wqis_badrecord_view TO user_test ;
```