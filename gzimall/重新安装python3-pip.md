重新安装python3-pip



我在这里使用了这个技巧： [https](https://link.segmentfault.com/?enc=SzZn7jPJq8NXlIVHf6ixUQ%3D%3D.U3TfOzU%2Fz1BoPosSiwMDqjmL1rpOXXF8Db5xvuvENcbP%2FyEHgEjCUph2%2FWdRhYAP) ://askubuntu.com/a/1433089/497392

```dsconfig
 sudo apt remove python3-pip
wget https://bootstrap.pypa.io/get-pip.py
sudo python3 get-pip.py
```

然后重启后：

```ada
 pip install pyopenssl --upgrade
```