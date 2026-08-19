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

$env:ADMIN_SERVICE_URL =
    "http://localhost:8081"

$env:NOTIFICATION_SERVICE_URL =
    "http://localhost:8082"


# ------------------------------------------------------------
# Realm берём из существующего Keycloak import,
# чтобы не дублировать его имя вручную.
# ------------------------------------------------------------

$realmFile =
    Join-Path `
        $root `
        "infrastructure\keycloak\import\practica-2026-realm.json"

if (-not (Test-Path $realmFile)) {
    throw "Keycloak realm file not found: $realmFile"
}

$realmConfig =
    Get-Content $realmFile -Raw |
    ConvertFrom-Json

$realm =
    $realmConfig.realm

if ([string]::IsNullOrWhiteSpace($realm)) {
    throw "Keycloak realm name is empty"
}

$env:KEYCLOAK_ISSUER_URI =
    "http://localhost:8180/realms/$realm"

Write-Host "Starting api-gateway..."
Write-Host "Port: 8080"
Write-Host "Admin service: $env:ADMIN_SERVICE_URL"
Write-Host "Notification service: $env:NOTIFICATION_SERVICE_URL"
Write-Host "Keycloak issuer: $env:KEYCLOAK_ISSUER_URI"

mvn `
    -pl services/api-gateway `
    spring-boot:run
