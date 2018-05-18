
# Image Search Java Sample Web API


## Development

### Prepare image search environment variables

```bash
export REGION_ID=ap-southeast-1
export ACCESS_KEY_ID=XXXXXXXXXXXXXXXX
export ACCESS_KEY_SECRET=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
export INSTANCE_NAME=your_instance_name
```

### Create WAR file

```bash
# this will create target/image-search-webapp.war
mvn package
```

### Run Tomcat locally

```bash
mvn tomcat7:run
# GET returns default result
open http://localhost:8080/image-search-webapp/search_picture
# POST from form
open http://localhost:8080/image-search-webapp/check.html
```

### Run Tomcat by Docker

```bash
docker-compose up -d
docker-compose ps
docker-compose logs -f web
open http://localhost/image-search-webapp/search_picture
open http://localhost/image-search-webapp/check.html
# go into container for debug
docker exec -it image-search-webapp bash
```

* `Dockerfile` を編集した後は `docker-compose up -d --build`

### Stop Tomcat by Docker

```bash
docker-compose stop
docker-compose down     # stop and remove
docker-compose ps
```


## Deploy

Quick deploy using docker machine

### Create docker machine

* install Go Language https://golang.org/doc/install
* create docker virtual machine

```bash
# this will create ~/go/bin/docker-machine-driver-aliyunecs
go get -u github.com/AliyunContainerService/docker-machine-driver-aliyunecs
export ECS_ACCESS_KEY_ID=XXXXXXXXXXXXXXXX
export ECS_ACCESS_KEY_SECRET=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
export ECS_REGION=ap-southeast-1
docker-machine create --driver aliyunecs image-search-webapp
docker-machine ls
# you should see a new virtual machine
```

* in case failed, try `docker-machine rm -f image-search-webapp`

### Run Tomcat remotely

```bash
eval $(docker-machine env image-search-webapp)
docker-compose up -d
docker-compose ps
docker-compose logs -f web
IP=$(docker-machine ip image-search-webapp)
open http://${IP}/image-search-webapp/search_picture
open http://${IP}/image-search-webapp/check.html
# ssh to the machine for debug
docker-machine ssh image-search-webapp
# go into container for debug
docker exec -it image-search-webapp bash
```

### Clean up

```bash
docker-compose stop
docker-compose down
eval $(docker-machine env -u)
docker-machine stop image-search-webapp 
docker-machine rm image-search-webapp
```


## Note

* `@WebServlet("/search_picture")` により `WEB-INF/web.xml` の servlet 定義は省略可能。

### Maven project from scratch

* IntelliJ IDEA インストール https://www.jetbrains.com/idea/download/ 
* Maven | Create from archetype にチェック | maven-archetype-webapp
* Run | Edit Configurations | + Maven をクリック | Runner タブで環境変数を設定可能

### Install specific version of Docker

```bash
docker-machine ssh image-search-webapp
apt-get update
lsb_release -a
apt list --installed | grep docker
apt-cache madison docker-ce
apt-get install docker-ce=18.03.1~ce-0~ubuntu
```


## Reference

* https://www.alibabacloud.com/help/doc-detail/71238.htm
* https://docs.docker.com/install/linux/docker-ce/ubuntu/