## Nginx配置proxy_pass转发的/路径问题


### 在nginx中配置proxy_pass时，如果是按照^~匹配路径时,要注意proxy_pass后的url最后的/,当加上了/，相当于是绝对根路径，
则nginx不会把location中匹配的路径部分代理走;如果没有/，则会把匹配的路径部分也给代理走。

````
location ^~ /static_js/
{
proxy_cache js_cache;
proxy_set_header Host js.test.com;
proxy_pass http://js.test.com/;
}
````

### 如上面的配置，如果请求的url是http://servername/static_js/test.html会被代理成http://js.test.com/test.html
而如果这么配置
````
location ^~ /static_js/
{
proxy_cache js_cache;
proxy_set_header Host js.test.com;
proxy_pass http://js.test.com;
}
````
则会被代理到http://js.test.com/static_js/test.htm


### 当然，我们可以用如下的rewrite来实现/的功能
````
location ^~ /static_js/
{
proxy_cache js_cache;
proxy_set_header Host js.test.com;
rewrite /static_js/(.+)$ /$1 break;
proxy_pass http://js.test.com;
} 

````
