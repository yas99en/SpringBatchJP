package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

@Configuration
public class JpaCursorBatchConfig extends BaseConfig {

    /** EntityManagerFactory(JPAで必要) */
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    /** JpaCursorItemReader */
    @Bean
    @StepScope
    public JpaCursorItemReader<Employee> jpaCursorReader() {
        // SQL
        String sql = "select * from employee where gender = :genderParam";
        // クエリーの設定
        JpaNativeQueryProvider<Employee> queryProvider = 
                new JpaNativeQueryProvider<>();
        queryProvider.setSqlQuery(sql);
        queryProvider.setEntityClass(Employee.class);

        // クエリーに渡すパラメーター
        Map<String, Object> parameterValues = new HashMap<>();
        parameterValues.put("genderParam", 1);

        return new JpaCursorItemReaderBuilder<Employee>() // Builder生成
            .entityManagerFactory(entityManagerFactory)
            .name("jpaCursorItemReader") // 名前のセット
            .queryProvider(queryProvider)
            .parameterValues(parameterValues) // パラメーター
            .build(); // readerの生成
    }

    /** JpaCursorItemReaderを使用するStepの生成 */
    @Bean
    public Step exportJpaCursorStep() throws Exception {
        return new StepBuilder("ExportJpaCursorStep", jobRepository)
            .<Employee, Employee>chunk(10, transactionManager)
            .reader(jpaCursorReader()).listener(readListener)
            .processor(this.genderConvertProcessor)
            .writer(csvWriter()).listener(writeListener)
            .build();
    }

    /** JpaCursorItemReaderを使用するJobの生成 */
//    @Bean("JpaCursorJob")
    public Job exportJpaCursorJob() throws Exception {
        return new JobBuilder("ExportJpaCursorJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(exportJpaCursorStep())
            .build();
    }
}
