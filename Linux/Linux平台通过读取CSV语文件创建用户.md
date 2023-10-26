## Linux平台通过读取CSV语文件创建用户

### 一、SFTP.cvs文件

~~~
u41011,Ar4Gu5Eyd9Wd911
u41013,Jb9Sj6Rj0dMw623
u41020,Us0Wu4Zn0CsA033
u41022,To8Vc2Nu4Ukd423

~~~





### 二、Create-user.sh文件

~~~bash
#!/bin/bash

# 设置用户名和密码文件路径
csv_file="SFTP.csv"

# 逐行读取 CSV 文件
while IFS=, read -r username password
do
  # 清理密码字段，确保没有额外的换行符等
  password=$(echo "$password" | tr -d '\r')

  # 检查用户是否已存在
  if id "$username" &>/dev/null; then
    echo "用户 $username 已存在，跳过"
  else
    # 使用useradd命令创建新用户，设置密码
    sudo useradd -m "$username"
    echo "$username:$password" | sudo chpasswd

    # 输出用户已创建成功的消息
    echo "用户 $username 已创建成功"
  fi
done < "$csv_file"


~~~

