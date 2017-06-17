# harbor私服搭建

### 为ubuntu添加163镜像源

在`/etc/apt/sources.list`文件头部添加如下内容

```bash
sudo vim /etc/apt/sources.list
deb http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse
deb http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse
deb http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse
deb http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse
deb-src http://mirrors.163.com/ubuntu/ trusty main restricted universe multiverse
deb-src http://mirrors.163.com/ubuntu/ trusty-security main restricted universe multiverse
deb-src http://mirrors.163.com/ubuntu/ trusty-updates main restricted universe multiverse
deb-src http://mirrors.163.com/ubuntu/ trusty-proposed main restricted universe multiverse
deb-src http://mirrors.163.com/ubuntu/ trusty-backports main restricted universe multiverse
```

http://mirrors.163.com/.help/ubuntu.html

http://mirrors.163.com/.help/sources.list.trusty

更新使其生效

```bash
sudo apt-get update
```

### 安装docker

#### 卸载旧版本

```bash
sudo apt-get remove docker docker-engine
```

#### 安装前配置

```bash
sudo apt-get -y install \
  apt-transport-https \
  ca-certificates \
  curl \
  python-pip
```

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
```

```bash
sudo add-apt-repository \
       "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
       $(lsb_release -cs) \
       stable"
     
sudo apt-get update
```

#### 安装docker

```bash
sudo apt-get -y install docker-ce
```

#### 测试docker

```bash
sudo docker run hello-world
```

#### 解决docker只能以`sudo`模式运行

```bash
sudo groupadd docker
sudo usermod -aG docker $USER
sudo service docker restart
```

#### 配置阿里云Docker加速器

```bash
echo "DOCKER_OPTS=\"\$DOCKER_OPTS --registry-mirror=https://ex93eg1r.mirror.aliyuncs.com\"" | sudo tee -a /etc/default/docker
sudo service docker restart
```

*注意*

ubuntu环境下docker的配置文件路径为`/etc/default/docker`(可参考`/etc/init.d/docker`文件中的配置)

#### docker开启2376或2375监听端口

ubuntu环境中修改/etc/default/docker文件后重启docker服务

```
DOCKER_OPTS="$DOCKER_OPTS --registry-mirror=https://ex93eg1r.mirror.aliyuncs.com -H unix:///var/run/docker.sock -H tcp://0.0.0.0:2376"
```

重启docker服务即可

### 安装docker-compose

第一种方法

```bash
curl -L https://github.com/docker/compose/releases/download/1.13.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose

chmod +x /usr/local/bin/docker-compose

docker-compose --version
```

第二种方法

使用`pip`安装

```bash
sudo pip install docker-compose
```

### harbor安装及配置

#### 下载解压

下载其安装包https://github.com/vmware/harbor/releases

当前最新版本为`harbor-online-installer-v1.1.2.tgz`

```bash
sudo tar zxvf harbor-online-installer-v1.1.2.tgz -C /usr/local/
```

解压后其目录结构如下

```bash
root@docker:/usr/local/harbor# pwd
/usr/local/harbor
root@docker:/usr/local/harbor# ls -lah
drwxrwxrwx 1 vagrant vagrant 4.0K Jun 15 13:56 .
drwxrwxrwx 1 vagrant vagrant    0 Jun 15 13:56 ..
drwxrwxrwx 1 vagrant vagrant    0 Jun 15 13:56 common
-rwxrwxrwx 1 vagrant vagrant 2.0K Jun 15 13:56 docker-compose.notary.yml
-rwxrwxrwx 1 vagrant vagrant 3.1K Jun 15 13:56 docker-compose.yml
-rwxrwxrwx 1 vagrant vagrant 4.3K Jun 15 13:56 harbor_1_1_0_template
-rwxrwxrwx 1 vagrant vagrant 4.0K Jun 15 13:56 harbor.cfg
-rwxrwxrwx 1 vagrant vagrant 5.1K Jun 15 13:56 install.sh
-rwxrwxrwx 1 vagrant vagrant 330K Jun 15 13:56 LICENSE
-rwxrwxrwx 1 vagrant vagrant  472 Jun 15 13:56 NOTICE
-rwxrwxrwx 1 vagrant vagrant  17K Jun 15 13:56 prepare
-rwxrwxrwx 1 vagrant vagrant 4.5K Jun 15 13:56 upgrade
root@docker:/usr/local/harbor# 
```

#### 修改`harbor.cfg`配置

修改`/usr/local/harbor/harbor.cfg`文件中下列参数值如下所示

```
hostname = 192.168.31.228     # 这里修改为本机IP(运行docker服务机器的IP)，禁止使用127.0.0.1
ui_url_protocol = http      # 保持默认http协议，生产环境建议修改为https
db_password = root      # 数据库密码
harbor_admin_password = admin # 管控台admin用户名密码
```

注意：`hostname`配置项为运行docker服务的机器IP地址

#### 修改`/etc/default/docker`文件

为`DOCKER_OPTS`添加`--insecure-registry=192.168.31.228`解决http模式docker拒绝访问的问题

如下所示

```
DOCKER_OPTS="$DOCKER_OPTS --registry-mirror=https://ex93eg1r.mirror.aliyuncs.com --insecure-registry=192.168.31.228"
```

重启docker服务

```bash
sudo service docker restart
```

详情参考https://github.com/vmware/harbor/blob/master/docs/user_guide.md

#### 安装harbor

```bash
root@docker:/usr/local/harbor# ./install.sh 

[Step 0]: checking installation environment ...

Note: docker version: 17.03.1

Note: docker-compose version: 1.13.0


[Step 1]: preparing environment ...
Clearing the configuration file: ./common/config/jobservice/env
Clearing the configuration file: ./common/config/jobservice/app.conf
Clearing the configuration file: ./common/config/ui/env
Clearing the configuration file: ./common/config/ui/private_key.pem
Clearing the configuration file: ./common/config/ui/app.conf
Clearing the configuration file: ./common/config/nginx/nginx.conf
Clearing the configuration file: ./common/config/db/env
Clearing the configuration file: ./common/config/adminserver/env
Clearing the configuration file: ./common/config/registry/root.crt
Clearing the configuration file: ./common/config/registry/config.yml
loaded secret from file: /data/secretkey
Generated configuration file: ./common/config/nginx/nginx.conf
Generated configuration file: ./common/config/adminserver/env
Generated configuration file: ./common/config/ui/env
Generated configuration file: ./common/config/registry/config.yml
Generated configuration file: ./common/config/db/env
Generated configuration file: ./common/config/jobservice/env
Generated configuration file: ./common/config/jobservice/app.conf
Generated configuration file: ./common/config/ui/app.conf
Generated certificate, key file: ./common/config/ui/private_key.pem, cert file: ./common/config/registry/root.crt
The configuration files are ready, please use docker-compose to start the service.


[Step 2]: checking existing instance of Harbor ...


[Step 3]: starting Harbor ...
Creating network "harbor_harbor" with the default driver
Pulling log (vmware/harbor-log:v1.1.2)...
v1.1.2: Pulling from vmware/harbor-log
93b3dcee11d6: Pull complete
d31900e63a3f: Pull complete
d267ee2912d5: Pull complete
827766337aa5: Pull complete
c0f98490f831: Pull complete
Digest: sha256:2de84ff1c41d6277203a2f70b66704ff18cd99fa29958131ea4b350656826d65
Status: Downloaded newer image for vmware/harbor-log:v1.1.2
Pulling adminserver (vmware/harbor-adminserver:v1.1.2)...
v1.1.2: Pulling from vmware/harbor-adminserver
93b3dcee11d6: Already exists
73cee1677514: Pull complete
a0fb654d0080: Pull complete
f9d9f0947564: Pull complete
Digest: sha256:4e73cda76633d39ed000f812923208a7652da9e51e85143bb9939ff91d8fe7fa
Status: Downloaded newer image for vmware/harbor-adminserver:v1.1.2
Pulling registry (vmware/registry:2.6.1-photon)...
2.6.1-photon: Pulling from vmware/registry
93b3dcee11d6: Already exists
d9573f25cba0: Pull complete
e4dfb2b317a8: Pull complete
9b43c0ce6f50: Pull complete
Digest: sha256:f9183e3c721ff9703c26b816e2a7b4cb39349ddf3ce6b9c90a626f5bf6399b77
Status: Downloaded newer image for vmware/registry:2.6.1-photon
Pulling ui (vmware/harbor-ui:v1.1.2)...
v1.1.2: Pulling from vmware/harbor-ui
93b3dcee11d6: Already exists
73cee1677514: Already exists
7af31ef2857c: Pull complete
857d22952c74: Pull complete
a7aa89c1f4b2: Pull complete
86ef9f49b776: Pull complete
a9827ecae302: Pull complete
8456b9209c9c: Pull complete
Digest: sha256:4088e9ab876a3a821a5548578b00da9c6cdd5e43434b1afab106f9199723bd14
Status: Downloaded newer image for vmware/harbor-ui:v1.1.2
Pulling mysql (vmware/harbor-db:v1.1.2)...
v1.1.2: Pulling from vmware/harbor-db
6d827a3ef358: Pull complete
ed0929eb7dfe: Pull complete
03f348dc3b9d: Pull complete
fd337761ca76: Pull complete
ac3f5f870257: Pull complete
38a247b5bcdf: Pull complete
8d528ca18a06: Pull complete
70601d0f6e97: Pull complete
1d7a793f527d: Pull complete
15e9fd86591a: Pull complete
79b5a6ccbd39: Pull complete
831d582888b7: Pull complete
8d1e15502c2a: Pull complete
eb434983945e: Pull complete
Digest: sha256:01f73b927b8160c95230acbc4bfe0c023ffa0426b30155cae5a3c04819965a24
Status: Downloaded newer image for vmware/harbor-db:v1.1.2
Pulling jobservice (vmware/harbor-jobservice:v1.1.2)...
v1.1.2: Pulling from vmware/harbor-jobservice
93b3dcee11d6: Already exists
73cee1677514: Already exists
3218403731ec: Pull complete
Digest: sha256:c18a027f90f118ffd0077c3cbdb55002bd1219c458d715ca22fd379e4aa36933
Status: Downloaded newer image for vmware/harbor-jobservice:v1.1.2
Pulling proxy (vmware/nginx:1.11.5-patched)...
1.11.5-patched: Pulling from vmware/nginx
386a066cd84a: Pull complete
7bdb4b002d7f: Pull complete
49b006ddea70: Pull complete
4baf3c4768f5: Pull complete
Digest: sha256:07cd4b73ec64e12581399c4ab7c523553955946a02bba2be715c4f02b97bdf86
Status: Downloaded newer image for vmware/nginx:1.11.5-patched
Creating harbor-log ... 
Creating harbor-log ... done
Creating harbor-db ... 
Creating registry ... 
Creating harbor-adminserver ... 
Creating registry
Creating harbor-db
Creating harbor-adminserver ... done
Creating harbor-ui ... 
Creating harbor-ui ... done
Creating harbor-jobservice ... 
Creating nginx ... 
Creating harbor-jobservice
Creating nginx ... done

✔ ----Harbor has been installed and started successfully.----

Now you should be able to visit the admin portal at http://192.168.31.228. 
For more details, please visit https://github.com/vmware/harbor .
```

经过漫长的等待后，安装完毕。浏览器访问http://192.168.31.228 admin/admin

![login](https://user-images.githubusercontent.com/887836/27187726-84f4c4fe-521e-11e7-848c-42902802fc47.png)

#### 查看docker容器运行情况

```bash
root@docker:/usr/local/harbor# docker ps -a
CONTAINER ID        IMAGE                              COMMAND                  CREATED             STATUS              PORTS                                                              NAMES
3cb48da05559        vmware/nginx:1.11.5-patched        "nginx -g 'daemon ..."   4 minutes ago       Up 4 minutes        0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp, 0.0.0.0:4443->4443/tcp   nginx
83804e76ec07        vmware/harbor-jobservice:v1.1.2    "/harbor/harbor_jo..."   4 minutes ago       Up 4 minutes                                                                           harbor-jobservice
90fca63f0940        vmware/harbor-ui:v1.1.2            "/harbor/harbor_ui"      4 minutes ago       Up 4 minutes                                                                           harbor-ui
182e4a734b2d        vmware/harbor-adminserver:v1.1.2   "/harbor/harbor_ad..."   4 minutes ago       Up 4 minutes                                                                           harbor-adminserver
2409dc8a043b        vmware/harbor-db:v1.1.2            "docker-entrypoint..."   4 minutes ago       Up 4 minutes        3306/tcp                                                           harbor-db
6dd5fad55419        vmware/registry:2.6.1-photon       "/entrypoint.sh se..."   4 minutes ago       Up 4 minutes        5000/tcp                                                           registry
ae50bbf83368        vmware/harbor-log:v1.1.2           "/bin/sh -c 'crond..."   4 minutes ago       Up 4 minutes        127.0.0.1:1514->514/tcp                                            harbor-log
```

#### 命令行登录私服

```bash
root@docker:/usr/local/harbor# docker login 192.168.31.228
Username (admin): admin
Password: 
Login Succeeded
```

#### 向私服推送镜像

```bash
root@docker:/usr/local/harbor# docker tag redis:latest 192.168.31.228/library/redis:latest
root@docker:/usr/local/harbor# docker images -a
REPOSITORY                     TAG                 IMAGE ID            CREATED             SIZE
vmware/harbor-jobservice       v1.1.2              4ef0a7a33734        3 days ago          163 MB
vmware/harbor-ui               v1.1.2              4ee8f190f366        3 days ago          183 MB
vmware/harbor-adminserver      v1.1.2              cdcf1bed7eb4        3 days ago          142 MB
vmware/harbor-db               v1.1.2              fcb8aa7a0640        3 days ago          329 MB
192.168.31.228/library/redis   latest              83744227b191        6 days ago          98.9 MB
redis                          latest              83744227b191        6 days ago          98.9 MB
vmware/registry                2.6.1-photon        0f6c96580032        4 weeks ago         150 MB
vmware/nginx                   1.11.5-patched      8ddadb143133        2 months ago        199 MB
vmware/harbor-log              v1.1.2              9c46a7b5e517        3 months ago        192 MB
root@docker:/usr/local/harbor# docker push 192.168.31.228/library/redis
The push refers to a repository [192.168.31.228/library/redis]
ebfb0a55a275: Pushed 
1213cad8924b: Pushed 
8ae00f04131b: Pushed 
e74a993fa648: Pushed 
3c8f219ed9b6: Pushed 
414f472e5061: Pushed 
latest: digest: sha256:6022356f9d729c858000fc10fc1b09d1624ba099227a0c5d314f7461c2fe6020 size: 1571
```

![redis-1](https://user-images.githubusercontent.com/887836/27187757-9569f57a-521e-11e7-986d-55579c66b951.png)

![redis-2](https://user-images.githubusercontent.com/887836/27187769-9bf0b46a-521e-11e7-8cfa-218cfab52147.png)

### Harbor作为mirror registry

建议使用root用户操作，镜像仓库不允许push操作，只作为官方仓库缓存

#### 修改`templates/registry/config.yml`文件，在文件末尾添加如下内容：

```
proxy:
  remoteurl: https://registry-1.docker.io
```

#### 修改`/etc/default/docker`文件

```
DOCKER_OPTS="$DOCKER_OPTS --registry-mirror=http://192.168.31.228 --insecure-registry=192.168.31.228"
```

#### 重启docker

```
sudo service docker restart 
```

#### 重新部署

```bash
docker-compose down
sudo service docker restart
sudo ./install.sh
```

#### push镜像到私服

```
$ docker push 192.168.31.228/library/redis
The push refers to a repository [192.168.31.228/library/redis]
ebfb0a55a275: Layer already exists 
1213cad8924b: Layer already exists 
8ae00f04131b: Layer already exists 
e74a993fa648: Layer already exists 
3c8f219ed9b6: Layer already exists 
414f472e5061: Waiting 
denied: requested access to the resource is denied
```

注意

* Harbor作为mirror服务器时只能pull不能push

* Harbor作为mirror服务器时不能从ui上删除镜像仓库

### Harbor开启https配置

注意

* 以下操作如无说明均在`/usr/local/harbor/ssl`目录操作，即在`/usr/local/harbor`目录下创建`ssl`目录

* 使用root用户操作

#### 使用openssl创建证书

1 创建CA

```bash
openssl req \
    -newkey rsa:4096 -nodes -sha256 -keyout ca.key \
    -x509 -days 365 -out ca.crt
```

具体操作如下

```
openssl req \
>     -newkey rsa:4096 -nodes -sha256 -keyout ca.key \
>     -x509 -days 365 -out ca.crt
Generating a 4096 bit RSA private key
...................................................................................................++
.............................++
writing new private key to 'ca.key'
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:CN
State or Province Name (full name) [Some-State]:Beijing
Locality Name (eg, city) []:Beijing
Organization Name (eg, company) [Internet Widgits Pty Ltd]:ameizi
Organizational Unit Name (eg, section) []:ameizi
Common Name (e.g. server FQDN or YOUR name) []:registry.ameizi.me
Email Address []:sxyx2008@163.com
root@docker:/usr/local/harbor/ssl$ ll
```

2 创建签名请求

```bash
openssl req \
    -newkey rsa:4096 -nodes -sha256 -keyout registry.ameizi.me.key \
    -out registry.ameizi.me.csr
```

具体操作如下

```
openssl req \
>     -newkey rsa:4096 -nodes -sha256 -keyout registry.ameizi.me.key \
>     -out registry.ameizi.me.csr
Generating a 4096 bit RSA private key
................++
.............................................................++
writing new private key to 'registry.ameizi.me.key'
-----
You are about to be asked to enter information that will be incorporated
into your certificate request.
What you are about to enter is what is called a Distinguished Name or a DN.
There are quite a few fields but you can leave some blank
For some fields there will be a default value,
If you enter '.', the field will be left blank.
-----
Country Name (2 letter code) [AU]:CN
State or Province Name (full name) [Some-State]:Beijing
Locality Name (eg, city) []:Beijing
Organization Name (eg, company) [Internet Widgits Pty Ltd]:ameizi
Organizational Unit Name (eg, section) []:ameizi
Common Name (e.g. server FQDN or YOUR name) []:registry.ameizi.me
Email Address []:sxyx2008@163.com

Please enter the following 'extra' attributes
to be sent with your certificate request
A challenge password []:
An optional company name []:
root@docker:/usr/local/harbor/ssl$ ll
total 24
drwxr-xr-x 2 root root 4096 Jun 16 13:00 ./
drwxr-xr-x 4 root root 4096 Jun 16 12:41 ../
-rw-r--r-- 1 root root 2130 Jun 16 12:57 ca.crt
-rw-r--r-- 1 root root 3272 Jun 16 12:57 ca.key
-rw-r--r-- 1 root root 1756 Jun 16 13:00 registry.ameizi.me.csr
-rw-r--r-- 1 root root 3268 Jun 16 13:00 registry.ameizi.me.key
```

3 签署证书

方案一

初始化CA信息

```
mkdir demoCA
cd demoCA
touch index.txt
echo '01' > serial
cd ../
```

```bash
openssl ca -in registry.ameizi.me.csr -out registry.ameizi.me.crt -cert ca.crt -keyfile ca.key -outdir .
```

具体操作如下

```
root@docker:/usr/local/harbor/ssl# mkdir demoCA
mkdir: cannot create directory ‘demoCA’: File exists
root@docker:/usr/local/harbor/ssl# rm -fr demoCA/
root@docker:/usr/local/harbor/ssl# clear
root@docker:/usr/local/harbor/ssl# mkdir demoCA
root@docker:/usr/local/harbor/ssl# cd demoCA
root@docker:/usr/local/harbor/ssl/demoCA# touch index.txt
root@docker:/usr/local/harbor/ssl/demoCA# echo '01' > serial
root@docker:/usr/local/harbor/ssl/demoCA# cd ../
root@docker:/usr/local/harbor/ssl# openssl ca -in registry.ameizi.me.csr -out registry.ameizi.me.crt -cert ca.crt -keyfile ca.key -outdir .
Using configuration from /usr/lib/ssl/openssl.cnf
Check that the request matches the signature
Signature ok
Certificate Details:
        Serial Number: 1 (0x1)
        Validity
            Not Before: Jun 16 13:15:02 2017 GMT
            Not After : Jun 16 13:15:02 2018 GMT
        Subject:
            countryName               = CN
            stateOrProvinceName       = Beijing
            organizationName          = ameizi
            organizationalUnitName    = ameizi
            commonName                = registry.ameizi.me
            emailAddress              = sxyx2008@163.com
        X509v3 extensions:
            X509v3 Basic Constraints: 
                CA:FALSE
            Netscape Comment: 
                OpenSSL Generated Certificate
            X509v3 Subject Key Identifier: 
                3A:72:98:44:B7:E8:2A:9D:8B:AD:8C:FA:68:00:1A:9C:6E:30:BB:B7
            X509v3 Authority Key Identifier: 
                keyid:4C:40:C7:95:1F:F2:46:17:01:F4:87:B4:5D:2D:CD:5B:9B:5C:70:32

Certificate is to be certified until Jun 16 13:15:02 2018 GMT (365 days)
Sign the certificate? [y/n]:y


1 out of 1 certificate requests certified, commit? [y/n]y
Write out database with 1 new entries
Data Base Updated
```

方案二


```bash
openssl x509 -req -days 365 -in registry.ameizi.me.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out registry.ameizi.me.crt
```

具体操作如下

```
openssl x509 -req -days 365 -in registry.ameizi.me.csr -CA ca.crt -CAkey ca.key -CAcreateserial -out registry.ameizi.me.crt
Signature ok
subject=/C=CN/ST=Beijing/L=Beijing/O=ameizi/OU=ameizi/CN=registry.ameizi.me/emailAddress=sxyx2008@163.com
Getting CA Private Key
root@docker:/usr/local/harbor/ssl$ ll
total 32
drwxr-xr-x 2 root root 4096 Jun 16 13:07 ./
drwxr-xr-x 4 root root 4096 Jun 16 12:41 ../
-rw-r--r-- 1 root root 2130 Jun 16 12:57 ca.crt
-rw-r--r-- 1 root root 3272 Jun 16 12:57 ca.key
-rw-r--r-- 1 root root   17 Jun 16 13:07 ca.srl
-rw-r--r-- 1 root root 2013 Jun 16 13:07 registry.ameizi.me.crt
-rw-r--r-- 1 root root 1756 Jun 16 13:00 registry.ameizi.me.csr
-rw-r--r-- 1 root root 3268 Jun 16 13:00 registry.ameizi.me.key
```

#### 安装配置

修改`harbor.cfg`文件

```
hostname = registry.ameizi.me
ui_url_protocol = https
customize_crt = on
ssl_cert = /data/cert/registry.ameizi.me.crt
ssl_cert_key = /data/cert/registry.ameizi.me.key
secretkey_path = /data
```

拷贝证书到`/data/cert/`目录

```bash
root@docker:/usr/local/harbor# mkdir -p /data/cert/
root@docker:/usr/local/harbor# cp ssl/registry.ameizi.me.crt /data/cert/
root@docker:/usr/local/harbor# cp ssl/registry.ameizi.me.key /data/cert/
root@docker:/usr/local/harbor# ll /data/cert/
total 20
drwxr-xr-x 2 root root 4096 Jun 16 13:30 ./
drw------- 8 root root 4096 Jun 16 13:30 ../
-rw-r--r-- 1 root root 7371 Jun 16 13:30 registry.ameizi.me.crt
-rw-r--r-- 1 root root 3268 Jun 16 13:30 registry.ameizi.me.key
```

#### 执行./install.sh

```bash
root@docker:/usr/local/harbor# ./install.sh 

[Step 0]: checking installation environment ...

Note: docker version: 17.03.1

Note: docker-compose version: 1.13.0


[Step 1]: preparing environment ...
Clearing the configuration file: ./common/config/jobservice/env
Clearing the configuration file: ./common/config/jobservice/app.conf
Clearing the configuration file: ./common/config/ui/env
Clearing the configuration file: ./common/config/ui/private_key.pem
Clearing the configuration file: ./common/config/ui/app.conf
Clearing the configuration file: ./common/config/nginx/nginx.conf
Clearing the configuration file: ./common/config/db/env
Clearing the configuration file: ./common/config/adminserver/env
Clearing the configuration file: ./common/config/registry/root.crt
Clearing the configuration file: ./common/config/registry/config.yml
loaded secret from file: /data/secretkey
Generated configuration file: ./common/config/nginx/nginx.conf
Generated configuration file: ./common/config/adminserver/env
Generated configuration file: ./common/config/ui/env
Generated configuration file: ./common/config/registry/config.yml
Generated configuration file: ./common/config/db/env
Generated configuration file: ./common/config/jobservice/env
Generated configuration file: ./common/config/jobservice/app.conf
Generated configuration file: ./common/config/ui/app.conf
Generated certificate, key file: ./common/config/ui/private_key.pem, cert file: ./common/config/registry/root.crt
The configuration files are ready, please use docker-compose to start the service.


[Step 2]: checking existing instance of Harbor ...


[Step 3]: starting Harbor ...
Creating network "harbor_harbor" with the default driver
Creating harbor-log ... 
Creating harbor-log ... done
Creating harbor-db ... 
Creating registry ... 
Creating harbor-adminserver ... 
Creating harbor-db
Creating registry
Creating harbor-adminserver ... done
Creating harbor-db ... done
Creating harbor-ui ... done
Creating nginx ... 
Creating harbor-jobservice ... 
Creating nginx
Creating nginx ... done

✔ ----Harbor has been installed and started successfully.----

Now you should be able to visit the admin portal at https://registry.ameizi.me. 
For more details, please visit https://github.com/vmware/harbor .

root@docker:/usr/local/harbor# docker ps -a
CONTAINER ID        IMAGE                              COMMAND                  CREATED             STATUS              PORTS                                                              NAMES
b6ce27f7415c        vmware/nginx:1.11.5-patched        "nginx -g 'daemon ..."   8 seconds ago       Up 7 seconds        0.0.0.0:80->80/tcp, 0.0.0.0:443->443/tcp, 0.0.0.0:4443->4443/tcp   nginx
95c4faeaeaca        vmware/harbor-jobservice:v1.1.2    "/harbor/harbor_jo..."   8 seconds ago       Up 7 seconds                                                                           harbor-jobservice
376afe9d5b58        vmware/harbor-ui:v1.1.2            "/harbor/harbor_ui"      8 seconds ago       Up 7 seconds                                                                           harbor-ui
1c28e0d42a6e        vmware/harbor-db:v1.1.2            "docker-entrypoint..."   9 seconds ago       Up 8 seconds        3306/tcp                                                           harbor-db
377e1f0085b9        vmware/harbor-adminserver:v1.1.2   "/harbor/harbor_ad..."   9 seconds ago       Up 8 seconds                                                                           harbor-adminserver
5846871b47cb        vmware/registry:2.6.1-photon       "/entrypoint.sh se..."   9 seconds ago       Up 8 seconds        5000/tcp                                                           registry
409194224dd0        vmware/harbor-log:v1.1.2           "/bin/sh -c 'crond..."   9 seconds ago       Up 9 seconds        127.0.0.1:1514->514/tcp                                            harbor-log
root@docker:/usr/local/harbor# 
```

#### 修改`hosts`

```
192.168.31.228  registry.ameizi.me
```

#### 修改`/etc/default/docker`文件如下所示，去除`--insecure-registry=192.168.31.228`配置

```
DOCKER_OPTS="$DOCKER_OPTS --registry-mirror=https://ex93eg1r.mirror.aliyuncs.com"
```

#### 重启docker

```
service docker restart
docker-compose down
./install.sh
```

#### 浏览器访问

https://registry.ameizi.me admin/admin

![qq 20170616224422](https://user-images.githubusercontent.com/887836/27231574-9a426132-52e5-11e7-978e-12c5a699432c.png)

#### 客户端登录遇到`x509: certificate signed by unknown authority`错误

```
root@docker:/usr/local/harbor# docker login registry.ameizi.me
Username: admin
Password: 
Error response from daemon: Get https://registry.ameizi.me/v1/users/: x509: certificate signed by unknown authority
```

解决方法

创建`/etc/docker/certs.d/registry.ameizi.me`目录

拷贝`ca.crt`到`/etc/docker/certs.d/registry.ameizi.me/`目录下

重启docker

具体操作如下

```
root@docker:/usr/local/harbor# mkdir -p /etc/docker/certs.d/registry.ameizi.me 
root@docker:/usr/local/harbor# cp ssl/ca.crt /etc/docker/certs.d/registry.ameizi.me/
root@docker:/usr/local/harbor# docker-compose down
root@docker:/usr/local/harbor# service docker restart
root@docker:/usr/local/harbor# ./install.sh
root@docker:/usr/local/harbor# docker login registry.ameizi.me
Username: admin
Password: 
Login Succeeded
```

#### 向私服push镜像

```bash
root@docker:/usr/local/harbor# docker tag redis:latest registry.ameizi.me/library/redis:latest
root@docker:/usr/local/harbor# docker images -a
REPOSITORY                         TAG                 IMAGE ID            CREATED             SIZE
vmware/harbor-jobservice           v1.1.2              4ef0a7a33734        4 days ago          163 MB
vmware/harbor-ui                   v1.1.2              4ee8f190f366        4 days ago          183 MB
vmware/harbor-adminserver          v1.1.2              cdcf1bed7eb4        4 days ago          142 MB
vmware/harbor-db                   v1.1.2              fcb8aa7a0640        4 days ago          329 MB
redis                              latest              83744227b191        7 days ago          98.9 MB
registry.ameizi.me/library/redis   latest              83744227b191        7 days ago          98.9 MB
vmware/registry                    2.6.1-photon        0f6c96580032        4 weeks ago         150 MB
vmware/nginx                       1.11.5-patched      8ddadb143133        2 months ago        199 MB
vmware/harbor-log                  v1.1.2              9c46a7b5e517        3 months ago        192 MB
root@docker:/usr/local/harbor# docker push registry.ameizi.me/library/redis
The push refers to a repository [registry.ameizi.me/library/redis]
ebfb0a55a275: Pushed 
1213cad8924b: Pushed 
8ae00f04131b: Layer already exists 
e74a993fa648: Pushed 
3c8f219ed9b6: Pushed 
414f472e5061: Layer already exists 
latest: digest: sha256:6022356f9d729c858000fc10fc1b09d1624ba099227a0c5d314f7461c2fe6020 size: 1571
```

![qq 20170616224452](https://user-images.githubusercontent.com/887836/27231550-884608b2-52e5-11e7-9dba-79539eb0e816.png)


### 使用maven插件构建和推送镜像到私服

详情参考https://github.com/ameizi/spring-boot-docker-example

### 参考文章

docker安装 

https://store.docker.com/editions/community/docker-ce-server-ubuntu

https://docs.docker.com/engine/installation/linux/ubuntu/

docker-compose安装

https://docs.docker.com/compose/install/

https://github.com/docker/compose/releases/

docker免sudo配置

https://docs.docker.com/engine/installation/linux/linux-postinstall/

harbor安装配置

https://github.com/vmware/harbor/blob/master/docs/installation_guide.md

https://github.com/vmware/harbor/blob/master/docs/user_guide.md

docker阿里云加速配置

https://cr.console.aliyun.com/#/accelerator

harbor mirror registry配置

https://github.com/vmware/harbor/blob/master/contrib/Configure_mirror.md

http://www.jianshu.com/p/8d4fcff97a35

harbor https配置

https://github.com/vmware/harbor/blob/master/docs/configure_https.md

docker开启2376或2375监听端口

https://docs.docker.com/engine/reference/commandline/dockerd/