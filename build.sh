#!/bin/bash

# Membangun file JAR dengan Maven
mvn clean package -DskipTests

# Memeriksa apakah build JAR berhasil
if [ $? -ne 0 ]; then
  echo "Build JAR gagal. Mohon periksa kesalahan."
  exit 1
fi

# Membangun Docker image
docker build -t ragilmaulana79/user-service:latest .

# Mendorong image ke Docker Hub
docker push ragilmaulana79/user-service:latest
