package com.example.learning.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.Date;

/**
 * -------------------------
 * JOB LAUNCHER -> JOB
 * -------------------------
 */
@SpringBootApplication
@EnableBatchProcessing
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Temporary run
	 */
	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Override
	public void run(String... args) throws Exception {

		// We can receive the job parameter and used in the logic
		JobParameters jobParameters = new JobParametersBuilder()
//				.addDate("date", new Date())
				.addLong("startAt", System.currentTimeMillis())
//				.addString("sampleKey", Arrays.stream(args).filter(val -> val.contains("--sampleKey")).toString())
				.toJobParameters();

		JobExecution execution = jobLauncher.run(job, jobParameters);
		System.out.println("STATUS :: " + execution.getStatus());
	}
}
