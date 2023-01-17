package com.example.demo;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.openjdk.jmh.runner.RunnerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    @Autowired    private CustomerRepository repository;
    private static int numberOfData = 100;

    public static void main(String[] args) throws RunnerException {
        SpringApplication.run(DemoApplication.class, args);    }
    @Override
    public void run(String... args) throws Exception {        
        for(int i=0; i < numberOfData; i++) {            repository.save(new Customer("Alice", "Smith"));
        }
    }
}