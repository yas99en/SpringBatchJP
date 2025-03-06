package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    /** HelloTasklet */
    @Autowired
    private Tasklet helloTasklet;

    /** TaskletのStepを生成 */
    @Bean
    public Step taskletStep1() {
        return new StepBuilder("HelloTaskletStep1", jobRepository) // Builderの取得
            .tasklet(helloTasklet, transactionManager) // Taskletのセット
            .build(); // Stepの生成
    }

    /** Jobを生成 */
    @Bean
    public Job taskletJob() throws Exception {
        return new JobBuilder("HelloWorldTaskletJob", jobRepository) // Builderの取得
            .incrementer(new RunIdIncrementer()) // IDのインクリメント
            .start(taskletStep1()) // 最初のStep
            .build(); // Jobの生成
    }
}
