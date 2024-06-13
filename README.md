# Tangxiaozhi_Sports_Backend
[![Glycemia SE Huangjie](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml/badge.svg?branch=docker)](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml) [![codecov](https://codecov.io/gh/SEMedical/Backend/graph/badge.svg?token=ZBBAGREM4F)](https://codecov.io/gh/SEMedical/Backend)
[![CodeQL](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/codeql.yml)
[![Glycemia SE Doc](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/docflow.yml)
[![Dependabot Updates](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/SEMedical/Backend/actions/workflows/dependabot/dependabot-updates)

血糖项目后端

## 运行方式
```shell
mvn package
docker compose down --rmi all
docker compose up -d
```
执行以上命令用于版本更新后更新服务。


## Docker镜像
```shell
docker compose up -d
docker pull victor005/exercise-service:latest
docker pull victor005/user-service:latest
docker pull victor005/backend-gateway:latest
docker pull victor005/glycemia-service:latest
```
以上为不下载仓库远程拉取镜像并运行的方法。

## Test coverage
[<img src="https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F">](https://codecov.io/gh/SEMedical/Backend/graphs/tree.svg?token=ZBBAGREM4F)
