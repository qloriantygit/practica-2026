$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "Stopping practica-2026 infrastructure..."
Write-Host ""

docker compose down
