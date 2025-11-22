@echo off
REM Cargar variables de entorno directamente
set AWS_ACCESS_KEY_ID=AKIAYJZZZQVMRLTSPZSE
set AWS_SECRET_ACCESS_KEY=D4deMOK0R+4f0vomUmD7cdBLC9nB6BOEIw6b6cvO
set AWS_REGION=us-east-1
set AWS_SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/570814989657/ecommerce-notifications
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME_CATALOG=ecommerce
set DB_USERNAME=postgres
set DB_PASSWORD=admin

echo Iniciando CATALOGO en puerto 9092...
cd "catalogo\catalogo"
call mvnw.cmd spring-boot:run
pause
