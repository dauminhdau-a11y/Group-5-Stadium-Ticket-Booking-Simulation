[CmdletBinding()]
param (
    [string]$Class = ""
)

$javapPath = "C:\Program Files\Java\jdk-27\bin\javap.exe"
if (!(Test-Path $javapPath)) {
    $javapPath = "javap"
}

if (!(Test-Path "bin")) {
    Write-Host "[!] Thu muc 'bin' chua ton tai. Vui long chay .\\run.ps1 de bien dich truoc." -ForegroundColor Red
    exit 1
}

if ($Class -ne "") {
    # Find matching class in bin
    $found = Get-ChildItem -Recurse -Filter "$Class.class" bin | Select-Object -First 1
    if (!$found) {
        $found = Get-ChildItem -Recurse -Filter "*$Class*.class" bin | Where-Object { $_.Name -notmatch '\$' } | Select-Object -First 1
    }
    if (!$found) {
        Write-Host "[!] Khong tim thay class '$Class' trong thu muc bin." -ForegroundColor Yellow
        exit 1
    }
    $relative = $found.FullName.Substring((Get-Item "bin").FullName.Length + 1).Replace("\", "/").Replace(".class", "")
    $className = $relative.Replace("/", ".")
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host "  DANH SACH BIEN & HAM CUA: $className" -ForegroundColor Green
    Write-Host "===============================================================" -ForegroundColor Cyan
    & $javapPath -p -cp bin $className
} else {
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host "  HUONG DAN XEM BIEN & HAM CUA TUNG CLASS" -ForegroundColor Green
    Write-Host "===============================================================" -ForegroundColor Cyan
    Write-Host "Cach 1: Xem 1 class cu the, go:" -ForegroundColor Yellow
    Write-Host "  .\\inspect_classes.ps1 -Class Section"
    Write-Host "  .\\inspect_classes.ps1 -Class TransactionController"
    Write-Host "  .\\inspect_classes.ps1 -Class FanView"
    Write-Host ""
    Write-Host "Cach 2: Xuat toan bo bien va ham cua tat ca class ra file:" -ForegroundColor Yellow
    Write-Host "  .\\inspect_classes.ps1 -All > docs/ALL_CLASSES_STRUCTURE.txt"
    Write-Host "===============================================================" -ForegroundColor Cyan
    
    $classes = Get-ChildItem -Recurse -Filter "*.class" bin | Where-Object { $_.Name -notmatch '\$' }
    Write-Host "Tong so class trong he thong: $($classes.Count)" -ForegroundColor White
    Write-Host "Cac class chinh:" -ForegroundColor Gray
    $classes | ForEach-Object {
        $rel = $_.FullName.Substring((Get-Item "bin").FullName.Length + 1).Replace("\", "/").Replace(".class", "")
        Write-Host " - $($rel.Replace('/', '.'))" -ForegroundColor DarkGray
    }
}
