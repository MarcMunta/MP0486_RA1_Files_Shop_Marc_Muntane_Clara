Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
if (-not ([System.Management.Automation.PSTypeName]'WinApi').Type) {
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class WinApi {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr hWnd);
}
"@
}

function Wait-Window {
  param(
    [string]$TitleLike,
    [int]$TimeoutMs = 20000
  )

  $sw = [System.Diagnostics.Stopwatch]::StartNew()
  while ($sw.ElapsedMilliseconds -lt $TimeoutMs) {
    $proc = Get-Process |
      Where-Object { $_.MainWindowHandle -ne 0 -and $_.MainWindowTitle -like "*$TitleLike*" } |
      Select-Object -First 1

    if ($proc) {
      return $proc
    }

    [System.Windows.Forms.Application]::DoEvents() | Out-Null
  }

  throw "No se encontro ventana: $TitleLike"
}

function Focus-Window {
  param([System.Diagnostics.Process]$Process)

  [WinApi]::ShowWindow($Process.MainWindowHandle, 9) | Out-Null
  [WinApi]::BringWindowToTop($Process.MainWindowHandle) | Out-Null
  [WinApi]::SetForegroundWindow($Process.MainWindowHandle) | Out-Null
}

function Save-ScreenCapture {
  param([string]$OutputPath)

  $bounds = [System.Windows.Forms.Screen]::PrimaryScreen.Bounds
  $bmp = New-Object System.Drawing.Bitmap $bounds.Width, $bounds.Height
  $graphics = [System.Drawing.Graphics]::FromImage($bmp)
  $graphics.CopyFromScreen($bounds.Location, [System.Drawing.Point]::Empty, $bounds.Size)
  $bmp.Save($OutputPath, [System.Drawing.Imaging.ImageFormat]::Png)
  $graphics.Dispose()
  $bmp.Dispose()
}

$shots = "evidence\screenshots"
$productName = "evidenceobj" + (Get-Random -Minimum 100 -Maximum 999)
Set-Content -Path "evidence\dumps\product_name.txt" -Value $productName

$login = Wait-Window -TitleLike "Login" -TimeoutMs 30000
Focus-Window -Process $login
[System.Windows.Forms.SendKeys]::SendWait("123{TAB}test")
Save-ScreenCapture -OutputPath "$shots\15-login-correcto-relleno.png"
[System.Windows.Forms.SendKeys]::SendWait("{TAB}{ENTER}")

$menu = Wait-Window -TitleLike "MiTenda.com - Menu principal" -TimeoutMs 20000
Focus-Window -Process $menu
Save-ScreenCapture -OutputPath "$shots\16-menu-principal-objectdb.png"

[System.Windows.Forms.SendKeys]::SendWait("2")
$addProduct = Wait-Window -TitleLike "Producto" -TimeoutMs 15000
Focus-Window -Process $addProduct
[System.Windows.Forms.SendKeys]::SendWait("$productName{TAB}7{TAB}4.5")
Save-ScreenCapture -OutputPath "$shots\17-anadir-producto-con-stock.png"
[System.Windows.Forms.SendKeys]::SendWait("{TAB}{ENTER}")

$info = Wait-Window -TitleLike "Information" -TimeoutMs 15000
Focus-Window -Process $info
Save-ScreenCapture -OutputPath "$shots\18-producto-anadido-ok.png"
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")

$menu = Wait-Window -TitleLike "MiTenda.com - Menu principal" -TimeoutMs 15000
Focus-Window -Process $menu
[System.Windows.Forms.SendKeys]::SendWait("3")
$addStock = Wait-Window -TitleLike "Stock" -TimeoutMs 15000
Focus-Window -Process $addStock
[System.Windows.Forms.SendKeys]::SendWait("$productName{TAB}5")
Save-ScreenCapture -OutputPath "$shots\19-anadir-stock-con-cantidad.png"
[System.Windows.Forms.SendKeys]::SendWait("{TAB}{ENTER}")

$info = Wait-Window -TitleLike "Information" -TimeoutMs 15000
Focus-Window -Process $info
Save-ScreenCapture -OutputPath "$shots\20-stock-actualizado-ok.png"
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")

$menu = Wait-Window -TitleLike "MiTenda.com - Menu principal" -TimeoutMs 15000
Focus-Window -Process $menu
[System.Windows.Forms.SendKeys]::SendWait("9")
$removeProduct = Wait-Window -TitleLike "Eliminar Producto" -TimeoutMs 15000
Focus-Window -Process $removeProduct
[System.Windows.Forms.SendKeys]::SendWait("$productName")
Save-ScreenCapture -OutputPath "$shots\21-eliminar-producto-relleno.png"
[System.Windows.Forms.SendKeys]::SendWait("{TAB}{ENTER}")

$info = Wait-Window -TitleLike "Information" -TimeoutMs 15000
Focus-Window -Process $info
Save-ScreenCapture -OutputPath "$shots\22-producto-eliminado-ok.png"
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")

Write-Output "UI_MAINTENANCE_OK $productName"
