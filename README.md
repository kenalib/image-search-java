
# Image Search Java Sample Web API


CircleCI Status:
[![CircleCI](https://circleci.com/gh/kenalib/image-search-java.svg?style=svg)](https://circleci.com/gh/kenalib/image-search-java)


## Sample UI and code

* UI Demo: http://image-search-demo3.oss-ap-northeast-1.aliyuncs.com/
* Front End: https://github.com/kenalib/image-search-react
* Back End: https://github.com/kenalib/image-search-java

* CI status: https://circleci.com/gh/kenalib/image-search-java
* Test coverage report: https://kenalib.github.io/image-search-java/coverage_report/


## Development

### Prepare image search environment variables

```bash
export ACCESS_KEY_ID=XXXXXXXXXXXXXXXX
export ACCESS_KEY_SECRET=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

* update `INSTANCE_NAME` in `src/main/resources/image-search.properties`
* (optional) update `CORS_URL` (default is `*`)

### Prepare images

* c.f. https://www.alibabacloud.com/help/doc-detail/66580.htm
* upload your image files and `increment.meta` on your OSS bucket
* setup RAM role and copy ARN in the `ImageSearchOSSAccessRole`
* open Image Search instance and import the images from OSS
* if needed, select `Reset` before OSS import to clear existing data
* click manage to check OSS Import status to be `Imported`

### Test and build

```bash
mvn test
# this will create target/image-search-webapp.war
mvn package
# this will skip test
mvn package -DskipTests
```

* Test coverage report by using IntelliJ IDEA is below.
* https://kenalib.github.io/image-search-java/coverage_report/

### Run Tomcat locally

```bash
mvn tomcat7:run
# GET returns default result
curl http://localhost:8080/image-search-webapp/search_picture | python -m json.tool
# POST from form
open http://localhost:8080/image-search-webapp/check.html
```

### Run Tomcat by Docker

```bash
# Test
docker-compose -f docker-compose.test.yml run web mvn test
```

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

### Run Tomcat on remote Docker

```bash
eval $(docker-machine env image-search-webapp)
docker-compose up -d

docker-compose ps
docker-compose logs -f web

IP=$(docker-machine ip image-search-webapp)
curl http://${IP}/image-search-webapp/search_picture | python -m json.tool
open http://${IP}/image-search-webapp/check.html

# ssh to the machine for debug
docker-machine ssh image-search-webapp
# go into container for debug
docker exec -it image-search-webapp bash
```


## Auto deploy using CircleCI

* Status: https://circleci.com/gh/kenalib/workflows/image-search-java

### Setup CircleCI 2.0 and docker-compose

* Test build deploy processes are automated on CircleCI 2.0
* see `.circleci/config.yml` for auto deploy using `docker-compose`
* need following environment variables in CircleCI settings

```bash
# for image search
ACCESS_KEY_ID
ACCESS_KEY_SECRET

# c.f. docker-machine env image-search-webapp
DOCKER_TLS_VERIFY=1
DOCKER_CERT_PATH=.
DOCKER_HOST

# .pem files content
DOCKER_CA_PEM
DOCKER_CERT_PEM
DOCKER_KEY_PEM
```

* run `docker-machine env image-search-webapp` to show actual variables.
* for each PEM files, you can copy data to clipboard by below on Mac.
* paste to Value field then delete header and footer in the field.

```bash
cat ~/.docker/machine/machines/image-search-webapp/ca.pem | pbcopy
```

### Clean up

```bash
docker-compose stop
docker-compose down
eval $(docker-machine env -u)
docker-machine stop image-search-webapp 
docker-machine rm image-search-webapp
```


## Misc Note

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
* https://circleci.com/
* https://github.com/mockito/mockito
* https://github.com/kenalib/image-search-react
