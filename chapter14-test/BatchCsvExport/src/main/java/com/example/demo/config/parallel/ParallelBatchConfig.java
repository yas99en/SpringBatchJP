package com.example.demo.config.parallel;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class ParallelBatchConfig extends BaseConfig {

    @Autowired
    private JdbcPagingItemReader<Employee> jdbcPagingReader;

    @Bean
    public TaskExecutor asyncTaskExecutor() {
        return new SimpleAsyncTaskExecutor("parallel_");
    }

    /** Stepを生成 */
    @Bean
    public Step exportParallelStep() throws Exception {
        return new StepBuilder("ExportParallelStep", jobRepository)
            .<Employee, Employee>chunk(10, transactionManager)
            .reader(jdbcPagingReader).listener(readListener)
            .processor(this.genderConvertProcessor)
            .writer(csvWriter()).listener(writeListener)
            .taskExecutor(asyncTaskExecutor()) // executor
            .throttleLimit(3) // 同時実行数
            .build(); // Stepの生成
    }

    /** Jobを生成 */
//    @Bean
    public Job exportParalleljob() throws Exception {
        return new JobBuilder("ExportParallelJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(exportParallelStep())
            .build();
    }
}
