package com.example.demo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.model.Employee;

@Configuration
public class MyBatisImportBatchConfig extends BaseConfig {

    /** SqlSessionFactory(MyBatisで必要) */
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /** Writer(MyBatis) */
    @Bean
    public MyBatisBatchItemWriter<Employee> mybatisWriter() {
        return new MyBatisBatchItemWriterBuilder<Employee>()
            .sqlSessionFactory(sqlSessionFactory)
            .statementId("com.example.demo.repository.EmployeeMapper.insertOne")
            .build();
    }

    /** Stepの生成(MyBatis) */
    @Bean
    public Step csvImportMybatisStep() {
        return new StepBuilder("CsvImportMybatisStep", jobRepository)
            .<Employee, Employee>chunk(10, transactionManager)
            .reader(csvReader()).listener(this.readListener)
            .processor(compositeProcessor()).listener(this.processListener)
            .writer(mybatisWriter()).listener(this.writeListener)
            .build();
    }

    /** Jobの生成(MyBatis) */
    @Bean("MybatisJob")
    public Job csvImportMybatisJob() {
        return new JobBuilder("CsvImportMybatisJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(csvImportMybatisStep())
            .build();
    }
}
