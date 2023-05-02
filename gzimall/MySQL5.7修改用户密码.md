# MySQL5.7修改用户密码

在MySQL 5.7 password字段已从mysql.user表中删除，新的字段名是“authenticalion_string”.

更新root的密码

格式：

　　update user set authentication_string=password('新密码') where user='root' and Host='localhost';

例：

选择数据库

```
use mysql;
```

修改密码

```
update user set authentication_string=password('Dkd202263#') where user='testuser' and Host='%';
```

刷新权限

```
flush privileges;
```