param(
    [Parameter(Mandatory = $true)]
    [ValidateSet("test-admin", "analyst")]
    [string]$Account
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $root ".env"

if (-not (Test-Path $envFile)) {
    throw ".env file was not found"
}

$values = @{}

Get-Content $envFile |
    ForEach-Object {

        $line = $_.Trim()

        if (
            -not [string]::IsNullOrWhiteSpace($line) -and
            -not $line.StartsWith("#")
        ) {

            $separatorIndex = $line.IndexOf("=")

            if ($separatorIndex -gt 0) {

                $name =
                    $line.Substring(
                        0,
                        $separatorIndex
                    ).Trim()

                $value =
                    $line.Substring(
                        $separatorIndex + 1
                    ).Trim()

                $values[$name] = $value
            }
        }
    }

switch ($Account) {

    "test-admin" {

        $username = "test.admin"

        $password =
            $values[
                "KEYCLOAK_TEST_ADMIN_PASSWORD"
            ]
    }

    "analyst" {

        $username = "analyst.user"

        $password =
            $values[
                "KEYCLOAK_ANALYST_USER_PASSWORD"
            ]
    }
}

if ([string]::IsNullOrWhiteSpace($password)) {
    throw "Password for $Account was not found in .env"
}

$response = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8180/realms/practica-2026/protocol/openid-connect/token" `
    -ContentType "application/x-www-form-urlencoded" `
    -Body @{
        grant_type = "password"
        client_id = "practica-cli"
        username = $username
        password = $password
        scope = "openid profile email"
    }

$response.access_token
