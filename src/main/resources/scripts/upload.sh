#!/bin/bash

curl -X POST \
-H "Content-Type: multipart/form-data" \
-F "testId002=@./testId002.zip" \
-F "testId001=@./testId001.zip" \
-F "access_token=xxx" \
https://developer.ingenuity.com/datastream/api/v1/labcorp/datapackages/batch


