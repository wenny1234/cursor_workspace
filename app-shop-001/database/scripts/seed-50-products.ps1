# 50件のサンプル商品を admin-shop-manage API 経由で登録
$ErrorActionPreference = "Stop"
$BaseUrl = "http://localhost:8081"
$ImagesDir = Join-Path $PSScriptRoot "..\..\data\images"
$CookieFile = Join-Path $env:TEMP "admin-shop-cookies.txt"

$sourceImages = @(
    "product-1-smartphone.png",
    "product-2-laptop.png",
    "product-3-tshirt.png",
    "product-4-jeans.png",
    "product-5-coffee-maker.png",
    "product-6-earbuds.png",
    "product-7-sneakers.png",
    "product-8-backpack.png"
)

$products = @(
    @{ Name = "ワイヤレスイヤホン Pro"; Category = "電子機器"; Price = 12800; Stock = 45; Desc = "ノイズキャンセリング搭載の高音質イヤホン" }
    @{ Name = "スマートウォッチ S2"; Category = "電子機器"; Price = 24800; Stock = 30; Desc = "健康管理と通知機能付きスマートウォッチ" }
    @{ Name = "タブレット 10インチ"; Category = "電子機器"; Price = 35800; Stock = 22; Desc = "学習・動画視聴に最適な大画面タブレット" }
    @{ Name = "ゲーミングマウス"; Category = "電子機器"; Price = 6800; Stock = 60; Desc = "高精度センサー搭載のゲーミングマウス" }
    @{ Name = "メカニカルキーボード"; Category = "電子機器"; Price = 15800; Stock = 35; Desc = "打鍵感にこだわったメカニカルキーボード" }
    @{ Name = "ポータブルSSD 1TB"; Category = "電子機器"; Price = 11800; Stock = 40; Desc = "高速転送対応の外付けSSD" }
    @{ Name = "Webカメラ HD"; Category = "電子機器"; Price = 8900; Stock = 55; Desc = "テレワーク向けフルHD Webカメラ" }
    @{ Name = "USB-C ハブ 7in1"; Category = "電子機器"; Price = 4500; Stock = 80; Desc = "HDMI・SD・USBポート搭載マルチハブ" }
    @{ Name = "モバイルバッテリー 20000mAh"; Category = "電子機器"; Price = 3980; Stock = 100; Desc = "大容量で急速充電対応のモバイルバッテリー" }
    @{ Name = "Bluetoothスピーカー"; Category = "電子機器"; Price = 7800; Stock = 48; Desc = "防水対応のコンパクトスピーカー" }
    @{ Name = "コットンTシャツ 白"; Category = "ファッション"; Price = 2980; Stock = 150; Desc = "肌触りの良いオーガニックコットンTシャツ" }
    @{ Name = "カーディガン グレー"; Category = "ファッション"; Price = 5980; Stock = 70; Desc = "春秋に活躍するニットカーディガン" }
    @{ Name = "スリムチノパン"; Category = "ファッション"; Price = 4980; Stock = 85; Desc = "ストレッチ性のあるスリムフィットパンツ" }
    @{ Name = "レザーベルト"; Category = "ファッション"; Price = 3480; Stock = 90; Desc = "本革使用のシンプルベルト" }
    @{ Name = "ウールマフラー"; Category = "ファッション"; Price = 4280; Stock = 65; Desc = "暖かいウール100%マフラー" }
    @{ Name = "デニムジャケット"; Category = "ファッション"; Price = 8980; Stock = 40; Desc = "クラシックなデニムジャケット" }
    @{ Name = "ポロシャツ ネイビー"; Category = "ファッション"; Price = 3980; Stock = 95; Desc = "ビジネスカジュアルに使えるポロシャツ" }
    @{ Name = "スニーカー 白"; Category = "ファッション"; Price = 7980; Stock = 75; Desc = "デイリー使いできるホワイトスニーカー" }
    @{ Name = "サンダル 夏用"; Category = "ファッション"; Price = 2980; Stock = 110; Desc = "軽量で歩きやすいサンダル" }
    @{ Name = "トートバッグ キャンバス"; Category = "ファッション"; Price = 3580; Stock = 88; Desc = "A4サイズが入るキャンバストート" }
    @{ Name = "ロボット掃除機"; Category = "家電"; Price = 49800; Stock = 15; Desc = "自動充電対応のロボット掃除機" }
    @{ Name = "空気清浄機 小型"; Category = "家電"; Price = 19800; Stock = 25; Desc = "寝室向けコンパクト空気清浄機" }
    @{ Name = "加湿器 超音波"; Category = "家電"; Price = 6800; Stock = 42; Desc = "静音設計の卓上加湿器" }
    @{ Name = "電気ケトル 1.7L"; Category = "家電"; Price = 3980; Stock = 60; Desc = "急速沸騰のステンレス電気ケトル" }
    @{ Name = "IH調理器 1口"; Category = "家電"; Price = 12800; Stock = 28; Desc = "一人暮らしに便利な卓上IHクッカー" }
    @{ Name = "電子レンジ 20L"; Category = "家電"; Price = 15800; Stock = 20; Desc = "シンプル操作の電子レンジ" }
    @{ Name = "ヘアドライヤー 大風量"; Category = "家電"; Price = 8900; Stock = 38; Desc = "マイナスイオン機能付きドライヤー" }
    @{ Name = "電動歯ブラシ"; Category = "家電"; Price = 7800; Stock = 50; Desc = "音波振動の電動歯ブラシセット" }
    @{ Name = "アイロン スチーム"; Category = "家電"; Price = 5800; Stock = 33; Desc = "スチーム機能付きコードレスアイロン" }
    @{ Name = "扇風機 リビング用"; Category = "家電"; Price = 9800; Stock = 30; Desc = "リモコン付き静音扇風機" }
    @{ Name = "オーガニック緑茶 100g"; Category = "食品・飲料"; Price = 1280; Stock = 200; Desc = "静岡産の香り高い緑茶" }
    @{ Name = "ドリップコーヒー 20袋"; Category = "食品・飲料"; Price = 1980; Stock = 180; Desc = "厳選豆を使用したドリップコーヒー" }
    @{ Name = "ミネラルウォーター 24本"; Category = "食品・飲料"; Price = 1480; Stock = 250; Desc = "天然水のミネラルウォーター" }
    @{ Name = "プロテインバー 12本"; Category = "食品・飴料"; Price = 2480; Stock = 120; Desc = "運動後に便利なプロテインバー" }
    @{ Name = "ナッツミックス 500g"; Category = "食品・飲料"; Price = 1680; Stock = 140; Desc = "アーモンド・くるみ・カシューナッツミックス" }
    @{ Name = "はちみつ 純国産"; Category = "食品・飲料"; Price = 2180; Stock = 90; Desc = "非加熱の純国産はちみつ" }
    @{ Name = "オリーブオイル エクストラ"; Category = "食品・飲料"; Price = 1780; Stock = 85; Desc = "サラダや料理に使えるエクストラバージン" }
    @{ Name = "インスタントラーメン 5食"; Category = "食品・飲料"; Price = 680; Stock = 300; Desc = "人気の醤油味インスタントラーメン" }
    @{ Name = "チョコレート 詰め合わせ"; Category = "食品・飲料"; Price = 2980; Stock = 75; Desc = "ギフトにも最適なチョコレートセット" }
    @{ Name = "フルーツティー 30袋"; Category = "食品・飲料"; Price = 980; Stock = 160; Desc = "カフェイン不使用のハーブティー" }
    @{ Name = "ヨガマット 10mm"; Category = "スポーツ"; Price = 3980; Stock = 55; Desc = "滑り止め付きの厚手ヨガマット" }
    @{ Name = "ダンベルセット 5kg×2"; Category = "スポーツ"; Price = 6980; Stock = 40; Desc = "ホームトレーニング用ダンベル" }
    @{ Name = "ランニングシューズ"; Category = "スポーツ"; Price = 9980; Stock = 45; Desc = "クッション性に優れたランニングシューズ" }
    @{ Name = "トレーニングウェア 上下"; Category = "スポーツ"; Price = 5480; Stock = 60; Desc = "吸汗速乾のスポーツウェアセット" }
    @{ Name = "テニスラケット"; Category = "スポーツ"; Price = 12800; Stock = 25; Desc = "初心者向けライトウェイトラケット" }
    @{ Name = "水泳ゴーグル"; Category = "スポーツ"; Price = 2480; Stock = 80; Desc = "曇り止めコーティング付きゴーグル" }
    @{ Name = "サッカーボール 5号"; Category = "スポーツ"; Price = 3580; Stock = 50; Desc = "公式サイズの練習用サッカーボール" }
    @{ Name = "自転車ヘルメット"; Category = "スポーツ"; Price = 4980; Stock = 35; Desc = "軽量で通気性の良いヘルメット" }
    @{ Name = "ジャンプロープ"; Category = "スポーツ"; Price = 1280; Stock = 100; Desc = "長さ調節可能な縄跳び" }
    @{ Name = "プロテインシェイカー"; Category = "スポーツ"; Price = 980; Stock = 150; Desc = "漏れ防止設計のシェイカーボトル" }
    @{ Name = "ビジネス書 生産性向上"; Category = "書籍"; Price = 1680; Stock = 70; Desc = "仕事効率を上げる実践的ビジネス書" }
    @{ Name = "プログラミング入門"; Category = "書籍"; Price = 2980; Stock = 55; Desc = "初心者向けプログラミング学習書" }
    @{ Name = "料理レシピ集"; Category = "書籍"; Price = 1980; Stock = 65; Desc = "毎日使える簡単レシピ100選" }
    @{ Name = "写真集 風景"; Category = "書籍"; Price = 3480; Stock = 30; Desc = "日本の美しい風景写真集" }
    @{ Name = "英語学習 単語帳"; Category = "書籍"; Price = 1280; Stock = 90; Desc = "TOEIC対策に使える単語帳" }
    @{ Name = "フェイスマスク 30枚"; Category = "美容・健康"; Price = 1980; Stock = 120; Desc = "保湿成分配合のシートマスク" }
    @{ Name = "ボディソープ 詰替"; Category = "美容・健康"; Price = 980; Stock = 180; Desc = "肌にやさしい低刺激ボディソープ" }
    @{ Name = "ビタミンC サプリ 90粒"; Category = "美容・健康"; Price = 2480; Stock = 95; Desc = "毎日の健康維持にビタミンCサプリメント" }
)

# タイポ修正
$products = $products | ForEach-Object {
    if ($_.Category -eq "食品・飴料") { $_.Category = "食品・飲料" }
    $_
}

function Ensure-Login {
    if (Test-Path $CookieFile) { Remove-Item $CookieFile -Force }
    curl.exe -s -c $CookieFile -b $CookieFile "$BaseUrl/login" -o NUL | Out-Null
    $code = curl.exe -s -c $CookieFile -b $CookieFile -X POST "$BaseUrl/login" `
        -H "Content-Type: application/x-www-form-urlencoded" `
        -d "username=admin&password=admin123" -o NUL -w "%{http_code}"
    if ($code -ne "302") {
        throw "ログインに失敗しました (HTTP $code)"
    }
}

Write-Host "画像ファイルを準備中..."
for ($i = 0; $i -lt $products.Count; $i++) {
    $num = $i + 10
    $src = $sourceImages[$i % $sourceImages.Count]
    $destName = "product-$num-seed.png"
    $destPath = Join-Path $ImagesDir $destName
    Copy-Item -Path (Join-Path $ImagesDir $src) -Destination $destPath -Force
}

Write-Host "ログイン中..."
Ensure-Login

$created = 0
$failed = 0
for ($i = 0; $i -lt $products.Count; $i++) {
    $p = $products[$i]
    $num = $i + 10
    $imageUrl = "/api/files/product-$num-seed.png"
    $body = @{
        name        = $p.Name
        description = $p.Desc
        price       = $p.Price
        stock       = $p.Stock
        category    = $p.Category
        imageUrl    = $imageUrl
        active      = $true
    } | ConvertTo-Json -Compress

    $response = curl.exe -s -b $CookieFile -X POST "$BaseUrl/api/products" `
        -H "Content-Type: application/json" `
        -d $body `
        -w "`n%{http_code}"

    $lines = $response -split "`n"
    $httpCode = $lines[-1]
    if ($httpCode -eq "200") {
        $created++
        Write-Host "  OK [$created/$($products.Count)] $($p.Name)"
    } else {
        $failed++
        Write-Host "  NG $($p.Name): $response"
    }
}

Write-Host ""
Write-Host "完了: $created 件登録, $failed 件失敗"
