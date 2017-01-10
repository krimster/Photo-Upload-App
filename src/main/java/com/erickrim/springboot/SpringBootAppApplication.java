package com.erickrim.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.endpoint.MetricReaderPublicMetrics;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.repository.InMemoryMetricRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Collection;

@SpringBootApplication
public class SpringBootAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringBootAppApplication.class, args);
	}

	// this is the type of metricRepository that let's increment by delta value
	@Bean
	InMemoryMetricRepository inMemoryMetricRepository() {
		return new InMemoryMetricRepository();
	}

	// but inMemoryMetricRepositories are not automatically included /metrics page
	// for that we have to create another Bean

	@Bean
	PublicMetrics publicMetrics(InMemoryMetricRepository repository) {
		return new MetricReaderPublicMetrics(repository);
	}

}
