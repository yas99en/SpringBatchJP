# Spring解体新書(バッチ編)のサンプルのSpringBatch5対応

[Spring解体新書(バッチ編)](https://amzn.to/3B2cMnx)のサンプルをSpringBatch5＆SpringBoot3対応させました。
オリジナルは[こちら](https://github.com/TatsuyaTamura/SpringBatchJP) 。


## 変更点

- Eclipseで一括インポートできるように各pom.xmlのartifactIdに「chapXX-」のようなプレフィックスを付与
- SpringBootのバージョンを3.4.3に変更
- java.versionを21に変更
- @Configurationクラスの@EnableBatchProcessingを削除
- @ConfigurationクラスからJobBuilderFactoryとJobBuilderFactoryを削除
- @ConfigurationクラスでJobRepositoryとPlatformTransactionManagerを@Autowired
- StepBuilderFactory.get()をnew StepBuilder()に置き換え
- JobBuilderFactory.get()をnew JobBuilder()に置き換え
- chunk()やtasklet()にtransactionManagerを渡すように書き換え
- ItemWriterの引数の型をListからChunkへ変更
- javaxをjakartaに変更(BeanValidationとJPA)
- mybatis-spring-boot-starterのバージョンを3.0.4に変更
- chap12-BatchCsvExportのParallelBatchConfigはthrottleLimitが非推奨のまま
- chap13-BatchInMemoryはSpringBatch5に対応できない
- chap14全般  
テストを実行すると警告が出るのでpom.xmlに以下を追加
```:xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
		<argLine>
			-javaagent:${settings.localRepository}/org/mockito/mockito-core/${mockito.version}/mockito-core-${mockito.version}.jar
			-Xshare:off
		</argLine>
	</configuration>
</plugin>
```
Eclipseからのテスト実行時の警告を消すにはVM引数に以下を追加する
```
-Xshare:off
-javaagent:${env_var:userprofile}\.m2\repository\org\mockito\mockito-core\5.14.2\mockito-core-5.14.2.jar
```
- chap14-BatchCsvImortとchap14-BatchCsvExport  
ジョブが複数定義されているとIntegrationTestでエラーになるので、使用するジョブ以外はコメントアウト
- chap14-BatchCsvExport  
org.springframework.batch.test.AssertFileが存在しないので、AssertJのhasSameTextualContentAsで代用

## JobReposity

SpringBatch5のJobRepositoryのテーブル群は、SpringBatch4と互換性がありません。
必要に応じてテーブルを削除してください。

``` :sql
DROP TABLE  IF EXISTS BATCH_STEP_EXECUTION_CONTEXT;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION_CONTEXT;
DROP TABLE  IF EXISTS BATCH_STEP_EXECUTION;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION_PARAMS;
DROP TABLE  IF EXISTS BATCH_JOB_EXECUTION;
DROP TABLE  IF EXISTS BATCH_JOB_INSTANCE;

DROP SEQUENCE  IF EXISTS BATCH_STEP_EXECUTION_SEQ;
DROP SEQUENCE  IF EXISTS BATCH_JOB_EXECUTION_SEQ;
DROP SEQUENCE  IF EXISTS BATCH_JOB_SEQ;
```


以下、元の文書です。

# SpringBatchJP
Springバッチ解体新書（書籍）内で載せているサンプルソースです。<br>

解説は以下の書籍に載せています。<br>
https://amzn.to/3B2cMnx

※Kindle Unlimitedを契約の方は無料で読むことができます。
