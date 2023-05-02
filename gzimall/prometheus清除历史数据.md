# prometheus清除历史数据

### 数据存储

Prometheus具有配置本地存储的多个标志。最重要的是：

```
--storage.tsdb.path：Prometheus写入数据库的位置。默认为data/。
--storage.tsdb.retention.time：何时删除旧数据。默认为15d。storage.tsdb.retention如果此标志设置为默认值以外的任何值，则覆盖。
--storage.tsdb.retention.size：[EXPERIMENTAL]要保留的最大存储块字节数。最旧的数据将首先被删除。默认为0或禁用。该标志是试验性的，将来的发行版中可能会更改。支持的单位：B，KB，MB，GB，TB，PB，EB。例如：“ 512MB”
--storage.tsdb.retention：不推荐使用storage.tsdb.retention.time。
--storage.tsdb.wal-compression：启用压缩预写日志（WAL）。根据您的数据，您可以预期WAL大小将减少一半，而额外的CPU负载却很少。该标志在2.11.0中引入，默认情况下在2.20.0中启用。请注意，一旦启用，将Prometheus降级到2.11.0以下的版本将需要删除WAL。
#For example:
./prometheus --storage.tsdb.retention.time=1d
./prometheus --storage.tsdb.retention.size=10GB
```

### 删除历史数据

默认情况下，管理时间序列的 API 是被禁用的，要启用它，我们需要在 Prometheus 的启动参数中添加--web.enable-admin-api这个参数

### 删除某个标签匹配的数据

```
$ curl -X POST -g 'http://localhost:9090/api/v1/admin/tsdb/delete_series?match[]={instance=".*"}'
```

### 删除某个指标数据

```
$ curl -X POST -g 'http://localhost:9090/api/v1/admin/tsdb/delete_series?match[]={node_load1=".*"}'
```

### 根据时间删除

```
$ curl -X POST -g 'http://localhost:9090/api/v1/admin/tsdb/delete_series?match[]={node_load1=".*"}&start<2020-02-01T00:00:00Z&end=2020-02-04T00:00:00Z'
```