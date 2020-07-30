#!/bin/bash


mvn clean package -Dmaven.test.skip=true -U

rm ./docker/*.jar

mv ./target/config-server-1.0-SNAPSHOT.jar ./docker

echo '开始构建镜像。。。。'
docker build -t config-server ./docker
# -t 给镜像起个名字（eureka-test）
# ./docker 使用的Dockerfile文件的位置

echo '完成构建镜像。。。。'
echo '开始启动镜像。。。。'

docker run -d -p 13324:3324 config-server

# -d参数是让容器后台运行
# -p 是做端口映射，此时将服务器中的13324端口映射到容器中的3324(项目中端口配置的是3324)端口
