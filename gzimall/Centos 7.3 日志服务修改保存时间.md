## Centos 7.3 日志服务修改保存时间

由于有安全方面的需求，要求可追溯系统操作记录，因此想让系统日志保存6个月。
开始以为是 syslogd.service 服务，但是发现系统里没有这项服务，因此网上冲浪了一下，发现在 centos 7.3 中已经被 rsyslog 替代了。

下面记录一下修改保存时间：

```
修改/etc/logrotate.conf 中的
# keep 4 weeks worth of backlogs
rotate 4
# 改为rotate 24
将/var/log/wtmp {
    monthly
    create 0664 root utmp
    rotate 1
    # rotate 1 改为rotate 3，
}
 
# 保存后
# 重启 rsyslog 服务
sudo systemctl restart rsyslog 
```