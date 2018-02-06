package com.medibox.auto.mapping;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;




@SpringBootApplication
@EnableAsync
public class SpringApp {

	
    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Threadpool");
        executor.initialize();
        return executor;
    }

	public static void main(String[] args) {
		
		
    	new SpringApplicationBuilder(SpringApp.class).registerShutdownHook(true).run(args);    
	}

}
