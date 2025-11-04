param(
    [Parameter(Mandatory=$true)]
    [string]$itemId,

    [Parameter(Mandatory=$true)]
    [double]$price
)

$formattedPrice = "`$$price USD"
$body = @{
    price = $formattedPrice
} | ConvertTo-Json

Write-Host "⚡ Actualizando precio del item $itemId a $formattedPrice..."

try {
    $response = Invoke-WebRequest `
        -Method PATCH `
        -Uri "http://localhost:8080/api/items/$itemId/price" `
        -ContentType "application/json" `
        -Body $body

    Write-Host "✅ Precio actualizado correctamente!" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "❌ Error actualizando precio: $_" -ForegroundColor Red
}
