# Spring解体新書(バッチ編)のサンプルのSpringBatch5対応

[Spring解体新書(バッチ編)](https://amzn.to/3B2cMnx)のサンプルをSpringBatch5＆SpringBoot3対応させました。
オリジナルは[こちら](https://github.com/TatsuyaTamura/SpringBatchJP) 。


## 変更点

- Eclipseで一括インポートできるように各pom.xmlのartifactIdに「chapXX-」のようなプレフィックスを付与
- @Configurationクラスの@EnableBatchProcessingを削除
- @ConfigurationクラスからJobBuilderFactoryとJobBuilderFactoryを削除
- @ConfigurationクラスでJobRepositoryとPlatformTransactionManagerを@Autowired
- StepBuilderFactory.get()をnew StepBuilder()に置き換え
- JobBuilderFactory.get()をnew JobBuilder()に置き換え
- chunk()やtasklet()にtransactionManagerを渡すように書き換え
- ItemWriterの引数の型をListからChunkへ変更
- javaxをjakartaに変更(BeanValidationとJPA)
- mybatis-spring-boot-starterのバージョンを3.0.4に変更

## JobReposity

SpringBatch5のJobRepositoryのテーブル群は、SpringBatch4と互換性がありません。
必要に応じてテーブルを削除してください。

``` :sql
drop table if exists batch_job_execution cascade;
drop table if exists batch_job_execution_context;
drop table if exists batch_job_execution_params;
drop table if exists batch_job_instance;
drop table if exists batch_step_execution cascade;
drop table if exists batch_step_execution_context;
```


以下、元の文書です。

# SpringBatchJP
Springバッチ解体新書（書籍）内で載せているサンプルソースです。<br>

解説は以下の書籍に載せています。<br>
https://amzn.to/3B2cMnx

※Kindle Unlimitedを契約の方は無料で読むことができます。
