@echo off
REM Cargar variables de entorno directamente
set AWS_ACCESS_KEY_ID=REDACTED
set AWS_SECRET_ACCESS_KEY=REDACTED
set AWS_REGION=us-east-1
set AWS_SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/570814989657/ecommerce-notifications
set AWS_SES_FROM_EMAIL=your-email@example.com
set TWILIO_ACCOUNT_SID=REDACTED
set TWILIO_AUTH_TOKEN=REDACTED
set TWILIO_PHONE_NUMBER=+15075790947

echo Iniciando NOTIFICACIONES en puerto 9093...
cd "notificaciones"
call mvnw.cmd spring-boot:run
pause
