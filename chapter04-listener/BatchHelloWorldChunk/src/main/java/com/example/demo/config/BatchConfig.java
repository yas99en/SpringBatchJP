package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /** HelloReader */
    @Autowired
    private ItemReader<String> reader;

    /** HelloProcessor */
    @Autowired
    private ItemProcessor<String, String> processor;

    /** HelloWriter */
    @Autowired
    private ItemWriter<String> writer;

    @Autowired
    private JobExecutionListener jobListener;

    @Autowired
    private StepExecutionListener stepListener;

    /** ChunkのStepを生成 */
    @Bean
    public Step chunkStep() {
        return new StepBuilder("HelloChunkStep", jobRepository) // Builderの取得
            .<String, String>chunk(3, transactionManager) // チャンクの設定
            .reader(reader) // readerセット
            .processor(processor) // processorセット
            .writer(writer) // writerセット
            .listener(stepListener) // StepListner
            .build(); // Stepの生成
    }

    /** Jobを生成 */
    @Bean
    public Job chunkJob() throws Exception {
        return new JobBuilder("HelloWorldChunkJob", jobRepository) // Builderの取得
            .incrementer(new RunIdIncrementer()) // IDのインクリメント
            .start(chunkStep()) // 最初のStep
            .listener(jobListener) // JobListner
            .build(); // Jobの生成
    }
}
