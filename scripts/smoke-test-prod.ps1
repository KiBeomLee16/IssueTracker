param(
    [string]$BaseUrl = "http://localhost:8080",
    [string]$AdminUserId = "admin01",
    [string]$OwnerUserId = "owner01",
    [string]$Password = "password"
)

$ErrorActionPreference = "Stop"

$base = $BaseUrl.TrimEnd("/")
$results = New-Object System.Collections.Generic.List[object]
$created = @{
    projectId = $null
    issueId = $null
    labelId = $null
    commentId = $null
    memberUserId = $null
    apiAdminId = $null
    signupUserId = $null
}

function Add-Result {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        [int]$Expected,
        [int]$Status,
        [bool]$Ok,
        [string]$Message
    )

    $script:results.Add([PSCustomObject]@{
        Result = if ($Ok) { "PASS" } else { "FAIL" }
        Name = $Name
        Method = $Method
        Path = $Path
        Expected = $Expected
        Status = $Status
        Message = $Message
    }) | Out-Null
}

function Invoke-Api {
    param(
        [string]$Name,
        [string]$Method,
        [string]$Path,
        [string]$Token,
        [object]$Body,
        [int]$Expected
    )

    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $params = @{
        Method = $Method
        Uri = "$script:base$Path"
        Headers = $headers
        UseBasicParsing = $true
        ErrorAction = "Stop"
    }

    if ($null -ne $Body) {
        $params["ContentType"] = "application/json"
        $params["Body"] = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        $response = Invoke-WebRequest @params
        $json = $null

        if ($response.Content) {
            try {
                $json = $response.Content | ConvertFrom-Json
            } catch {
                $json = $null
            }
        }

        $status = [int]$response.StatusCode
        $successOk = ($null -eq $json) -or ($null -eq $json.PSObject.Properties["success"]) -or ([bool]$json.success)
        $ok = ($status -eq $Expected) -and $successOk
        $message = if ($json -and $json.PSObject.Properties["message"]) { $json.message } else { "" }

        Add-Result $Name $Method $Path $Expected $status $ok $message
        return [PSCustomObject]@{ Ok = $ok; Status = $status; Json = $json }
    } catch {
        $status = 0
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
        }

        $message = $_.ErrorDetails.Message
        if ([string]::IsNullOrWhiteSpace($message)) {
            $message = $_.Exception.Message
        }

        Add-Result $Name $Method $Path $Expected $status $false $message
        return [PSCustomObject]@{ Ok = $false; Status = $status; Json = $null }
    }
}

function Invoke-Raw {
    param(
        [string]$Method,
        [string]$Path,
        [string]$Token,
        [object]$Body
    )

    $headers = @{}
    if ($Token) {
        $headers["Authorization"] = "Bearer $Token"
    }

    $params = @{
        Method = $Method
        Uri = "$script:base$Path"
        Headers = $headers
        UseBasicParsing = $true
        ErrorAction = "Stop"
    }

    if ($null -ne $Body) {
        $params["ContentType"] = "application/json"
        $params["Body"] = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        Invoke-WebRequest @params | Out-Null
    } catch {
        # Best-effort cleanup should not hide the original smoke-test result.
    }
}

function Get-Token {
    param([string]$UserId)

    $login = Invoke-Api "auth login $UserId" "POST" "/api/auth/login" $null @{
        userId = $UserId
        password = $script:Password
    } 200

    if (-not $login.Ok) {
        throw "Login failed for $UserId"
    }

    return $login.Json.data.accessToken
}

function Remove-SmokeProjects {
    param([string]$OwnerToken)

    try {
        $response = Invoke-RestMethod -Method Get -Uri "$script:base/api/projects" -Headers @{
            Authorization = "Bearer $OwnerToken"
        }

        foreach ($project in @($response.data | Where-Object { $_.name -like "Smoke Project*" })) {
            Invoke-Raw "DELETE" "/api/projects/$($project.id)" $OwnerToken $null
        }
    } catch {
    }
}

function Cleanup-CreatedData {
    param(
        [string]$OwnerToken,
        [string]$AdminToken
    )

    if ($script:created.commentId) {
        Invoke-Raw "DELETE" "/api/comments/$($script:created.commentId)" $OwnerToken $null
    }

    if ($script:created.issueId) {
        Invoke-Raw "DELETE" "/api/issues/$($script:created.issueId)" $OwnerToken $null
    }

    if ($script:created.labelId) {
        Invoke-Raw "DELETE" "/api/labels/$($script:created.labelId)" $OwnerToken $null
    }

    if ($script:created.projectId -and $script:created.memberUserId) {
        Invoke-Raw "DELETE" "/api/projects/$($script:created.projectId)/members/$($script:created.memberUserId)" $OwnerToken $null
    }

    if ($script:created.projectId) {
        Invoke-Raw "DELETE" "/api/projects/$($script:created.projectId)" $OwnerToken $null
    }

    foreach ($userId in @($script:created.memberUserId, $script:created.apiAdminId, $script:created.signupUserId)) {
        if ($userId) {
            Invoke-Raw "DELETE" "/api/users/$userId" $AdminToken $null
        }
    }
}

$stamp = (Get-Date).ToString("yyyyMMddHHmmss")
$dueDate = (Get-Date).AddDays(14).ToString("yyyy-MM-dd")
$adminToken = $null
$ownerToken = $null

try {
    Invoke-Api "GET /actuator/health" "GET" "/actuator/health" $null $null 200 | Out-Null
    Invoke-Api "GET /v3/api-docs" "GET" "/v3/api-docs" $null $null 200 | Out-Null

    $adminToken = Get-Token $AdminUserId
    $ownerToken = Get-Token $OwnerUserId
    Remove-SmokeProjects $ownerToken

    $signupUserText = "smoke_signup_$stamp"
    $signup = Invoke-Api "POST /api/auth/signup" "POST" "/api/auth/signup" $null @{
        name = "Smoke Signup"
        email = "smoke.signup.$stamp@example.com"
        userId = $signupUserText
        password = $Password
    } 200
    $created.signupUserId = $signup.Json.data.id

    $signupLogin = Invoke-Api "POST /api/auth/login" "POST" "/api/auth/login" $null @{
        userId = $signupUserText
        password = $Password
    } 200
    $signupRefreshToken = $signupLogin.Json.data.refreshToken

    Invoke-Api "POST /api/auth/refresh" "POST" "/api/auth/refresh" $null @{ refreshToken = $signupRefreshToken } 200 | Out-Null
    Invoke-Api "POST /api/auth/logout" "POST" "/api/auth/logout" $null @{ refreshToken = $signupRefreshToken } 200 | Out-Null

    $memberUserText = "smoke_member_$stamp"
    $memberUser = Invoke-Api "POST /api/users" "POST" "/api/users" $adminToken @{
        name = "Smoke Member"
        email = "smoke.member.$stamp@example.com"
        userId = $memberUserText
        password = $Password
    } 201
    $created.memberUserId = $memberUser.Json.data.id

    $apiAdminText = "smoke_admin_$stamp"
    $apiAdmin = Invoke-Api "POST /api/users/admin" "POST" "/api/users/admin" $adminToken @{
        name = "Smoke Admin"
        email = "smoke.admin.$stamp@example.com"
        userId = $apiAdminText
        password = $Password
    } 201
    $created.apiAdminId = $apiAdmin.Json.data.id

    Invoke-Api "GET /api/users" "GET" "/api/users" $adminToken $null 200 | Out-Null
    Invoke-Api "GET /api/users/{id}" "GET" "/api/users/$($created.memberUserId)" $adminToken $null 200 | Out-Null
    Invoke-Api "PUT /api/users/{id}" "PUT" "/api/users/$($created.memberUserId)" $adminToken @{
        name = "Smoke Member Updated"
        email = "smoke.member.updated.$stamp@example.com"
        userId = "smoke_member_updated_$stamp"
    } 200 | Out-Null

    $project = Invoke-Api "POST /api/projects" "POST" "/api/projects" $ownerToken @{
        name = "Smoke Project $stamp"
        description = "temporary project for prod swagger smoke test"
    } 201
    $created.projectId = $project.Json.data.id

    Invoke-Api "GET /api/projects" "GET" "/api/projects" $ownerToken $null 200 | Out-Null
    Invoke-Api "GET /api/projects/{projectId}" "GET" "/api/projects/$($created.projectId)" $ownerToken $null 200 | Out-Null
    Invoke-Api "PUT /api/projects/{projectId}" "PUT" "/api/projects/$($created.projectId)" $ownerToken @{
        name = "Smoke Project Updated $stamp"
        description = "updated temporary project"
        status = "ACTIVE"
    } 200 | Out-Null
    Invoke-Api "GET /api/projects/{projectId}/stats" "GET" "/api/projects/$($created.projectId)/stats" $ownerToken $null 200 | Out-Null

    Invoke-Api "GET /api/projects/{projectId}/members" "GET" "/api/projects/$($created.projectId)/members" $ownerToken $null 200 | Out-Null
    Invoke-Api "POST /api/projects/{projectId}/members" "POST" "/api/projects/$($created.projectId)/members" $ownerToken @{ userId = $created.memberUserId } 201 | Out-Null
    Invoke-Api "DELETE /api/projects/{projectId}/members/{userId}" "DELETE" "/api/projects/$($created.projectId)/members/$($created.memberUserId)" $ownerToken $null 200 | Out-Null
    Invoke-Api "POST /api/projects/{projectId}/members add again" "POST" "/api/projects/$($created.projectId)/members" $ownerToken @{ userId = $created.memberUserId } 201 | Out-Null

    $label = Invoke-Api "POST /api/projects/{projectId}/labels" "POST" "/api/projects/$($created.projectId)/labels" $ownerToken @{
        name = "smoke-label-$stamp"
        color = "#2563eb"
    } 201
    $created.labelId = $label.Json.data.id
    Invoke-Api "GET /api/projects/{projectId}/labels" "GET" "/api/projects/$($created.projectId)/labels" $ownerToken $null 200 | Out-Null

    $issue = Invoke-Api "POST /api/projects/{projectId}/issues" "POST" "/api/projects/$($created.projectId)/issues" $ownerToken @{
        title = "Smoke issue $stamp"
        description = "temporary issue for prod swagger smoke test"
        status = "TODO"
        priority = "HIGH"
        dueDate = $dueDate
    } 201
    $created.issueId = $issue.Json.data.id

    Invoke-Api "GET /api/projects/{projectId}/issues" "GET" "/api/projects/$($created.projectId)/issues" $ownerToken $null 200 | Out-Null
    Invoke-Api "GET /api/projects/{projectId}/issues/page" "GET" "/api/projects/$($created.projectId)/issues/page?status=TODO&priority=HIGH&keyword=Smoke&page=0&size=10&sortBy=id&direction=desc" $ownerToken $null 200 | Out-Null
    Invoke-Api "GET /api/issues/{issueId}" "GET" "/api/issues/$($created.issueId)" $ownerToken $null 200 | Out-Null
    Invoke-Api "PUT /api/issues/{issueId}" "PUT" "/api/issues/$($created.issueId)" $ownerToken @{
        title = "Smoke issue updated $stamp"
        description = "updated temporary issue"
        status = "TODO"
        priority = "MEDIUM"
        dueDate = $dueDate
    } 200 | Out-Null
    Invoke-Api "PATCH /api/issues/{issueId}/assignee" "PATCH" "/api/issues/$($created.issueId)/assignee" $ownerToken @{ assigneeId = $created.memberUserId } 200 | Out-Null
    Invoke-Api "PATCH /api/issues/{issueId}/status" "PATCH" "/api/issues/$($created.issueId)/status" $ownerToken @{ status = "IN_PROGRESS" } 200 | Out-Null
    Invoke-Api "PUT /api/issues/{issueId}/labels" "PUT" "/api/issues/$($created.issueId)/labels" $ownerToken @{ labelIds = @($created.labelId) } 200 | Out-Null
    Invoke-Api "GET /api/issues/{issueId}/histories" "GET" "/api/issues/$($created.issueId)/histories" $ownerToken $null 200 | Out-Null
    Invoke-Api "DELETE /api/issues/{issueId}/assignee" "DELETE" "/api/issues/$($created.issueId)/assignee" $ownerToken $null 200 | Out-Null

    $comment = Invoke-Api "POST /api/issues/{issueId}/comments" "POST" "/api/issues/$($created.issueId)/comments" $ownerToken @{
        content = "Smoke comment $stamp"
    } 200
    $created.commentId = $comment.Json.data.id

    Invoke-Api "GET /api/issues/{issueId}/comments" "GET" "/api/issues/$($created.issueId)/comments" $ownerToken $null 200 | Out-Null
    Invoke-Api "GET /api/comments/{commentId}" "GET" "/api/comments/$($created.commentId)" $ownerToken $null 200 | Out-Null
    Invoke-Api "PUT /api/comments/{commentId}" "PUT" "/api/comments/$($created.commentId)" $ownerToken @{ content = "Smoke comment updated $stamp" } 200 | Out-Null
    Invoke-Api "DELETE /api/comments/{commentId}" "DELETE" "/api/comments/$($created.commentId)" $ownerToken $null 200 | Out-Null
    $created.commentId = $null

    Invoke-Api "DELETE /api/issues/{issueId}" "DELETE" "/api/issues/$($created.issueId)" $ownerToken $null 200 | Out-Null
    $created.issueId = $null
    Invoke-Api "DELETE /api/labels/{labelId}" "DELETE" "/api/labels/$($created.labelId)" $ownerToken $null 200 | Out-Null
    $created.labelId = $null
    Invoke-Api "DELETE /api/projects/{projectId}/members/{userId} cleanup" "DELETE" "/api/projects/$($created.projectId)/members/$($created.memberUserId)" $ownerToken $null 200 | Out-Null
    Invoke-Api "DELETE /api/projects/{projectId}" "DELETE" "/api/projects/$($created.projectId)" $ownerToken $null 200 | Out-Null
    $created.projectId = $null
    Invoke-Api "DELETE /api/users/{id} member" "DELETE" "/api/users/$($created.memberUserId)" $adminToken $null 200 | Out-Null
    $created.memberUserId = $null
    Invoke-Api "DELETE /api/users/{id} admin" "DELETE" "/api/users/$($created.apiAdminId)" $adminToken $null 200 | Out-Null
    $created.apiAdminId = $null
    Invoke-Api "DELETE /api/users/{id} signup" "DELETE" "/api/users/$($created.signupUserId)" $adminToken $null 200 | Out-Null
    $created.signupUserId = $null
} finally {
    if ($ownerToken -and $adminToken) {
        Cleanup-CreatedData $ownerToken $adminToken
    }
}

$results | Format-Table -AutoSize

$failCount = ($results | Where-Object { $_.Result -eq "FAIL" }).Count
$totalCount = $results.Count
Write-Host "TOTAL=$totalCount FAIL=$failCount"

if ($failCount -gt 0) {
    exit 1
}
