### kubernetes资源清单定义入门

#### 创建资源的方法：
~~~
	apiserver 仅接收JOSN格式的资源定义：
	yaml格式提供配置清单，apiserver可自动将其转为json格式，而后再提交执行
~~~

#### 大部份资源的配置清单：
apiVersion:group/version
	$ kubect api-versions

kind: 资源类别

metadata:元数据
	name
	namespace
	lables
	annotations
	
每个资源组的引用PATH
/api/GROUP/VERSION/namespaces/NAMESPACE/TYPE/NAME

 
Spec：用于期望的状态

status: 当前状态，current state ,由kubenets维护。






### 编写一个yaml资源列表文件

~~~

### app-deaom.yaml
apiVersion: v1
kind: Pod
metadata:
  name: pod-demo
  namespace: default
  labels:
       app: myapp
       tier: frontend
spec:
  containers:
  - name: myapp
    image: nginx:latest
  - name: busybox
    image: busybox:latest
    command:
         - "/bin/sh"
         - "-c"
         - "sleep 3600"


~~~

### 使用yaml文件生成集群资源

~~~
[root@docker01 ~]# kubectl create -f app-deaom.yaml 
pod/pod-demo created
[root@docker01 ~]# 


[root@docker01 ~]# kubectl get pods
NAME                           READY   STATUS      RESTARTS   AGE
client                         0/1     Error       0          6d19h
client2                        0/1     Completed   0          6d19h
nginx-deploy-55d8d67cf-zn85v   1/1     Running     1          6d19h
nginx-scal-5b4bdc9775-jdf9v    1/1     Running     1          6d18h
nginx-scal-5b4bdc9775-mj64h    1/1     Running     1          6d18h
nginx-scal-5b4bdc9775-p2nmg    1/1     Running     1          6d18h
nginx-scal-5b4bdc9775-vf2pq    1/1     Running     1          6d18h
pod-demo                       2/2     Running     0          2m28s
[root@docker01 ~]# 





~~~
