package com.example.demo.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.demo.listener.TaskletStepListener;

@Configuration
public class BatchConfig {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    @Qualifier("FirstTasklet")
    private Tasklet firstTasklet;

    @Autowired
    @Qualifier("SuccessTasklet")
    private Tasklet successTasklet;

    @Autowired
    @Qualifier("FailTasklet")
    private Tasklet failTasklet;

    @Autowired
    @Qualifier("TaskletStepListener")
    private TaskletStepListener taskletStepListener;

    @Autowired
    @Qualifier("RandomTasklet")
    private Tasklet randomTasklet;

    @Autowired
    private JobExecutionDecider sampleDecider;

    /** FirstStepを生成 */
    @Bean
    public Step firstStep() {
        return new StepBuilder("FirstStep", jobRepository)
            .tasklet(firstTasklet, transactionManager)
            .listener(taskletStepListener)
            .build();
    }

    /** SuccessStepを生成 */
    @Bean
    public Step suceessStep() {
        return new StepBuilder("SuceessStep", jobRepository)
            .tasklet(successTasklet, transactionManager)
            .build();
    }

    /** FailStepを生成 */
    @Bean
    public Step failStep() {
        return new StepBuilder("FailStep", jobRepository)
            .tasklet(failTasklet, transactionManager)
            .build();
    }

    /** RandomStepを生成 */
    @Bean
    public Step randomStep() {
        return new StepBuilder("RandomStep", jobRepository)
            .tasklet(randomTasklet, transactionManager)
            .listener(taskletStepListener)
            .build();
    }

    /** Taskletの分岐Jobを生成 */
    @Bean
    public Job taskletBranchJob() throws Exception {
        return new JobBuilder("TaskletBranchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(firstStep()) // 最初のStepをセット
                .on(ExitStatus.COMPLETED.getExitCode()) // COMPLETEDの場合
                .to(suceessStep()) // Step2へ
            .from(firstStep()) // Step1へ戻る
                .on(ExitStatus.FAILED.getExitCode()) // FAILEDの場合
                .to(failStep()) // Step3へ
                .end() // 分岐終了
            .build(); // Jobの生成
    }

    /** RandomTaskletの分岐Jobを生成 */
    @Bean
    public Job randomTaskletBranchJob() throws Exception {
        return new JobBuilder("RandomTaskletBranchJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(randomStep()) // 最初のStep
            .next(sampleDecider) // Deciderへ
            .from(sampleDecider) // Deciderに戻る
                .on(FlowExecutionStatus.COMPLETED.getName())
                .to(suceessStep())
            .from(sampleDecider) // Deciderに戻る
                .on(FlowExecutionStatus.FAILED.getName())
                .to(failStep())
                .end() // 分岐終了
            .build(); // Jobの生成
    }
}
