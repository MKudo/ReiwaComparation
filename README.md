# ReiwaComparation
新元号「令和」の「令」の字が複数存在して面倒、という話を検証する

## Description
Unicode の入力で元号「令和」とみなさないといけないのは

- U+32FF
- U+4EE4 U+548C
- U+F9A8 U+548C
- U+4EE4 U+FE00 U+548C
- U+4EE4 U+E0100 U+548C
- U+4EE4 U+E0101 U+548C
- U+4EE4 U+E0102 U+548C

くらいある、とのことなので、どうやってチェックすればいいのか検証した

## Conclusion

1. Normalize (検証では NFKC を選択) して
1. VS 落として
1. 比較
1. 合字はいずれ Normalize で対応されるはずだけど、暫定的に個別チェックしておくこと

## How to do it

- [検証コード] (/blob/master/src/main/java/io/github/mkudo/compare/Reiwa.java) 読んで下さい

java プログラムで何やってるかよく分からない人はググってください

## Detail
### What is the standard era name

- U+4EE4 U+548C

これはいずれも CJK 統合漢字。元号は入力を保存する必要が無い（名前や地名は入力されてきたデータをそのまま保持した方が良いけど元号は標準のものに変えてしまっても問題ないはず）ので、最終的にはこれになればいいはず

### Why need to normalize

- U+F9A8 は CJK 拡張漢字なので normalize すれば CJK 統合漢字 U+4EE4 になる
- そもそも web アプリでユーザー入力の検証は、検証前に normalize することが推奨されているので、普通、やっているはず

web アプリ開発者で normalize 知らない人は以下のリンクを読んでおきましょう（大事なリンクなので二回貼りました）
- [文字列は検査するまえに標準化する] (https://www.jpcert.or.jp/java-rules/ids01-j.html)
- [文字列は検査するまえに標準化する] (https://www.jpcert.or.jp/java-rules/ids01-j.html)

### Why need to remove VS

- IVS は統合漢字にしか付けられない
- 異体字が他にいくらあってもこのルールは変わらない
- 単純に削除してしまえば CJK 統合漢字 U+4EE4 だけが残る

## Limitation

CJK 統合拡張に「令」や「和」の異体字があるかはチェックしていないので、そこで事故ったらごめんなさい

## Authors
- [Masahiko Kudo](https://github.com/MKudo)
