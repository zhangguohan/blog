## Resin-4 采用vhosts及WAR包发布

## 添加resin.xml

```
  <host id="wwww.test.cn" >
      <!--
         - webapps can be overridden/extended in the resin.xml
        -->
      <web-app id="/" root-directory="/opt/test" expand-preserve-fileset="WEB-INF/work/**"
               archive-path="/opt/test.war"/>
       <stdout-log path='/webapps/log/stdout.log'
                       archive-format="stdout-%Y_%m_%d.log"
                       rollover-period='1D'
                       rollover-size='5mb'/>

       <stderr-log path='/webapps/log/stderr.log'
                                 archive-format="stderr-%Y_%m_%d.log"
                                 rollover-period='1D'
                                 timestamp='[%Y/%m/%d %H:%M:%S.%s] '
                                 rollover-size='5mb'/>

                           <access-log  path='/webapps/log/access.log'
                                           archive-format="access-%Y_%m_%d.log"
                                           rollover-period='1D'
                                           rollover-size='5mb'/>



    </host>
```