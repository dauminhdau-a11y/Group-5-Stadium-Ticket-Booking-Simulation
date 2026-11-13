[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8
Write-Host "[1/2] Compiling source code..." -ForegroundColor Cyan
if (!(Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }
$sources = Get-ChildItem -Recurse -Filter *.java src | Select-Object -ExpandProperty FullName
javac -encoding UTF-8 -d bin $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}
Write-Host "[2/2] Launching application..." -ForegroundColor Green
Write-Host "===================================================" -ForegroundColor Yellow
java -cp bin Main
