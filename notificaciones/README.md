# Microservicio NOTIFICACIONES

## Configuración de Variables de Entorno

Para ejecutar este microservicio, necesitas configurar las siguientes variables de entorno:

### AWS Credentials
```bash
AWS_ACCESS_KEY_ID=tu_access_key
AWS_SECRET_ACCESS_KEY=tu_secret_key
AWS_SQS_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/ACCOUNT_ID/ecommerce-notifications
AWS_SES_FROM_EMAIL=tu_email_verificado@domain.com
```

### Twilio Credentials
```bash
TWILIO_ACCOUNT_SID=tu_account_sid
TWILIO_AUTH_TOKEN=tu_auth_token
TWILIO_PHONE_NUMBER=+1234567890
```

## Ejecución

### PowerShell
```powershell
$env:AWS_ACCESS_KEY_ID="tu_key"
$env:AWS_SECRET_ACCESS_KEY="tu_secret"
$env:TWILIO_ACCOUNT_SID="tu_sid"
$env:TWILIO_AUTH_TOKEN="tu_token"
$env:TWILIO_PHONE_NUMBER="+1234567890"

.\mvnw.cmd spring-boot:run
```

### Bash/Linux
```bash
export AWS_ACCESS_KEY_ID="tu_key"
export AWS_SECRET_ACCESS_KEY="tu_secret"
export TWILIO_ACCOUNT_SID="tu_sid"
export TWILIO_AUTH_TOKEN="tu_token"
export TWILIO_PHONE_NUMBER="+1234567890"

./mvnw spring-boot:run
```

## Obtener Credenciales

- **AWS**: https://console.aws.amazon.com/iam/
- **Twilio**: https://www.twilio.com/console
