

### 3.4 为RMAN操作配置数据库

#### 3.4.1设置数据库用户
~~ 在默认情况下，可以通过SYS账号（即sysdba）为使用RMAN，该账号不需要任何配置。当然，在执行产品备份操作时，SYSDBA并不是最佳的账号，建议在使用RMAN操作之前建一个用于RMAN备份的单独账号设置。

##### 步骤1 确定要使用用户名
```

SQL> create user backup_admin IDENTIFIED BY 6220104 default tablespace users;

User created.
```

##### 步骤2.设置backup_admin用户sysdba权限

SQL> grant sysdba to backup_admin;

Grant succeeded.

SQL> 




