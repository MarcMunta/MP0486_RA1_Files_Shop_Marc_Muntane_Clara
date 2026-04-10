Add-Type -AssemblyName System.Windows.Forms
if (-not ([System.Management.Automation.PSTypeName]'WinApiDbg').Type) {
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class WinApiDbg {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr hWnd);
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

$login = Wait-Window -TitleLike "Login" -TimeoutMs 30000
[WinApiDbg]::ShowWindow($login.MainWindowHandle, 9) | Out-Null
[WinApiDbg]::BringWindowToTop($login.MainWindowHandle) | Out-Null
[WinApiDbg]::SetForegroundWindow($login.MainWindowHandle) | Out-Null
Start-Sleep -Milliseconds 300
[System.Windows.Forms.SendKeys]::SendWait("666{TAB}nose{TAB}{ENTER}")

Start-Sleep -Seconds 2
$titles = Get-Process |
  Where-Object { $_.MainWindowHandle -ne 0 } |
  Select-Object -ExpandProperty MainWindowTitle |
  Sort-Object -Unique

$titles | Set-Content -Path "evidence\dumps\debug_window_titles.txt"
Write-Output "DEBUG_DONE"
