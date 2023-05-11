package com.example.learning.spring.batch.config.createupdatedatabase;

import com.example.learning.spring.batch.entity.User;
import com.example.learning.spring.batch.repository.UserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;

/**
 * Configuration class for batch processing
 * --------------------------------------------
 * JOB -> STEP -> TASK
 * --------------------------------------------
 * ITEM READER -> ITEM PROCESSOR -> ITEM WRITER
 * ---------------------------------------------
 */
//@Configuration
public class CreateUpdateDataBatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;



    /**
     * -----------
     * ITEM READER
     * -----------
     */
    @Bean
    public JpaCursorItemReader<User> userJpaCursorItemReader() {
        JpaCursorItemReader<User> reader = new JpaCursorItemReader<>();
        reader.setEntityManagerFactory(entityManager.getEntityManagerFactory());
        reader.setQueryString("SELECT u FROM User u");
        reader.setParameterValues(Collections.emptyMap());
        return reader;
    }


    /**
     * ---------------
     * ITEM PROCESSOR
     * ---------------
     */
    @Bean
    public ItemProcessor<User, User> userItemProcessor() {
        return user -> {
            user.setStatus("UPDATED");
            return user;
        };
    }

    /**
     * ---------------
     * ITEM WRITER
     * ---------------
     */
    @Bean
    public RepositoryItemWriter<User> repositoryItemWriter() {
        RepositoryItemWriter<User> writer = new RepositoryItemWriter<>();
        writer.setRepository(userRepository);
        writer.setMethodName("save");
        return writer;
    }

    /**
     * *******
     * STEP
     * *******
     */
    @Bean
    public Step firstStep() {
        return stepBuilderFactory.get("firstStepForCreateUpdateDatabase")
                .<User, User>chunk(2)
                .reader(userJpaCursorItemReader())
                .processor(userItemProcessor())
                .writer(repositoryItemWriter())
                .build();
    }

    /**
     * *******
     * JOB
     * *******
     */
    @Bean
    public Job job() {
        return jobBuilderFactory.get("jobForCreateUpdateDatabase")
                .start(firstStep())
                .build();
    }
}
