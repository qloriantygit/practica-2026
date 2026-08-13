$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $root ".env"

if (-not (Test-Path $envFile)) {
    throw ".env file was not found: $envFile"
}

Get-Content $envFile |
    ForEach-Object {

        $line = $_.Trim()

        if (
            -not [string]::IsNullOrWhiteSpace($line) -and
            -not $line.StartsWith("#")
        ) {

            $separatorIndex = $line.IndexOf("=")

            if ($separatorIndex -gt 0) {

                $name = $line.Substring(0, $separatorIndex).Trim()
                $value = $line.Substring($separatorIndex + 1).Trim()

                [Environment]::SetEnvironmentVariable(
                    $name,
                    $value,
                    "Process"
                )
            }
        }
    }

$env:ADMIN_DB_HOST = "localhost"
$env:ADMIN_DB_PORT = "5433"

Write-Host ""
Write-Host "Starting admin-service..."
Write-Host ""

Push-Location $root

try {
    mvn -f services/admin-service/pom.xml spring-boot:run
}
finally {
    Pop-Location
}
