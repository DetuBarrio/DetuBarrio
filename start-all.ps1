param(
    [switch]$SkipDocker,
    [switch]$DryRun,
    [switch]$ForceBackend,
    [switch]$ForceFrontend
)

$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $root 'rest\rest'
$frontendDir = Join-Path $root 'a\vue'
$composeFile = Join-Path $backendDir 'docker-compose.yml'

function Write-Step([string]$message) {
    Write-Host "[DeTuBarrio] $message" -ForegroundColor Cyan
}

function Test-TcpPortListening([int]$port) {
    return [bool](Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue)
}

function Start-InNewPowerShell([string]$title, [string]$workingDir, [string]$command) {
    $escapedWorkingDir = $workingDir.Replace("'", "''")
    $escapedCommand = $command.Replace("'", "''")
    $script = "Set-Location '$escapedWorkingDir'; `$Host.UI.RawUI.WindowTitle = '$title'; $escapedCommand"

    if ($DryRun) {
        Write-Step "DRY-RUN: abrir terminal '$title' en $workingDir ejecutando: $command"
        return
    }

    Start-Process powershell -ArgumentList @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command', $script) | Out-Null
}

if (-not (Test-Path $backendDir)) {
    throw "No se encontro carpeta backend en: $backendDir"
}

if (-not (Test-Path $frontendDir)) {
    throw "No se encontro carpeta frontend en: $frontendDir"
}

if (-not $SkipDocker) {
    Write-Step 'Comprobando Docker...'

    if ($DryRun) {
        Write-Step "DRY-RUN: verificar contenedor detubarrio-mysql y arrancar docker compose con $composeFile si hace falta"
    } else {
        $containerName = 'detubarrio-mysql'
        $containerId = docker ps -aq -f "name=^${containerName}$"

        if (-not $containerId) {
            if (-not (Test-Path $composeFile)) {
                throw "No se encontro docker-compose.yml en $composeFile"
            }

            Write-Step 'Contenedor MySQL no encontrado. Levantando con docker compose...'
            docker compose -f $composeFile up -d
        } else {
            $running = docker ps -q -f "name=^${containerName}$"
            if (-not $running) {
                Write-Step 'Contenedor MySQL encontrado pero detenido. Arrancando...'
                docker start $containerName | Out-Null
            } else {
                Write-Step 'Contenedor MySQL ya estaba en ejecucion.'
            }
        }
    }
}

if (Test-TcpPortListening 8080 -and -not $ForceBackend) {
    Write-Step 'El puerto 8080 ya esta ocupado; se asume que el backend ya esta en ejecucion y se omite un segundo arranque.'
} else {
    Write-Step 'Arrancando backend en nueva terminal...'
    Start-InNewPowerShell -title 'DeTuBarrio Backend' -workingDir $backendDir -command '.\mvnw.cmd spring-boot:run'
}

if (Test-TcpPortListening 5173 -and -not $ForceFrontend) {
    Write-Step 'El puerto 5173 ya esta ocupado; se asume que el frontend ya esta en ejecucion y se omite un segundo arranque.'
} else {
    Write-Step 'Arrancando frontend en nueva terminal...'
    Start-InNewPowerShell -title 'DeTuBarrio Frontend' -workingDir $frontendDir -command 'npm run dev'
}

Write-Step 'Listo. URLs esperadas:'
Write-Host '  - Frontend: http://localhost:5173' -ForegroundColor Green
Write-Host '  - Backend Swagger: http://localhost:8080/swagger-ui/index.html' -ForegroundColor Green
Write-Host '  - Backend API docs: http://localhost:8080/v3/api-docs' -ForegroundColor Green