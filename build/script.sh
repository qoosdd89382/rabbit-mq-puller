#!/bin/bash

dockerhub_repo=$1

cd ../source

# Use IFS (Internal Field Separator) to properly split folder names
IFS=$'\n'
folder_names=$(find . -maxdepth 1 -type d ! -name "." -exec basename {} \;)

for folder_name in $folder_names; do
    echo "Processing folder: $folder_name"
    pwd    
    cd ./$folder_name

    # maven build
    docker run -it --rm --name my-maven-project -v ~/.m2:/root/.m2 -v $(pwd):/app maven:3.8.3-amazoncorretto-17 mvn clean install -f app/pom.xml -Dmaven.test.skip=true

	# docker build
	docker build . -t $dockerhub_repo/$folder_name
	docker push $dockerhub_repo/$folder_name

    # back to source path
    cd ..
done
