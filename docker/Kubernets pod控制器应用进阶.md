### Kubernets pod控制器应用进阶

#### 查看pods标签
~~~
[root@docker01 ~]# kubectl get pods --show-labels
NAME                           READY   STATUS      RESTARTS   AGE   LABELS
client                         0/1     Error       0          8d    run=client
client2                        0/1     Completed   0          8d    run=client2
nginx-deploy-55d8d67cf-zn85v   1/1     Running     1          8d    pod-template-hash=55d8d67cf,run=nginx-deploy
nginx-scal-5b4bdc9775-jdf9v    1/1     Running     1          8d    pod-template-hash=5b4bdc9775,run=nginx-scal
nginx-scal-5b4bdc9775-mj64h    1/1     Running     1          8d    pod-template-hash=5b4bdc9775,run=nginx-scal
nginx-scal-5b4bdc9775-p2nmg    1/1     Running     1          8d    pod-template-hash=5b4bdc9775,run=nginx-scal
nginx-scal-5b4bdc9775-vf2pq    1/1     Running     1          8d    pod-template-hash=5b4bdc9775,run=nginx-scal
pod-demo                       2/2     Running     34         34h   app=myapp,tier=frontend
[root@docker01 ~]# 

标签过滤



[root@docker01 ~]# kubectl get pods -l app
NAME       READY   STATUS    RESTARTS   AGE
pod-demo   2/2     Running   34         34h
[root@docker01 ~]#


打标签


[root@docker01 ~]# kubectl label pods pod-demo release=tank
pod/pod-demo labeled



[root@docker01 ~]# kubectl get pods -l app --show-labels
NAME       READY   STATUS    RESTARTS   AGE   LABELS
pod-demo   2/2     Running   34         34h   app=myapp,release=tank,tier=frontend
[root@docker01 ~]# 

~~~

