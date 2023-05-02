# kubectl命令自动补全功能

## kubectl命令补全工具的安装。

**1：安装bash-completion：**

```
yum install -y bash-completion 
source /usr/share/bash-completion/bash_completion
```

**2：应用kubectl的completion到系统环境：**

```
source <(kubectl completion bash)
echo "source <(kubectl completion bash)" >> ~/.bashrc
```