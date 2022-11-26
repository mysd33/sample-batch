# SpringBootの非同期/バッチアプリケーションサンプル

## 概要
* Spring BootでSpring JMS + AWS SQS Messaging Libraryを使って、SQSを介在したメッセージング処理方式で、非同期実行依頼を実施するサンプルAPである。
* 非同期実行処理アプリケーションは、Spring Batchを使用しており、非同期実行依頼メッセージで指定されたジョブIDとパラメータのジョブを実行するようになっている。
* 開発端末ローカル実行時にAWS SQSがなくても動作するよう、AP起動時にSQSの代わりに取得したジョブ実行依頼のメッセージをもとに対象のジョブを、SQS互換のFakeとしてElasticMQを組み込みで起動して動作し、特にAWS環境がなくても単独実行可能である。
![構成](img/sample-batch.png)

## プロジェクト構成
* sample-bff
    * Spring BootのWebアプリケーションで、APIから非同期実行依頼が可能である。
        * デフォルトでは「spring.profiles.active」プロパティが「dev」になっていて、プロファイルdevの場合は、sample-batch側で組み込みで起動するElasticMQへ送信するようになっている。
* sample-batch
    * Spring JMSを使ったSpring Bootの非同期処理アプリケーションで、sample-webが送信した非同期実行依頼のメッセージをSQSを介して受信し処理することが可能である。
        * デフォルトでは「spring.profiles.active」プロパティが「dev」になっていて、プロファイルdevの場合は、AP起動時にSQSの代わりにElasticMQを組み込みで起動し、リッスンするようになっている。

## 動作手順
1. Backend AP（sample-backend）の起動
    * sample-backendをSpringBoot Applicationとして起動。

1. Web AP（sample-bff）の起動
    * sample-bffをSpringBoot Applicationとして起動。

1. 非同期AP（sample-batch）の起動
    * sample-batchをSpringBoot Applicationとして起動。

1. 動作確認
    * ブラウザやREST APIクライアント（Postman等）で、以下入力する。 sample-bffのAPがリクエストを受け取り、SQS(ElastiqMQ)へ非同期実行依頼のメッセージを送信する。
    * GETメソッドの場合
    ```
    「http://localhost:8080/api/v1/async/(Job ID)?param01=(任意文字列)&param02=（任意の文字列）」
    #現状Job ID job001またはjob002にする

    #ローカル実行の場合の例
    http://localhost:8080/api/v1/async/job001?param01=aaa&param02=bbb
    
    http://localhost:8080/api/v1/async/job002?param01=aaa&param02=bbb

    #実行後、ブラウザに、以下の応答が返却
    {
        result: "accept"
    }    
    ```
    * POSTメソッドの場合（APIクライアントの使用が必要）
    ```
   「http://localhost:8080/api/v1/async/」

    #リクエストボディの例
    {
        "job_id" : "job001",
        "parameters" : {
            "param01" : "aaa",
            "param02" : "bbb"
        }
    }
    
    {
        "job_id" : "job002",
        "parameters" : {
            "param01" : "aaa",
            "param02" : "bbb"
        }
    }

    #実行後、APIクライアントに以下の応答が返却
    {
        result: "accept"
    }    
    ``` 
1. 動作結果の確認
    * sample-batchのAPが、SQS(ElastiqMQ)を介してsample-webから受け取ったメッセージ（Job IDとparam01、param02の値）を処理する。        
        * TODOリストが書かれたファイル(files/input/todolist.csv)を読み込み、リストに対して一件ずつ、sample-backendのREST APIを呼び出し、TODOリストを一括登録する。
            * job001は、タスクレットモデルで実装している。
            * job002は、チャンクモデルでjob001と同じ処理を実装している。

## AWS SQSと連携したAP動作確認
* デフォルトでは、「spring.profiles.active」プロパティが「dev」になっていて、プロファイルdevの場合、ElasticMQを使用するようになっている。
* AWS上のSQSにアクセスする動作に変更する場合は、例えばJVM引数を「-Dspring.profiles.active=production」に変更するか、環境変数「SPRING_PROFILES_ACTIVE=production」を設定する等して、sample-web、sample-asyncの両方のプロジェクトのプロファイルを「production」に変えて実行する。
* AP実行前に、AWS SQSで標準キューを作成しておく必要がある。application-production.ymlの設定上、 キュー名は「SampleQueue」、キューのリージョンは「ap-northeast-1」（東京）になっていますので、異なる名前、リージョンで作成したい場合は、sample-web、sample-asyncの両方のプロジェクトのapplication-production.ymlの設定も変更する。
* APがSQSにアクセスする権限が必要なので、開発端末上での実行する場合はSQSのアクセス権限をもったIAMユーザのクレデンシャル情報が「%USERPROFILE%/.aws/credentials」や「~/.aws/credentials」に格納されている、もしくはEC2やECS等のAWS上のラインタイム環境で実行する場合は対象のAWSリソースにSQSのアクセス権限を持ったIAMロールが付与されている必要がある。


## ソフトウェアフレームワーク
* 本サンプルアプリケーションでは、ソフトウェアフレームワーク実装例も同梱している。簡単のため、アプリケーションと同じプロジェクトでソース管理している。
* ソースコードはcom.example.fwパッケージ配下に格納されている。    
    * 本格的な開発を実施する場合には、業務アプリケーションと別のGitリポジトリとして管理し、CodeArtifactやSonatype NEXUSといったライブラリリポジトリサーバでjarを管理し、pom.xmlから参照するようにすべきであるし、テストやCI/CD等もちゃんとすべきであるが、ここでは、あえて同じプロジェクトに格納してノウハウを簡単に参考にしてもらいやすいようにしている。
* 各機能と実現方式は、以下の通り。

| 分類 | 機能 | 機能概要と実現方式 | 拡張実装 | 拡張実装の格納パッケージ |
| ---- | ---- | ---- | ---- | ---- |
| バッチ | バッチAP制御 | Spring JMSとAmazon SQS Java Messaging Libraryを利用しSQSの標準キューを介した非同期実行依頼のメッセージを受信し、SpringBatchにより対象のジョブを起動する機能を提供する | ○ | com.example.fw.batch.async |
| | 大量データアクセス | SpringBatchのItemReader、ItemWriterを利用し、大容量のファイルやDBのレコードを逐次読み書きする機能を提供する。 | - | - |
| | 集約例外ハンドリング | エラー（例外）発生時、SpringBatchの機能によりDBのロールバックするとともに、JobExecutionListenerを利用しエラーログの出力といった共通的なエラーハンドリングを実施する。 | ○ | com.example.fw.batch.exeption、com.example.fw.batch.listener |
| | トランザクション管理機能 | Spring Frameworkのトランザクション管理機能を利用して、タスクレットやチャンクに対するトランザクション管理を実現する機能を提供する。 | - | - |
| オン・バッチ共通 | RDBアクセス | MyBatisやSpringとの統合機能を利用し、DBコネクション取得、SQLの実行等のRDBへのアクセスのため定型的な処理を実施し、ORマッピングやSQLマッピングと呼ばれるドメイン層とインフラ層のインピーダンスミスマッチを吸収する機能を提供する。 | - | - |
| | DynamoDBアクセス | AWS SDK for Java 2.xのDynamoDB拡張クライアント（DynamoDbEnhancedClient)を使って、DBへのアクセス機能を提供する。 | - | - |
| | HTTPクライアント | WebClientやRestTemplateを利用してREST APIの呼び出しやサーバエラー時の例外の取り扱いを制御する。 | ○ | com.example.fw.common.httpclient |
| | リトライ・サーキットブレーカ | Spring Cloud Circuit Breaker（Resillience4j）を利用し、REST APIの呼び出しでの一時的な障害に対するリトライやフォールバック処理等を制御する。なお、AWSリソースのAPI呼び出しは、AWS SDKにてエクスポネンシャルバックオフによるリトライ処理を提供済。 | - | - |
| | 非同期実行依頼 | Spring JMS、Amazon SQS Java Messaging Libraryを利用し、SQSの標準キューを介した非同期実行依頼のメッセージを送信する。 | - | - |
| | 入力チェック| Java BeanValidationとSpringのValidation機能を利用し、単項目チェックや相関項目チェックといった画面の入力項目に対する形式的なチェックを実施する。 | ○ | com.example.fw.common.validation |
| | メッセージ管理 | MessageResourceで画面やログに出力するメッセージを管理する。 | ○ | com.example.fw.common.message |
| | 例外 | RuntimeExceptionを継承し、エラーコード（メッセージID）やメッセージを管理可能な共通的なビジネス例外、システム例外を提供する。 | ○ | com.example.fw.common.exception |
| | ロギング | Slf4jとLogback、SpringBootのLogback拡張の機能を利用し、プロファイルによって動作環境に応じたログレベルや出力先（ファイルや標準出力）、出力形式（タブ区切りやJSON）に切替可能とする。またメッセージIDをもとにログ出力可能な汎用的なAPIを提供する。 | ○ | com.example.fw.common.logging |
| | プロパティ管理 | SpringBootのプロパティ管理を使用して、APから環境依存のパラメータを切り出し、プロファイルによって動作環境に応じたパラメータ値に置き換え可能とする。 | - | - |
| | オブジェクトマッピング | MapStructを利用し、類似のプロパティを持つリソースオブジェクトやDTOとドメインオブジェクト間で、値のコピーやデータ変換処理を簡単にかつ高速に行えるようにする。 | - | - |
| | DI | Springを利用し、DI（依存性の注入）機能を提供する。 | - | - |
| | AOP | SpringとAspectJAOPを利用し、AOP機能を提供する。 | - | - |
| | ボイラープレートコード排除 | Lombokを利用し、オブジェクトのコンストラクタやGetter/Setter等のソースコードを自動生成し、ボイラープレートコードを排除する。 | - | - |


* 以下は、今後追加適用を検討中。

| 分類 | 機能 | 機能概要と実現方式 | 拡張実装 | 拡張実装の格納パッケージ |
| ---- | ---- | ---- | ---- | ---- |
| オンバッチ共通 | プロパティ管理（SSM） | Spring Cloud for AWS機能により、APから環境依存のパラメータをAWSのSSMパラメータストアに切り出し、プロファイルによって動作環境に応じたパラメータ値に置き換え可能とする。 | - | - |
| | テストコード作成支援 | JUnit、Mockito、Springのテスト機能を利用して、単体テストコードや結合テストコードの実装を支援する機能を提供する。 | - | - |