# docker 批量导入镜像包

```
for i in ./*.tar ; do docker load < $i ; done
```