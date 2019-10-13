
### 查询集群状态信息

~~~
[root@docker01 tmp]# kubectl cluster-info
Kubernetes master is running at https://108.88.3.112:6443
KubeDNS is running at https://108.88.3.112:6443/api/v1/namespaces/kube-system/services/kube-dns:dns/proxy

To further debug and diagnose cluster problems, use 'kubectl cluster-info dump'.
[root@docker01 tmp]#
~~~

#### 快速新建简单nginux测试

~~~

[root@docker01 tmp]# kubectl  run nginx-deploy --image=nginx:1.14-alpine --port=80 --replicas=1 


kubectl run --generator=deployment/apps.v1 is DEPRECATED and will be removed in a future version. Use kubectl run --generator=run-pod/v1 or kubectl create instead.
deployment.apps/nginx-deploy created

[root@docker01 tmp]# kubectl get deployment

NAME           READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deploy   0/1     1            0           19s
[root@docker01 tmp]# kubectl get deployment
NAME           READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deploy   1/1     1            1           63s

[root@docker01 tmp]# kubectl get pods

NAME                           READY   STATUS    RESTARTS   AGE
nginx-deploy-55d8d67cf-wb85w   1/1     Running   0          81s

[root@docker01 tmp]# kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
nginx-deploy-55d8d67cf-wb85w   1/1     Running   0          98s

[root@docker01 tmp]# 

~~~ 


### 查看pods状态


~~~

[root@docker01 tmp]# kubectl get pods -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP           NODE       NOMINATED NODE   READINESS GATES
nginx-deploy-55d8d67cf-wb85w   1/1     Running   0          3m50s   10.244.1.2   docker02   <none>           <none>


测试nginx状态


[root@docker01 tmp]# curl 10.244.1.2

<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
[root@docker01 tmp]# 

~~~


### 删除pods 重建


~~~

[root@docker01 tmp]# kubectl get pods

NAME                           READY   STATUS    RESTARTS   AGE
nginx-deploy-55d8d67cf-wb85w   1/1     Running   0          8m27s

#删除pods

[root@docker01 tmp]# kubectl delete pods nginx-deploy-55d8d67cf-wb85w

pod "nginx-deploy-55d8d67cf-wb85w" deleted

##自动重建

[root@docker01 tmp]# kubectl get pods

NAME                           READY   STATUS              RESTARTS   AGE
nginx-deploy-55d8d67cf-6j2c8   0/1     ContainerCreating   0          22s

[root@docker01 tmp]# kubectl get pods

NAME                           READY   STATUS              RESTARTS   AGE
nginx-deploy-55d8d67cf-6j2c8   0/1     ContainerCreating   0          25s
[root@docker01 tmp]# 


[root@docker01 tmp]# kubectl get pods -o wide

NAME                           READY   STATUS    RESTARTS   AGE    IP           NODE       NOMINATED NODE   READINESS GATES
nginx-deploy-55d8d67cf-6j2c8   1/1     Running   0          113s   10.244.2.2   docker03   <none>           <none>
[root@docker01 tmp]# kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
nginx-deploy-55d8d67cf-6j2c8   1/1     Running   0          2m2s
[root@docker01 tmp]#

~~~


#### 创建service

~~~

新建一个service 用于对外访问

[root@docker01 ~]# kubectl expose deployment nginx-deploy --name=nginx --port=80 --target-port=80
service/nginx exposed
[root@docker01 ~]#


[root@docker01 ~]# kubectl describe svc nginx
Name:              nginx
Namespace:         default
Labels:            run=nginx-deploy
Annotations:       <none>
Selector:          run=nginx-deploy
Type:              ClusterIP
IP:                10.110.131.216
Port:              <unset>  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.2.3:80
Session Affinity:  None
Events:            <none>
[root@docker01 ~]# 

~~~ 


#### 扩展pods测试

~~~

定议一个deploymenet 最少为2个pods

[root@docker01 ~]# kubectl  run nginx-scal --image=nginx:1.14-alpine --port=80 --replicas=2 

kubectl run --generator=deployment/apps.v1 is DEPRECATED and will be removed in a future version. Use kubectl run --generator=run-pod/v1 or kubectl create instead.
deployment.apps/nginx-scal created


查看deployment  nginx-scal

[root@docker01 ~]# kubectl describe deployment nginx-scal

Name:                   nginx-scal
Namespace:              default
CreationTimestamp:      Thu, 20 Jun 2019 10:56:37 -0400
Labels:                 run=nginx-scal
Annotations:            deployment.kubernetes.io/revision: 1
Selector:               run=nginx-scal
Replicas:               2 desired | 2 updated | 2 total | 0 available | 2 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  run=nginx-scal
  Containers:
   nginx-scal:
    Image:        nginx:1.14-alpine
    Port:         80/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      False   MinimumReplicasUnavailable
  Progressing    True    ReplicaSetUpdated
OldReplicaSets:  <none>
NewReplicaSet:   nginx-scal-5b4bdc9775 (2/2 replicas created)
Events:
  Type    Reason             Age   From                   Message
  ----    ------             ----  ----                   -------
  Normal  ScalingReplicaSet  13s   deployment-controller  Scaled up replica set nginx-scal-5b4bdc9775 to 2



[root@docker01 ~]# kubectl  get pods 

NAME                           READY   STATUS    RESTARTS   AGE
client                         0/1     Error     0          39m
nginx-deploy-55d8d67cf-zn85v   1/1     Running   0          34m
nginx-scal-5b4bdc9775-9wqxw    1/1     Running   0          23s
nginx-scal-5b4bdc9775-bhfjj    1/1     Running   0          23s
[root@docker01 ~]# 


### 扩展4个 pods

[root@docker01 ~]# kubectl scale --replicas=4 deployment/nginx-scal
deployment.extensions/nginx-scal scaled
[root@docker01 ~]#  



[root@docker01 ~]# kubectl  get pods -o wide
NAME                           READY   STATUS    RESTARTS   AGE     IP           NODE       NOMINATED NODE   READINESS GATES
client                         0/1     Error     0          56m     10.244.1.3   docker02   <none>           <none>
client2                        1/1     Running   0          8m28s   10.244.1.5   docker02   <none>           <none>
nginx-deploy-55d8d67cf-zn85v   1/1     Running   0          51m     10.244.2.3   docker03   <none>           <none>
nginx-scal-5b4bdc9775-759ht    1/1     Running   0          51s     10.244.2.5   docker03   <none>           <none>
nginx-scal-5b4bdc9775-9wqxw    1/1     Running   0          17m     10.244.2.4   docker03   <none>           <none>
nginx-scal-5b4bdc9775-bhfjj    1/1     Running   0          17m     10.244.1.4   docker02   <none>           <none>
nginx-scal-5b4bdc9775-dchdw    1/1     Running   0          51s     10.244.1.6   docker02   <none>           <none>
[root@docker01 ~]# 

~~~


### 更换image版本

~~~

### 更换容器imgage版本为 latest

[root@docker01 ~]# kubectl set image deployment nginx-scal nginx-scal=nginx:latest

deployment.extensions/nginx-scal image updated


查看更换进度

[root@docker01 ~]# kubectl rollout status deployment nginx-scal

Waiting for deployment "nginx-scal" rollout to finish: 2 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 2 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 2 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 2 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 2 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 3 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 3 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 3 out of 4 new replicas have been updated...
Waiting for deployment "nginx-scal" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "nginx-scal" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "nginx-scal" rollout to finish: 1 old replicas are pending termination...
Waiting for deployment "nginx-scal" rollout to finish: 3 of 4 updated replicas are available...
deployment "nginx-scal" successfully rolled out
[root@docker01 ~]# 


查看当前使用image版本

[root@docker01 ~]# kubectl describe deployment nginx-scal
Name:                   nginx-scal
Namespace:              default
CreationTimestamp:      Thu, 20 Jun 2019 10:56:37 -0400
Labels:                 run=nginx-scal
Annotations:            deployment.kubernetes.io/revision: 2
Selector:               run=nginx-scal
Replicas:               4 desired | 4 updated | 4 total | 4 available | 0 unavailable
StrategyType:           RollingUpdate
MinReadySeconds:        0
RollingUpdateStrategy:  25% max unavailable, 25% max surge
Pod Template:
  Labels:  run=nginx-scal
  Containers:
   nginx-scal:
    Image:        nginx:latest
    Port:         80/TCP
    Host Port:    0/TCP
    Environment:  <none>
    Mounts:       <none>
  Volumes:        <none>
Conditions:
  Type           Status  Reason
  ----           ------  ------
  Available      True    MinimumReplicasAvailable
  Progressing    True    NewReplicaSetAvailable
OldReplicaSets:  <none>
NewReplicaSet:   nginx-scal-6d77dd584d (4/4 replicas created)
Events:
  Type    Reason             Age    From                   Message
  ----    ------             ----   ----                   -------
  Normal  ScalingReplicaSet  30m    deployment-controller  Scaled up replica set nginx-scal-5b4bdc9775 to 2
  Normal  ScalingReplicaSet  13m    deployment-controller  Scaled up replica set nginx-scal-5b4bdc9775 to 4
  Normal  ScalingReplicaSet  3m19s  deployment-controller  Scaled up replica set nginx-scal-6d77dd584d to 1
  Normal  ScalingReplicaSet  3m18s  deployment-controller  Scaled down replica set nginx-scal-5b4bdc9775 to 3
  Normal  ScalingReplicaSet  3m18s  deployment-controller  Scaled up replica set nginx-scal-6d77dd584d to 2
  Normal  ScalingReplicaSet  2m10s  deployment-controller  Scaled down replica set nginx-scal-5b4bdc9775 to 2
  Normal  ScalingReplicaSet  2m9s   deployment-controller  Scaled up replica set nginx-scal-6d77dd584d to 3
  Normal  ScalingReplicaSet  2m5s   deployment-controller  Scaled down replica set nginx-scal-5b4bdc9775 to 1
  Normal  ScalingReplicaSet  2m4s   deployment-controller  Scaled up replica set nginx-scal-6d77dd584d to 4
  Normal  ScalingReplicaSet  107s   deployment-controller  Scaled down replica set nginx-scal-5b4bdc9775 to 0
[root@docker01 ~]# 

~~~


### 更新image作业回退

~~~

执行回退更换image

[root@docker01 ~]# kubectl rollout undo deployment nginx-scal

deployment.extensions/nginx-scal rolled back

[root@docker01 ~]# kubectl  get pods -o wide

NAME                           READY   STATUS              RESTARTS   AGE     IP           NODE       NOMINATED NODE   READINESS GATES
client                         0/1     Error               0          71m     10.244.1.3   docker02   <none>           <none>
client2                        1/1     Running             0          23m     10.244.1.5   docker02   <none>           <none>
nginx-deploy-55d8d67cf-zn85v   1/1     Running             0          66m     10.244.2.3   docker03   <none>           <none>
nginx-scal-5b4bdc9775-p2nmg    0/1     ContainerCreating   0          10s     <none>       docker02   <none>           <none>
nginx-scal-5b4bdc9775-vf2pq    0/1     ContainerCreating   0          12s     <none>       docker03   <none>           <none>
nginx-scal-6d77dd584d-hwjwm    1/1     Running             0          5m36s   10.244.2.6   docker03   <none>           <none>
nginx-scal-6d77dd584d-n6lwn    1/1     Running             0          5m35s   10.244.1.7   docker02   <none>           <none>
nginx-scal-6d77dd584d-w2p6g    1/1     Running             0          4m24s   10.244.2.7   docker03   <none>           <none>
nginx-scal-6d77dd584d-z7wfk    0/1     Terminating         0          4m21s   <none>       docker02   <none>           <none>
[root@docker01 ~]# 

~~~


### 服务公开的多节点服务器访问

~~~

编辑service服务为  type: NodePort 

[root@docker01 ~]# kubectl edit svc myngx

# Please edit the object below. Lines beginning with a '#' will be ignored,
# and an empty file will abort the edit. If an error occurs while saving this file will be
# reopened with the relevant failures.
#
apiVersion: v1
kind: Service
metadata:
  creationTimestamp: "2019-06-20T15:03:50Z"
  labels:
    run: nginx-scal
  name: myngx
  namespace: default
  resourceVersion: "399616"
  selfLink: /api/v1/namespaces/default/services/myngx
  uid: 9ca4f464-936c-11e9-bce0-0800276d546c
spec:
  clusterIP: 10.111.176.51
  externalTrafficPolicy: Cluster
  ports:
  - nodePort: 32486
    port: 80
    protocol: TCP
    targetPort: 80
  selector:
    run: nginx-scal
  sessionAffinity: None
  type: NodePort
status:
  loadBalancer: {}
~                  


查看服务信息

[root@docker01 ~]# kubectl get svc myngx
NAME    TYPE       CLUSTER-IP      EXTERNAL-IP   PORT(S)        AGE
myngx   NodePort   10.111.176.51   <none>        80:32486/TCP   40m
[root@docker01 ~]# 




访问节点服务器测试

[root@docker01 ~]# wget -c http://docker03:32486


--2019-06-20 11:45:41--  http://docker03:32486/
Resolving docker03 (docker03)... 108.88.3.122
Connecting to docker03 (docker03)|108.88.3.122|:32486... connected.
HTTP request sent, awaiting response... 200 OK
Length: 612 [text/html]
Saving to: ‘index.html’

100%[===================================================================================================================================================>] 612         --.-K/s   in 0s      

2019-06-20 11:45:41 (9.40 MB/s) - ‘index.html’ saved [612/612]

[root@docker01 ~]# 

~~~