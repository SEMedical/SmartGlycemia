# Tangxiaozhi_Sports_Backend
[![Glycemia SE Huangjie](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml/badge.svg?branch=docker)](https://github.com/SoftwareEngineeringMedical/Tangxiaozhi_Sports_Backend/actions/workflows/workflow.yml)

血糖项目后端

## 运行方式
```shell
docker compose up -d
docker run backend-glycemia-service --env-file=web-variables.yml
docker run backend-user-service --env-file=web-variables.yml
docker run backend-exercise-service --env-file=web-variables.yml
docker run backend-gateway --env-file=web-variables.yml
```