@echo off
REM Cargar variables de entorno directamente
set AWS_ACCESS_KEY_ID=AKIAYJZZZQVMRLTSPZSE
set AWS_SECRET_ACCESS_KEY=D4deMOK0R+4f0vomUmD7cdBLC9nB6BOEIw6b6cvO
set AWS_REGION=us-east-1
set AWS_SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/570814989657/ecommerce-notifications
set AWS_SES_FROM_EMAIL=kratexvertex90@gmail.com
set TWILIO_ACCOUNT_SID=ACe3c14d2cab2ff9db78094648318b409b
set TWILIO_AUTH_TOKEN=ba4b9faa98fcf2460f8981ae079188f4
set TWILIO_PHONE_NUMBER=+15075790947

echo Iniciando NOTIFICACIONES en puerto 9093...
cd "notificaciones"
call mvnw.cmd spring-boot:run
pause
