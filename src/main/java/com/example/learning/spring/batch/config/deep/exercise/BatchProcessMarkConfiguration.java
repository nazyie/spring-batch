package com.example.learning.spring.batch.config.deep.exercise;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;


/**
 * Batch class to process the mark from student at ABC
 * ----------------------------------------------------
 * 1. Step to accumulate markA and markB
 * 2. Step to calculate
 * Decide the RESULT using CustomFlow
 * If result it PASS
 * 3. Print the congratulation letter
 * 4. Inform the parent about the fail
 */
@Configuration
public class BatchProcessMarkConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 1. Step to accumulate markA and markB
     * =====================================
     */
    public Tasklet taskToAccumulateTheMark() {
        return (stepContribution, chunkContext) -> {
            int markA = Integer.parseInt(chunkContext.getStepContext().getJobParameters().get("markA").toString());
            int markB = Integer.parseInt(chunkContext.getStepContext().getJobParameters().get("markB").toString());
            System.out.printf("Accumulating mark A -> %d  | mark B -> %d%n", markA, markB);
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepToAccumulateTheMark() {
        return stepBuilderFactory.get("stepToAccumulateTheMark")
                .tasklet(taskToAccumulateTheMark())
                .build();
    }

    /**
     * 2. Step to calculate both MarkA and B
     * =====================================
     */
    public Tasklet taskToCalculateTheMark() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Calculate both markA and markB");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepToCalculateTheMark() {
        return stepBuilderFactory.get("stepToCalculateTheMark")
                .tasklet(taskToCalculateTheMark())
                .build();
    }

    /**
     * 3. Step to print to print congratulation letter
     * =====================================
     */
    public Tasklet taskToPrintCongratulationLetter() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Congratulation you pass");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepToPrintCongratulationLetter() {
        return stepBuilderFactory.get("stepToPrintCongratulationLetter")
                .tasklet(taskToPrintCongratulationLetter())
                .build();
    }

    /**
     * CUSTOM STATUS
     * =====================================
     */
    @Bean
    public JobExecutionDecider jobExecutionDecider() {
        // can create the class implementing this interface class
        return new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                int markA = Integer.parseInt(Objects.requireNonNull(jobExecution.getJobParameters().getString("markA")));
                int markB = Integer.parseInt(Objects.requireNonNull(jobExecution.getJobParameters().getString("markB")));
                String result = (markA + markB) < 50 ? "FAILED" : "SUCCESS";
                return new FlowExecutionStatus(result);
            }
        };
    }


    /**
     * 4. Step to inform parent about the fail
     * =====================================
     */
    public Tasklet taskToInformParent() {
        return (stepContribution, chunkContext) -> {
            System.out.println("Inform the parent about the student result");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepToInformParent() {
        return stepBuilderFactory.get("stepToInformTheParent")
                .tasklet(taskToInformParent())
                .build();
    }

    // Job to process the mark
    @Bean
    Job jobProcessMark() {
        return jobBuilderFactory.get("jobProcessMark")
                .start(stepToAccumulateTheMark())
                .next(stepToCalculateTheMark())
                .next(jobExecutionDecider())
                    .on("SUCCESS").to(stepToPrintCongratulationLetter())
                .from(jobExecutionDecider())
                    .on("FAILED").to(stepToInformParent())
                .end()
                .build();
    }
}
