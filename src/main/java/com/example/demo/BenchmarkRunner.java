package com.example.demo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.example.demo.repository.CustomerRepository;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G"})
public class BenchmarkRunner {
   
    @Param({"5"})
    private int N;

    @Autowired
    private CustomerRepository repository;
    private List<String> DATA_FOR_TESTING;

    private ConfigurableApplicationContext context;

    public static void main(String[] args) throws RunnerException {

        Options opt = new OptionsBuilder()
                .include(BenchmarkRunner.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        context = SpringApplication.run(DemoApplication.class);
        context.registerShutdownHook();
        repository = context.getBean(CustomerRepository.class);
         var teste = repository.findByFirstName("Alice");
         System.out.println("OI" + teste);
        DATA_FOR_TESTING = createData();
    }

    @TearDown
    public void closeContext(){
        context.close();
    }

    @Benchmark
    public void loopFor(Blackhole bh) {
        for (int i = 0; i < DATA_FOR_TESTING.size(); i++) {
            String s = DATA_FOR_TESTING.get(i); //take out n consume, fair with foreach
            bh.consume(s);
        }
    }

    @Benchmark
    public void loopWhile(Blackhole bh) {
        int i = 0;
        while (i < DATA_FOR_TESTING.size()) {
            String s = DATA_FOR_TESTING.get(i);
            bh.consume(s);
            i++;
        }
    }

    @Benchmark
    public void loopForEach(Blackhole bh) {
        for (String s : DATA_FOR_TESTING) {
            bh.consume(s);
        }
    }

    @Benchmark
    public void loopIterator(Blackhole bh) {
        Iterator<String> iterator = DATA_FOR_TESTING.iterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            bh.consume(s);
        }
    }

    private List<String> createData() {
        var teste = repository.findByFirstName("Alice");
        System.out.println("OI" + teste);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            data.add("Number : " + i);
        }
        return data;
    }
}
