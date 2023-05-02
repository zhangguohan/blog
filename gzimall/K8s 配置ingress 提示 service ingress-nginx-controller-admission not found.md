# K8s 配置ingress 提示 service "ingress-nginx-controller-admission" not found

```
 我以前使用清单安装过nginx-ingress。我删除了它创建的命名空间，以及文档中提到的clusterrole和clusterrolebinding，但这并没有删除安装在清单中的ValidatingWebhookConfiguration，但在默认使用helm时没有删除。
```

一、之前已经安装ingress然后重新部署Ingress

```
[root@k8s-master01 05]# kubectl apply -f  nginx-ingress.yaml 
Warning: networking.k8s.io/v1beta1 Ingress is deprecated in v1.19+, unavailable in v1.22+; use networking.k8s.io/v1 Ingress
Error from server (InternalError): error when creating "nginx-ingress.yaml": Internal error occurred: failed calling webhook "validate.nginx.ingress.kubernetes.io": Post "https://ingress-nginx-controller-admision.ingress-nginx.svc:443/networking/v1/ingresses?timeout=10s": service "ingress-nginx-controller-admission" not found
```

二、删除ValidatingWebhookConfiguration

```
[root@k8s-master01 05]# kubectl delete -A ValidatingWebhookConfiguration ingress-nginx-admission
validatingwebhookconfiguration.admissionregistration.k8s.io "ingress-nginx-admission" deleted
```

三、重新执行部署nginx-ingress服务
[root@k8s-master01 05]# kubectl apply -f nginx-ingress.yaml