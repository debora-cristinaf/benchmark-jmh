package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.example.demo.repository")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class DemoApplication implements CommandLineRunner {

	@Autowired
	private CustomerRepository repository;

	private List<String> DATA_FOR_TESTING;

	@Param({"5"})
	private int N;


	@Setup
	public void setup() {
		DATA_FOR_TESTING = createData();
	}

	private List<String> createData() {
		List<String> data = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			data.add("Number : " + i);
		}
		return data;
	}


	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(DemoApplication.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();

		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		repository.deleteAll();

		// save a couple of customers
		repository.save(new Customer("Alice", "Smith"));
		repository.save(new Customer("Bob", "Smith"));

		// fetch all customers
		System.out.println("Customers found with findAll():");
		System.out.println("-------------------------------");
		for (Customer customer : repository.findAll()) {
			System.out.println(customer);
		}
		System.out.println();

		// fetch an individual customer
		System.out.println("Customer found with findByFirstName('Alice'):");
		System.out.println("--------------------------------");
		System.out.println(repository.findByFirstName("Alice"));

		System.out.println("Customers found with findByLastName('Smith'):");
		System.out.println("--------------------------------");
		for (Customer customer : repository.findByLastName("Smith")) {
			System.out.println(customer);
		}

	}

	@Benchmark
	public void loopFor(Blackhole bh) {
		repository.findByFirstName("Alice");
		for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
			String s = DATA_FOR_TESTING.get(i); //take out n consume, fair with foreach
			bh.consume(s);
		}
	}

}
