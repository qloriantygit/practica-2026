$ErrorActionPreference = "Stop"

Write-Host ""
Write-Host "Starting practica-2026 infrastructure..."
Write-Host ""

docker compose up -d

Write-Host ""
Write-Host "Infrastructure containers:"
Write-Host ""

docker compose ps
