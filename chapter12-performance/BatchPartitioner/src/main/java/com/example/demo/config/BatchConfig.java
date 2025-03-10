package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private Partitioner samplePartitioner;

    @Autowired
    private Tasklet workerTasklet;

    /** 非同期実行のTaskExecutor */
    @Bean
    public TaskExecutor asyncTaskExecutor() {
        return new SimpleAsyncTaskExecutor("worker_");
    }

    @Bean
    public Step workerStep() {
        return new StepBuilder("WorkerStep", jobRepository)
            .tasklet(workerTasklet, transactionManager)
            .build();
    }

    @Bean
    public Step partitionStep() {
        return new StepBuilder("PartitionStep", jobRepository)
            .partitioner("WorkerStep", samplePartitioner) // Partitioner
            .step(workerStep()) // step
            .gridSize(3) // 同時実行数
            .taskExecutor(asyncTaskExecutor())
            .build();
    }

    @Bean
    public Job partitionJob() {
        return new JobBuilder("PartitionJob", jobRepository)
            .start(partitionStep())
            .build();
    }
}
