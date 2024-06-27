# Tangxiaozhi_Sports_Backend
[![Glycemia SE Huangjie](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml/badge.svg?branch=docker)](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml) [![codecov](https://codecov.io/gh/SEMedical/Backend/graph/badge.svg?token=ZBBAGREM4F)](https://codecov.io/gh/SEMedical/Backend)
[![CodeQL](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml)
[![Glycemia SE Doc](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml)
[![Dependabot Updates](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates)

血糖项目后端

## How to run it?
### Prerequisites
First,you should prepare a directory ```tmp``` in the root directory of the project.
Here is the tree:
```shell
/tmp
├── logs
│   ├── user
│   │   ├── roll_backlog.log
│   │   ├── ...
│   ├── exercise
│   ├── glycemia
│   ├── gateway
│   └── oa
├── redis
│   ├── local-data
│   └── local-log
└── mysql
│   ├── data
└── nginx
    ├── conf
    ├── logs
    └── html
```
- Note that the directory's ACL should be 755,
and the regular file should be 644.
- the ```/tmp/local-data``` is used to store the AOF,RDB and log file of Redis.
- the ```mysql/data``` is used to store the schema and data of MySQL.
- the ```nginx/conf``` is used to store the configuration of Nginx.
- the ```nginx/logs``` is used to store the log of Nginx.
- the ```nginx/html``` is used to store the static file of Nginx.
### Redis-Stack Service
Then,you can run the Redis-Stack service under the protection of firewall.
```shell
sudo docker compose up -d redis-stack
```
Then,you can assign ACL list in local redis-cli,
the {docker-id} can be found by ```docker ps```.
```shell
sudo docker exec -it {docker-id} redis-cli
```
In the client ,you can set the permission and password of different users.
```shell
ACL SETUSER {some-superuser} on ~* &* +@all > {REDACTED_PASSWORD}
ACL SETUSER default on ~* &* +@all -@dangerous -@slow -@blocking -@admin > {REDACTED_PASSWORD}
CONFIG REWRITE
```
The {REDACTED_PASSWORD} should be replaced by the password you want to set.

You can generate password by the following command:
```shell
ACL GENPASS 
```
After that,you can expose port of [Redis Stack](https://github.com/redis-stack/redis-stack) 
and [Redis Insight](https://github.com/RedisInsight/RedisInsight).

If you want more details about ACL,you can refer to the [Redis ACL](https://redis.io/docs/latest/operate/oss_and_stack/management/security/acl/) 
### MySQL Service
You can run the MySQL service under the protection of firewall.

- Before you run,you should set 4 environment variables in the .env file.

- And you can find the SQL file to create schema here:
[glycemia.sql](https://github.com/SEMedical/DB/blob/main/glycemia.sql)
- And if you want to import data,you can find the SQL file to load data here:
[data.sql](https://github.com/SEMedical/DB/blob/main/data.sql)
### Nginx
- As it's a backend repository ,here the Nginx is only used for load-balance of Nacos,
in the future even the Nacos will be replaced by the [Kubernetes](https://kubernetes.io/).
### Spring Cloud Service Series
```shell
mvn package
docker compose down --rmi all
docker compose up -d
```
执行以上命令用于版本更新后更新服务。

## Test coverage
[<img src="https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F">](https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F)
