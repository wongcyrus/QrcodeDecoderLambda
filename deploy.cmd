call ./gradlew shadowJar

:: echo Create Lambda
:: call aws lambda create-function ^
::    --region us-east-1 ^
::    --function-name DecodeQrcode ^
::    --zip-file fileb://build/libs/QrcodeDecoderLambda-1.0-SNAPSHOT-all.jar
::    --role arn:aws:iam::894598711988:role/webhook-dev ^
::    --handler com.cloudlab.healthAi.qrcode.QrcodeHandler::handleRequest^
::    --runtime java8 ^
::    --timeout 60 ^
::    --memory-size 1024

echo Update Lambda
call aws lambda update-function-code ^
   --region us-east-1 ^
   --function-name DecodeQrcode ^
   --zip-file fileb://build/libs/QrcodeDecoderLambda-1.0-SNAPSHOT-all.jar



