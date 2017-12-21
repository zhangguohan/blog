

## Nginx配置Location block说明

-----

### The = modifier模式。
所请求的文档URI必须与指定的模式完全匹配。该模式这里有一个简单的文字字符串;你不能使用正则表达式:

````
server {
server_name website.com;
location = /abcd {
                                […]
                           }
        }
        
````
这样配置在Location block中：

> - 支持访问  http://website.com/abcd (exact match)
> - 支持访问 http://website.com/ABCD (操作系统大小写分别)
> - 支技访问：http://website.com/abcd?param1&param2 (表达式及参数).
> - 不支持问：http://website.com/abcde (扩展字符)