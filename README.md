# Tangxiaozhi_Sports_Backend
[![Glycemia SE Huangjie](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml/badge.svg?branch=docker)](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml) [![codecov](https://codecov.io/gh/SEMedical/Backend/graph/badge.svg?token=ZBBAGREM4F)](https://codecov.io/gh/SEMedical/Backend)
[![CodeQL](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml)
[![Glycemia SE Doc](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml)
[![Dependabot Updates](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates)

血糖项目后端

> [!CAUTION]
> This project will be used as Curriculum Vitae material,and the following works will focus on infrastructure and interview.
> Only those who has contributed to this repo can use this project as a CV material,any modification is welcome,even the declaration of copyright,
> but copyright information can only be appended by the eligible and any modification of original information will be forbidden.

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
mvn package --DskipTests=true
docker compose down 
docker compose up --profiles app
```
执行以上命令用于版本更新后更新服务。
## RIOT
We can use riot to import data from MySQL to Redis.
### Prerequisites
In the page of latest release of riot,you can find the download link of the riot,
and you should choose Linux(glibc) standalone version,like this:
[riot-standalone-4.0.4-linux_musl-x86_64.zip](https://github.com/redis/riot/releases/download/v4.0.4/riot-standalone-4.0.4-linux-x86_64.zip
)
After uncompressed the zip file,you can find the executable file in the bin directory.
### Import data
Now,you can import the data,here's an example:
```shell
bin/riot db-import "SELECT UNIX_TIMESTAMP(rounded_time)*1000 AS record_ts,\
 patient_id, (SELECT glycemia FROM glycemia WHERE CONCAT(DATE_FORMAT(record_time\
 , '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0'), ':00') =\
  rt.rounded_time LIMIT 1) AS glycemia FROM (SELECT DISTINCT CONCAT(DATE_FORMAT\
  (record_time, '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0')\
  , ':00') AS rounded_time, patient_id FROM glycemia) rt ORDER BY rounded_time;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp record_ts --key patient_id \
   --value glycemia
```
You can find more in the file [RIOT-SCRIPTS](./riot-scripts.sh)
## Test coverage
[<img src="https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F">](https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F)
## Copyright
Copyright (C) 2024 Victor Hu,UltraTempest10,a-little-dust,rmEleven,Dawson128,LEAVE-cshj
All rights reserved.
## License
[GPL-3.0](https://www.gnu.org/licenses/gpl-3.0.html)
