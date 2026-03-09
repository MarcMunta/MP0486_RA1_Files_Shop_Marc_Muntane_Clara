param(
  [int]$MenuHandle = 5245766
)

Add-Type -AssemblyName System.Windows.Forms
Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class WinApi {
  [DllImport("user32.dll")] public static extern IntPtr GetForegroundWindow();
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool BringWindowToTop(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern uint GetWindowThreadProcessId(IntPtr hWnd, out uint processId);
  [DllImport("user32.dll")] public static extern bool AttachThreadInput(uint idAttach, uint idAttachTo, bool fAttach);
  [DllImport("user32.dll")] public static extern bool SetCursorPos(int x, int y);
  [DllImport("user32.dll")] public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint dwData, UIntPtr dwExtraInfo);
}
"@

function Focus-Window([IntPtr]$target) {
  $foreground = [WinApi]::GetForegroundWindow()
  $targetPid = [uint32]0
  $foregroundPid = [uint32]0
  $targetThread = [WinApi]::GetWindowThreadProcessId($target, [ref]$targetPid)
  $foregroundThread = [WinApi]::GetWindowThreadProcessId($foreground, [ref]$foregroundPid)

  [WinApi]::AttachThreadInput($targetThread, $foregroundThread, $true) | Out-Null
  [WinApi]::ShowWindow($target, 9) | Out-Null
  [WinApi]::BringWindowToTop($target) | Out-Null
  [WinApi]::SetForegroundWindow($target) | Out-Null
  Start-Sleep -Milliseconds 400
  [WinApi]::AttachThreadInput($targetThread, $foregroundThread, $false) | Out-Null
  Start-Sleep -Milliseconds 400
}

function Click-Point([int]$x, [int]$y) {
  [WinApi]::SetCursorPos($x, $y) | Out-Null
  Start-Sleep -Milliseconds 150
  [WinApi]::mouse_event(0x0002, 0, 0, 0, [UIntPtr]::Zero)
  [WinApi]::mouse_event(0x0004, 0, 0, 0, [UIntPtr]::Zero)
  Start-Sleep -Milliseconds 250
}

function Refresh-Dump {
  $classpath = (Resolve-Path "target\classes").Path + ";" +
    (Get-Content -Raw "target\runtime-classpath.txt").Trim() + ";" +
    (Resolve-Path "evidence\scripts").Path

  java -cp $classpath QueryMongo mongodb://localhost:27017 shop evidence\dumps | Out-Null
}

$menu = [IntPtr]$MenuHandle

# 1. Add product
Focus-Window $menu
Click-Point 340 291
Click-Point 345 160
[System.Windows.Forms.SendKeys]::SendWait("testmongo")
Click-Point 345 200
[System.Windows.Forms.SendKeys]::SendWait("7")
Click-Point 345 240
[System.Windows.Forms.SendKeys]::SendWait("4.5")
Click-Point 395 475
Start-Sleep -Milliseconds 700
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")
Start-Sleep -Milliseconds 700
Refresh-Dump
Copy-Item "evidence\dumps\inventory.json" "evidence\dumps\inventory_after_add.json" -Force

# 2. Add stock
Focus-Window $menu
Click-Point 340 341
Click-Point 345 160
[System.Windows.Forms.SendKeys]::SendWait("testmongo")
Click-Point 345 200
[System.Windows.Forms.SendKeys]::SendWait("5")
Click-Point 395 475
Start-Sleep -Milliseconds 700
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")
Start-Sleep -Milliseconds 700
Refresh-Dump
Copy-Item "evidence\dumps\inventory.json" "evidence\dumps\inventory_after_stock.json" -Force

# 3. Delete product
Focus-Window $menu
Click-Point 340 390
Click-Point 345 160
[System.Windows.Forms.SendKeys]::SendWait("testmongo")
Click-Point 395 475
Start-Sleep -Milliseconds 700
[System.Windows.Forms.SendKeys]::SendWait("{ENTER}")
Start-Sleep -Milliseconds 700
Refresh-Dump
Copy-Item "evidence\dumps\inventory.json" "evidence\dumps\inventory_after_delete.json" -Force

Write-Output "snapshots-created"
