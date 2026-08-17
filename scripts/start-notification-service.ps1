$ErrorActionPreference = "Stop"

$root =
    Split-Path $PSScriptRoot -Parent

Set-Location $root

$envFile =
    Join-Path $root ".env"

if (-not (Test-Path $envFile)) {
    throw ".env file not found: $envFile"
}

Get-Content $envFile |
    Where-Object {
        $_ -and -not $_.Trim().StartsWith("#") -and $_.Contains("=")
    } |
    ForEach-Object {

        $parts =
            $_ -split "=", 2

        $name =
            $parts[0].Trim()

        $value =
            $parts[1].Trim()

        [Environment]::SetEnvironmentVariable(
            $name,
            $value,
            "Process"
        )
    }

$env:NOTIFICATION_DB_HOST =
    "localhost"

$env:NOTIFICATION_DB_PORT =
    "5434"

$env:RABBITMQ_HOST =
    "localhost"

$env:RABBITMQ_PORT =
    "5672"

$env:MAIL_HOST =
    "localhost"

$env:MAIL_PORT =
    "1025"

Write-Host "Starting notification-service..."
Write-Host "PostgreSQL: localhost:5434/notification_db"
Write-Host "RabbitMQ: localhost:5672"
Write-Host "SMTP: localhost:1025"

mvn `
    -pl services/notification-service `
    spring-boot:run
