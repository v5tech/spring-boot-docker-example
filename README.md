# spring-boot-docker-example

### 1、spotify公司docker-maven-plugin使用

* build

```bash
mvn clean package docker:build
```
* build & push

```bash
mvn clean package docker:build -DpushImage
```

### 2、fabric8公司docker-maven-plugin使用

* build

```bash
mvn clean package docker:build
```
* run

```bash
mvn clean package docker:build docker:run
```
* stop

```bash
mvn docker:stop
```

### 3、docker-compose

`docker-compose.yml`文件中使用到了`ameizi/spring-boot-docker-example`镜像，因此首先需要进行`ameizi/spring-boot-docker-example`镜像的构建

在`docker-compose.yml`目录中执行如下命令

```bash
docker-compose up -d
```

```bash
docker-compose stop
```

### 参考文档

https://docs.docker.com/engine/reference/builder/

https://docs.docker.com/compose/compose-file/

https://github.com/spotify/docker-maven-plugin

https://dmp.fabric8.io