## Resin-4.0.37设置编码格式

Resin中就要修改配置文件来设置编码格式，方法如下：

打开C:resin-4.0.37confresin.xml文件

```


    <host-default>
      <!-- creates the webapps directory for .war expansion -->
      <web-app-deploy path="webapps"
                      expand-preserve-fileset="WEB-INF/work/**"
                      multiversion-routing="${webapp_multiversion_routing}"
                      path-suffix="${elastic_webapp?resin.id:''}"/>
  <character-encoding>UTF-8</character-encoding>
    </host-default>
```