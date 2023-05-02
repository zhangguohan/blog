## Resin4 版本修改日志配置

## Resin4的日志默认输出文件夹为：${resin_home}/log/ 如想修改可做如下操作

### 编辑resin.xml文件注释掉：

```xml
<log-handler name="" level="all" path="stdout:"
               timestamp="[%y-%m-%d %H:%M:%S.%s]"
               format=" {${thread}} ${log.message}"/>
```

### 以及

```
<logger name="" level="${log_level?:'info'}"/>
 
  <logger name="com.caucho.java" level="config"/>
  <logger name="com.caucho.loader" level="config"/>
```

### 在hosts下增加如下配置：

```
<host id="" root-directory=".">
 <web-app id="/" root-directory="/home/webapp"/>
 <!-- 以下为日志配置-->
 <stdout-log path='/data/logs/resin/stdout.log'
        archive-format="stdout-%Y_%m_%d.log"
        rollover-period='1D'
        rollover-size='5mb'/>
 
  <stderr-log path='/data/logs/resin/stderr.log'
        archive-format="stderr-%Y_%m_%d.log"
        rollover-period='1D'
        timestamp='[%Y/%m/%d %H:%M:%S.%s] '
        rollover-size='5mb'/>
 
  <access-log  path='/data/logs/resin/access.log' 
        archive-format="access-%Y_%m_%d.log"
        rollover-period='1D'
        rollover-size='5mb'/> 
 
</host>
```