package com.example.demo.config;

import java.nio.charset.StandardCharsets;

import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.domain.model.Employee;

public abstract class BaseConfig {

    @Autowired
    protected JobRepository jobRepository;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    /** 性別の数値を文字列に変換するProcessor */
    @Autowired
    protected ItemProcessor<Employee, Employee> genderConvertProcessor;

    @Autowired
    protected SampleProperty property;

    @Autowired
    protected ItemReadListener<Employee> readListener;

    @Autowired
    protected ItemWriteListener<Employee> writeListener;

    @Autowired
    protected FlatFileHeaderCallback csvHeaderCallback;

    @Autowired
    protected FlatFileFooterCallback csvFooterCallback;

    /** csv出力のWriterを生成 */
    @Bean
    @StepScope
    public FlatFileItemWriter<Employee> csvWriter() {

        // ファイル出力先設定
        String filePath = property.outputPath();
        WritableResource outputResource = new FileSystemResource(filePath);
        
        // 区切り文字設定
        DelimitedLineAggregator<Employee> aggregator = 
                new DelimitedLineAggregator<Employee>();
        aggregator.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
        
        // 出力フィールドの設定
        BeanWrapperFieldExtractor<Employee> extractor = 
                new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[] {"id", "name", "age", "genderString"});
        aggregator.setFieldExtractor(extractor);
        
        return new FlatFileItemWriterBuilder<Employee>()
                .name("employeeCsvWriter") // 名前
                .resource(outputResource) // ファイル出力先
                .append(false) // 追記設定
                .lineAggregator(aggregator) // 区切り文字
                .headerCallback(csvHeaderCallback) // ヘッダー
                .footerCallback(csvFooterCallback) // フッター
                .encoding(StandardCharsets.UTF_8.name()) // 文字コード
                .build();
    }
}
