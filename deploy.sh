#!/bin/bash
echo "PasswordAuthentication yes" | sudo tee -a /etc/ssh/ssh_config
echo "KbdInteractiveAuthentication yes" | sudo tee -a /etc/ssh/ssh_config
echo "PermitTTY yes" | sudo tee -a /etc/ssh/ssh_config

start=$(date +"%s")

ssh -v -p ${SERVER_PORT} ${SERVER_USER}@${SERVER_HOST} -i key.txt -t -t -o StrictHostKeyChecking=no << 'ENDSSH'
docker pull victor005/tangxiaozhi_sports_backend:latest

CONTAINER_NAME=glycemiaapp
if [ "$(docker ps -qa -f name=$CONTAINER_NAME)" ]; then
    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
        echo "Container is running -> stopping it..."
        docker stop $CONTAINER_NAME;
    fi
fi

docker run -d --rm -p 8000:8000 --name $CONTAINER_NAME victor005/tangxiaozhi_sports_backend:latest

exit
ENDSSH

if [ $? -eq 0 ]; then
  exit 0
else
  exit 1
fi

end=$(date +"%s")

diff=$(($end - $start))

echo "Deployed in : ${diff}s"
