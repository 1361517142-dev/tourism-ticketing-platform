$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$configPath = Join-Path $scriptDir 'test-config.properties'
$jmxPath = Join-Path $scriptDir 'coupon-seckill.jmx'

function Read-SimpleProperties {
    param([string]$Path)

    $properties = @{}
    foreach ($rawLine in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $line = $rawLine.Trim()
        if ($line.Length -eq 0 -or $line.StartsWith('#')) {
            continue
        }

        $separator = $line.IndexOf('=')
        if ($separator -le 0) {
            continue
        }

        $key = $line.Substring(0, $separator).Trim()
        $value = $line.Substring($separator + 1).Trim()
        $properties[$key] = $value
    }

    return $properties
}

function Require-PositiveInteger {
    param(
        [hashtable]$Properties,
        [string]$Name
    )

    $parsed = 0
    if (-not [int]::TryParse($Properties[$Name], [ref]$parsed) -or $parsed -le 0) {
        throw "Property $Name must be a positive integer. Current value: $($Properties[$Name])"
    }
    return $parsed
}

function Resolve-LocalPath {
    param(
        [string]$BaseDirectory,
        [string]$ConfiguredPath
    )

    if ([System.IO.Path]::IsPathRooted($ConfiguredPath)) {
        return [System.IO.Path]::GetFullPath($ConfiguredPath)
    }
    return [System.IO.Path]::GetFullPath((Join-Path $BaseDirectory $ConfiguredPath))
}

function Test-TcpPort {
    param(
        [string]$HostName,
        [int]$Port,
        [int]$TimeoutMilliseconds = 3000
    )

    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $connection = $client.BeginConnect($HostName, $Port, $null, $null)
        if (-not $connection.AsyncWaitHandle.WaitOne($TimeoutMilliseconds)) {
            return $false
        }
        $client.EndConnect($connection)
        return $true
    } catch {
        return $false
    } finally {
        $client.Close()
    }
}

function Get-Percentile {
    param(
        [int[]]$Values,
        [double]$Percentile
    )

    if ($Values.Count -eq 0) {
        return 0
    }

    $sorted = @($Values | Sort-Object)
    $index = [Math]::Ceiling($Percentile * $sorted.Count) - 1
    return $sorted[[Math]::Max(0, $index)]
}

if (-not (Test-Path -LiteralPath $configPath)) {
    throw "Missing config file: $configPath"
}

$config = Read-SimpleProperties -Path $configPath
$activityId = Require-PositiveInteger -Properties $config -Name 'activity_id'
$threads = Require-PositiveInteger -Properties $config -Name 'threads'
$rampUpSeconds = Require-PositiveInteger -Properties $config -Name 'ramp_up_seconds'
$port = Require-PositiveInteger -Properties $config -Name 'port'
$resultWaitSeconds = Require-PositiveInteger -Properties $config -Name 'result_wait_seconds'

$protocol = $config['protocol']
$hostName = $config['host']
$userMode = $config['user_mode'].ToLowerInvariant()
$pollFinalResult = $config['poll_final_result'].ToLowerInvariant()
$synchronizeUsers = $config['synchronize_users'].ToLowerInvariant()

if ($userMode -notin @('unique', 'single')) {
    throw 'user_mode must be unique or single.'
}
if ($pollFinalResult -notin @('true', 'false')) {
    throw 'poll_final_result must be true or false.'
}
if ($synchronizeUsers -notin @('true', 'false')) {
    throw 'synchronize_users must be true or false.'
}

$credentialsPath = Resolve-LocalPath `
    -BaseDirectory $scriptDir `
    -ConfiguredPath $config['credentials_file']

if (-not (Test-Path -LiteralPath $credentialsPath)) {
    throw "Missing credentials file: $credentialsPath`nCopy users.example.csv to users.csv and fill in real tourist accounts."
}

$credentialLines = @(
    Get-Content -LiteralPath $credentialsPath -Encoding UTF8 |
        ForEach-Object { $_.Trim() } |
        Where-Object { $_.Length -gt 0 -and -not $_.StartsWith('#') }
)

if ($credentialLines.Count -eq 0) {
    throw 'users.csv is empty. Expected format: loginName,password'
}
if ($credentialLines | Where-Object { $_ -notmatch '^[^,]+,[^,]+$' }) {
    throw 'Invalid users.csv. Each line must be loginName,password without extra commas.'
}
if ($userMode -eq 'unique' -and $credentialLines.Count -lt $threads) {
    throw "unique mode needs at least $threads accounts, but only $($credentialLines.Count) were found."
}
if ($userMode -eq 'unique') {
    $selectedLoginNames = @(
        $credentialLines |
            Select-Object -First $threads |
            ForEach-Object { ($_ -split ',', 2)[0] }
    )
    $distinctLoginCount = @($selectedLoginNames | Sort-Object -Unique).Count
    if ($distinctLoginCount -ne $threads) {
        throw "unique mode needs $threads distinct login names, but only $distinctLoginCount were found."
    }
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'The java command was not found. Install or configure Java 17 first.'
}

if (-not (Test-TcpPort -HostName $hostName -Port $port)) {
    throw "Cannot connect to $hostName`:$port. Start the Spring Boot application first."
}

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$resultRoot = Join-Path $scriptDir 'results'
$resultDir = Join-Path $resultRoot "activity-$activityId-$timestamp"
$reportDir = Join-Path $resultDir 'html-report'
$jtlPath = Join-Path $resultDir 'result.jtl'
$jmeterLogPath = Join-Path $resultDir 'jmeter.log'
$runtimeCredentialsPath = Join-Path $resultDir 'runtime-users.csv'

New-Item -ItemType Directory -Path $resultDir -Force | Out-Null

if ($userMode -eq 'single') {
    $runtimeCredentials = 1..$threads | ForEach-Object { $credentialLines[0] }
} else {
    $runtimeCredentials = $credentialLines | Select-Object -First $threads
}
$utf8WithoutBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllLines(
    $runtimeCredentialsPath,
    [string[]]$runtimeCredentials,
    $utf8WithoutBom
)

$jmeterVersion = $config['jmeter_version']
$toolsRoot = Join-Path $env:LOCALAPPDATA 'tourism-ticketing-tools'
$configuredJMeterHome = $config['jmeter_home']
if ([string]::IsNullOrWhiteSpace($configuredJMeterHome)) {
    $jmeterHome = Join-Path $toolsRoot "apache-jmeter-$jmeterVersion"
} else {
    $jmeterHome = Resolve-LocalPath `
        -BaseDirectory $scriptDir `
        -ConfiguredPath $configuredJMeterHome
}
$jmeterBat = Join-Path $jmeterHome 'bin\jmeter.bat'

if (-not (Test-Path -LiteralPath $jmeterBat)) {
    if ($config['auto_download_jmeter'].ToLowerInvariant() -ne 'true') {
        throw "JMeter was not found: $jmeterBat"
    }

    Write-Host "First run: downloading JMeter $jmeterVersion from Apache ..." -ForegroundColor Cyan
    New-Item -ItemType Directory -Path $toolsRoot -Force | Out-Null

    $archiveName = "apache-jmeter-$jmeterVersion.zip"
    $archivePath = Join-Path $toolsRoot $archiveName
    $partialArchivePath = "$archivePath.partial"
    $checksumPath = "$archivePath.sha512"
    $downloadBase = "https://dlcdn.apache.org/jmeter/binaries/$archiveName"

    # Windows curl shows download progress and can resume an interrupted first install.
    if (Get-Command curl.exe -ErrorAction SilentlyContinue) {
        & curl.exe --fail --location --continue-at - `
            --output $partialArchivePath $downloadBase
        if ($LASTEXITCODE -ne 0) {
            throw 'JMeter download failed.'
        }
        & curl.exe --fail --location `
            --output $checksumPath "$downloadBase.sha512"
        if ($LASTEXITCODE -ne 0) {
            throw 'JMeter checksum download failed.'
        }
    } else {
        Invoke-WebRequest -Uri $downloadBase -OutFile $partialArchivePath
        Invoke-WebRequest -Uri "$downloadBase.sha512" -OutFile $checksumPath
    }

    $expectedHash = ((Get-Content -Raw -LiteralPath $checksumPath).Trim() -split '\s+')[0].ToUpperInvariant()
    $actualHash = (Get-FileHash -LiteralPath $partialArchivePath -Algorithm SHA512).Hash.ToUpperInvariant()
    if ($actualHash -ne $expectedHash) {
        throw 'JMeter SHA-512 verification failed. The archive was not extracted.'
    }

    Move-Item -LiteralPath $partialArchivePath -Destination $archivePath -Force
    Expand-Archive -LiteralPath $archivePath -DestinationPath $toolsRoot -Force
    Remove-Item -LiteralPath $archivePath, $checksumPath -Force
}

Write-Host ''
Write-Host 'Starting coupon seckill test:' -ForegroundColor Cyan
Write-Host "  Activity ID : $activityId"
Write-Host "  Threads     : $threads"
Write-Host "  User mode   : $userMode"
Write-Host "  Sync start  : $synchronizeUsers"
Write-Host "  Poll result : $pollFinalResult"
Write-Host "  Server      : $protocol`://$hostName`:$port"
Write-Host ''

$jmeterArguments = @(
    '-n',
    '-t', $jmxPath,
    '-l', $jtlPath,
    '-j', $jmeterLogPath,
    '-e',
    '-o', $reportDir,
    "-Jprotocol=$protocol",
    "-Jhost=$hostName",
    "-Jport=$port",
    "-Jactivity_id=$activityId",
    "-Jthreads=$threads",
    "-Jramp_up_seconds=$rampUpSeconds",
    "-Jsynchronize_users=$synchronizeUsers",
    "-Jpoll_final_result=$pollFinalResult",
    "-Jpoll_interval_ms=$($config['poll_interval_ms'])",
    "-Jmax_poll_count=$($config['max_poll_count'])",
    "-Jconnect_timeout_ms=$($config['connect_timeout_ms'])",
    "-Jresponse_timeout_ms=$($config['response_timeout_ms'])",
    "-Jcredentials_file=$runtimeCredentialsPath"
)

try {
    & $jmeterBat @jmeterArguments
    $jmeterExitCode = $LASTEXITCODE
} finally {
    # Do not keep the generated plaintext credential copy in result folders.
    if (Test-Path -LiteralPath $runtimeCredentialsPath) {
        Remove-Item -LiteralPath $runtimeCredentialsPath -Force
    }
}
if ($jmeterExitCode -ne 0) {
    throw "JMeter failed with exit code $jmeterExitCode. Log: $jmeterLogPath"
}

if ($resultWaitSeconds -gt 0) {
    Write-Host "Waiting $resultWaitSeconds seconds for the Kafka consumer..."
    Start-Sleep -Seconds $resultWaitSeconds
}

$samples = @(Import-Csv -LiteralPath $jtlPath)
$claimSamples = @($samples | Where-Object { $_.label -like '02-claim-*' })
$acceptedSamples = @($claimSamples | Where-Object { $_.label -eq '02-claim-ACCEPTED' })
$soldOutSamples = @($claimSamples | Where-Object { $_.label -eq '02-claim-SOLD_OUT' })
$technicalFailures = @($claimSamples | Where-Object { $_.success -ne 'true' })
$elapsedValues = @($claimSamples | ForEach-Object { [int]$_.elapsed })

$summaryLines = @(
    "Activity ID: $activityId",
    "Threads: $threads",
    "User mode: $userMode",
    "Claim requests: $($claimSamples.Count)",
    "Redis accepted: $($acceptedSamples.Count)",
    "Sold out: $($soldOutSamples.Count)",
    "Technical failures: $($technicalFailures.Count)",
    "Entry P95: $(Get-Percentile -Values $elapsedValues -Percentile 0.95) ms",
    "Entry P99: $(Get-Percentile -Values $elapsedValues -Percentile 0.99) ms",
    '',
    'See html-report\index.html for TPS and full charts.',
    'Run verify-result.sql to check overselling and duplicate issuance.'
)

$summaryPath = Join-Path $resultDir 'summary.txt'
$summaryLines | Set-Content -LiteralPath $summaryPath -Encoding UTF8

$verificationSql = @"
-- Generated by run-test.ps1.
SET @activity_id = $activityId;

-- 1. Stock must not be negative, and issued + remaining must equal total stock.
SELECT ca.id,
       ca.total_stock,
       ca.remaining_stock,
       (SELECT COUNT(*) FROM user_coupon uc
        WHERE uc.activity_id = ca.id) AS issued_coupons,
       CASE WHEN ca.remaining_stock >= 0 THEN 'PASS' ELSE 'FAIL' END AS stock_non_negative,
       CASE WHEN ca.remaining_stock +
                      (SELECT COUNT(*) FROM user_coupon uc
                       WHERE uc.activity_id = ca.id) = ca.total_stock
            THEN 'PASS' ELSE 'FAIL' END AS stock_consistent
FROM coupon_activity ca
WHERE ca.id = @activity_id;

-- 2. Successful requests must equal issued user coupons.
SELECT
    (SELECT COUNT(*) FROM coupon_claim_request
     WHERE activity_id = @activity_id AND status = 'SUCCESS') AS success_requests,
    (SELECT COUNT(*) FROM user_coupon
     WHERE activity_id = @activity_id) AS issued_coupons;

-- 3. Zero rows means no duplicate coupon issuance.
SELECT user_id, COUNT(*) AS issued_count
FROM user_coupon
WHERE activity_id = @activity_id
GROUP BY user_id
HAVING COUNT(*) > 1;
"@
$verificationPath = Join-Path $resultDir 'verify-result.sql'
$verificationSql | Set-Content -LiteralPath $verificationPath -Encoding UTF8

Write-Host ''
Write-Host '================ TEST SUMMARY ================' -ForegroundColor Green
$summaryLines | ForEach-Object { Write-Host $_ }
Write-Host '==========================================' -ForegroundColor Green
Write-Host "Result directory: $resultDir"

$reportIndex = Join-Path $reportDir 'index.html'
if (Test-Path -LiteralPath $reportIndex) {
    Start-Process -FilePath $reportIndex
}
