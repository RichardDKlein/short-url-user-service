#!/bin/bash

BASE_URL=$(aws ssm get-parameter \
    --name /shortUrl/users/baseUrlLocal \
    --query "Parameter.Value" \
    --output text)

PORT_NUMBER=$(echo $BASE_URL | \
    sed 's/.*host.docker.internal:\([0-9]*\).*/\1/')

echo "Starting ShortUrlUserService on localhost:$PORT_NUMBER"

sam local start-api -p $PORT_NUMBER --env-vars env_prod.json
