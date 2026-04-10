Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing
if (-not ([System.Management.Automation.PSTypeName]'WinApiStock').Type) {
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class WinApiStock {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr hWnd, out RECT rect);
  [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
  [DllImport("user32.dll")] public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint dwData, UIntPtr dwExtraInfo);
  [StructLayout(LayoutKind.Sequential)] public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }
}
"@
}

function Wait-Window {
  param([string]$TitleLike,[int]$TimeoutMs = 20000)
  $sw = [System.Diagnostics.Stopwatch]::StartNew()
  while ($sw.ElapsedMilliseconds -lt $TimeoutMs) {
    $proc = Get-Process | Where-Object { $_.MainWindowHandle -ne 0 -and $_.MainWindowTitle -like "*$TitleLike*" } | Select-Object -First 1
    if ($proc) { return $proc }
    [System.Windows.Forms.Application]::DoEvents() | Out-Null
  }
  throw "No se encontro ventana: $TitleLike"
}

function Focus-Window {
  param([System.Diagnostics.Process]$Process)
  [WinApiStock]::ShowWindow($Process.MainWindowHandle, 9) | Out-Null
  [WinApiStock]::BringWindowToTop($Process.MainWindowHandle) | Out-Null
  [WinApiStock]::SetForegroundWindow($Process.MainWindowHandle) | Out-Null
}

function Click-WindowPoint {
  param(
    [System.Diagnostics.Process]$Process,
    [int]$X,
    [int]$Y
  )

  $rect = New-Object WinApiStock+RECT
  [WinApiStock]::GetWindowRect($Process.MainWindowHandle, [ref]$rect) | Out-Null

  $screenX = $rect.Left + $X
  $screenY = $rect.Top + $Y
  [WinApiStock]::SetCursorPos($screenX, $screenY) | Out-Null
  [WinApiStock]::mouse_event(0x0002, 0, 0, 0, [UIntPtr]::Zero)
  [WinApiStock]::mouse_event(0x0004, 0, 0, 0, [UIntPtr]::Zero)
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

$login = Wait-Window -TitleLike "Login" -TimeoutMs 30000
Focus-Window -Process $login
[System.Windows.Forms.SendKeys]::SendWait("123{TAB}test{TAB}{ENTER}")

$menu = Wait-Window -TitleLike "MiTenda.com - Menu principal" -TimeoutMs 20000
Focus-Window -Process $menu
Click-WindowPoint -Process $menu -X 220 -Y 240

$stockDialog = Wait-Window -TitleLike "Stock" -TimeoutMs 20000
Focus-Window -Process $stockDialog
[System.Windows.Forms.SendKeys]::SendWait("testmongo{TAB}5")
Save-ScreenCapture -OutputPath "evidence\screenshots\24-anadir-stock-con-numero-visible.png"

Write-Output "ADD_STOCK_CAPTURE_OK"
