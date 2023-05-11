package com.example.learning.spring.batch.config.deep;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * Delivery use case
 * --------
 * Step Use Case
 * ---------
 * 1. Packaging item
 * 2. Drive to address
 * Address found ?
 * Yes -> 3. Give to customer
 * No -> 4. Back to warehouse
 *
 * 3. Give to customer
 * Is customer present ?
 * Present ? Give to customer
 * Not present ? Leave at Door
 */
@Configuration
public class BatchDeepConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    // --------------------
    // Delivery Package Job
    // --------------------
    @Bean
    public Job jobToDeliverThePackage() {
//        return this.jobBuilderFactory.get("jobToDeliverThePackage")
//                .start(stepToPackageTheItem())
//                .next(stepDriveToAddress())
//                .next(stepGiveToCustomer())
//                .build();


//        return this.jobBuilderFactory.get("jobToDeliverThePackage")
//                .start(stepToPackageTheItem())
//                .next(stepDriveToAddress())
//                        .on("FAILED").to(stepStorePackageBackToWarehouse())
//                    .from(stepDriveToAddress())
//                        .on("*").to(stepGiveToCustomer())
//                .end()
//                .build();

        return this.jobBuilderFactory.get("jobToDeliverThePackage")
                .start(stepToPackageTheItem())
                .next(stepDriveToAddress())
                    .on("FAILED").to(stepStorePackageBackToWarehouse())
                .from(stepDriveToAddress())
                    .on("*")
                .to(jobExecutionDecider())
                        .on("PRESENT").to(stepGiveToCustomer())
                    .from(jobExecutionDecider())
                            .on("NOT_PRESENT").to(stepLeaveAtDoor())
                .end()
                .build();
    }

    /**
     * ---------------------
     * 1. Packaging the item
     * ---------------------
     */
    public static Tasklet taskToPackageTheItem() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                String item = chunkContext.getStepContext().getJobParameters().get("item").toString();

                System.out.printf("The %s has been packaged!%n", item);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Step stepToPackageTheItem() {
        return stepBuilderFactory.get("stepToPackageTheItem")
                .tasklet(taskToPackageTheItem())
                .build();
    }

    /**
     * ---------------------
     * 2. Drive to address
     * ---------------------
     */
    // improvisation replacing the return new using lambda expression
    public static Tasklet taskDriveToAddress() {
        return (stepContribution, chunkContext) -> {
            boolean GOT_LOST = false;

            if (GOT_LOST) {
                throw new RuntimeException();
            }
            System.out.printf("Successfully drive to target address%n");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepDriveToAddress() {
        return this.stepBuilderFactory.get("stepDriveToAddress")
                .tasklet(taskDriveToAddress())
                .build();
    }

    /**
     * ---------------------
     * 3. Give to customer
     * ---------------------
     */
    public static Tasklet taskGiveToCustomer() {
        return (stepContribution, chunkContext) -> {
            System.out.printf("Give to customer the package %n");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepGiveToCustomer() {
        return this.stepBuilderFactory.get("stepGiveToCustomer")
                .tasklet(taskGiveToCustomer())
                .build();
    }

    /**
     * ---------------------
     * 3.1. Decider to give to the customer (Leave at door)
     * ---------------------
     */
    @Bean
    public JobExecutionDecider jobExecutionDecider() {
        return new DeliveryDeciderClass();
    }

    // custom status job execution
    public static class DeliveryDeciderClass implements JobExecutionDecider {

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            String result = LocalDateTime.now().getHour() < 12 ? "PRESENT" : "NOT_PRESENT";
            System.out.println("Decider result is: " + result);
            return new FlowExecutionStatus(result);
        }
    }

    public static Tasklet taskLeaveAtDoorstep() {
        return (stepContribution, chunkContext) -> {
            System.out.printf("Leave the package at doorstep%n");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepLeaveAtDoor() {
        return this.stepBuilderFactory.get("stepLeaveAtDoor")
                .tasklet(taskLeaveAtDoorstep())
                .build();
    }

    /**
     * ---------------------
     * 4. Store the package back to warehouse
     * ---------------------
     */
    public static Tasklet taskStorePackageBackToWarehouse() {
        return (stepContribution, chunkContext) -> {
            System.out.printf("Store back the package to the warehouse%n");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step stepStorePackageBackToWarehouse() {
        return this.stepBuilderFactory.get("stepStorePackageBackToWarehouse")
                .tasklet(taskStorePackageBackToWarehouse())
                .build();
    }

}
