$sql = [System.IO.File]::ReadAllText("$PSScriptRoot\fix_all.sql", [System.Text.Encoding]::UTF8)
$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "mysql"
$psi.Arguments = "-u root -p1234 --default-character-set=utf8mb4 second_hand_trade"
$psi.UseShellExecute = $false
$psi.RedirectStandardInput = $true
$psi.RedirectStandardError = $true
$p = [System.Diagnostics.Process]::Start($psi)
$sw = $p.StandardInput
$sw.Write($sql)
$sw.Close()
$err = $p.StandardError.ReadToEnd()
$p.WaitForExit()
if ($p.ExitCode -ne 0) { Write-Host "ERROR: $err" }
else { Write-Host "Data imported successfully!" }
