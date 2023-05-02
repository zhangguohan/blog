# Ngnix与Tomcat日志处理保留一年

### vi /etc/logrotate.d/nginx

```
/appLogs/*.log {
    create 0640 nginx root
    daily
    rotate 360
    missingok
    notifempty
    compress
    delaycompress
    sharedscripts
    postrotate
        /bin/kill -USR1 `cat /run/nginx.pid 2>/dev/null` 2>/dev/null || true
    endscript
}
```

### Tomcat日志处理：

vi /etc/logrotate.d/tomcat

```
su root root
/apache-tomcat-7.0.109/logs/catalina.out{
copytruncate
daily
rotate 360
compress
dateext
size 50M
}
```