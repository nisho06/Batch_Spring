package com.example.batch_spring.configuration;

import com.example.batch_spring.entity.Person;
import com.example.batch_spring.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class CsvBatchConfig {

    private final PersonRepository personRepository;

    private final JobRepository jobRepository;

    private final PlatformTransactionManager transactionManager;

    // Create Item Reader
    @Bean
    public FlatFileItemReader<Person> personReader(){
        FlatFileItemReader<Person> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/peoples.csv"));
        itemReader.setName("Person Item Reader");
        itemReader.setLinesToSkip(1);
        // Read one line and represent it as a Person object.
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Person> lineMapper() {
        DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        // If true, the no.of tokens in the line should match the no.of tokens defined.
        // If false, the lines with fewer tokens will be replaced with empty null values for the empty token.
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("code","country","name","sport");

        // Convert the data(each lines) into Person objects.
        BeanWrapperFieldSetMapper<Person> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Person.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    // Item Processor
    @Bean
    public PersonProcessor personProcessor(){
        return new PersonProcessor();
    }

    // Item Writer
    @Bean
    public RepositoryItemWriter<Person> personWriter(){
        RepositoryItemWriter<Person> repositoryWriter = new RepositoryItemWriter<>();
        repositoryWriter.setRepository(personRepository);
        // save method is already available in jpa repository.
        repositoryWriter.setMethodName("save");
        return repositoryWriter;
    }

    @Bean
    public Step step() {
        // chunk - step will process the data in chunks of 10 rows. Having a small chunk would consume less memory and
        // transaction processing would be easier.
        // <Person, Person> - input and output types of the chunk.
        // transactionManager - handling transactions during the step execution.
        return new StepBuilder("step-1", jobRepository)
                .<Person, Person> chunk(10, transactionManager)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personWriter())
                .build();
    }

    @Bean
    public Job runJob(){
        return new JobBuilder("job-1", jobRepository).start(step()).build();
    }
}
