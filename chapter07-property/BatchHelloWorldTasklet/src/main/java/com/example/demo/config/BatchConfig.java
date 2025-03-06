package com.example.demo.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.validator.OptionalValidator;
import com.example.demo.validator.RequiredValidator;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;


    /** HelloTasklet */
    @Autowired
    @Qualifier("HelloTasklet")
    private Tasklet helloTasklet;
    
    /** HelloTasklet2 */
    @Autowired
    @Qualifier("HelloTasklet2")
    private Tasklet helloTasklet2;

    /** 必須入力チェックのValidator */
    @Bean
    public JobParametersValidator defaultValidator() {
        DefaultJobParametersValidator validator = new DefaultJobParametersValidator();
        // 必須入力
        String[] requiredKeys = new String[] {"run.id", "require1"};
        validator.setRequiredKeys(requiredKeys);
        // オプション入力
        String[] optionalKeys = new String[] {"option1"};
        validator.setOptionalKeys(optionalKeys);
        // 必須キーとオプションキーの間に重複がないことを確認
        validator.afterPropertiesSet();

        return validator;
    }
    
    /** 複数チェックのValidator */
    @Bean
    public JobParametersValidator compositeValidator() {
        // ValidatorのList生成
        List<JobParametersValidator> validators = new ArrayList<>();
        validators.add(defaultValidator());
        validators.add(new RequiredValidator());
        validators.add(new OptionalValidator());

        // CompositにValidatorを入れる
        CompositeJobParametersValidator compositValidator = 
                new CompositeJobParametersValidator();
        compositValidator.setValidators(validators);

        return compositValidator;
    }
    
    /** TaskletのStepを生成 */
    @Bean
    public Step taskletStep1() {
        return new StepBuilder("HelloTaskletStep1", jobRepository) // Builderの取得
            .tasklet(helloTasklet, transactionManager) // Taskletのセット
            .build(); // Stepの生成
    }
    
    /** TaskletのStepを生成 */
    @Bean
    public Step taskletStep2() {
    	return new StepBuilder("HelloTaskletStep2", jobRepository) // Builderの取得
    			.tasklet(helloTasklet2, transactionManager) // Taskletのセット
    			.build(); // Stepの生成
    }

    /** Jobを生成 */
    @Bean
    public Job taskletJob() throws Exception {
        return new JobBuilder("HelloWorldTaskletJob", jobRepository) // Builderの取得
            .incrementer(new RunIdIncrementer()) // IDのインクリメント
            .start(taskletStep1()) // 最初のStep
            .next(taskletStep2()) // 次のStep
            .validator(compositeValidator()) // validator
            .build(); // Jobの生成
    }
}
