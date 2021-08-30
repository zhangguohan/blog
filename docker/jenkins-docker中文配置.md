### 切换root用户  
docker exec --user root -it jenkins bash




## 修改jenkins启动配置文件添加对应中文编码为UTF-8及时区

vi /usr/local/bin/jenkins.sh 

~~~

echo "--- Copying files at $(date)" >> "$COPY_REFERENCE_FILE_LOG"
find "${REF}" \( -type f -o -type l \) -exec bash -c '. /usr/local/bin/jenkins-support; for arg; do copy_reference_file "$arg"; done' _ {} +

export JAVA_OPTS="-Dsun.jnu.encoding=UTF-8 -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai"



~~~

