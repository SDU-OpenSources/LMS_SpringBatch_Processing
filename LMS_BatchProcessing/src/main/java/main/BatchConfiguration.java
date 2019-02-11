package main;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	@Autowired
	private JobBuilderFactory jobs;

	@Autowired
	private StepBuilderFactory steps;

	@Value("org/springframework/batch/core/schema-drop-oracle10g.sql")
	private Resource dropReopsitoryTables;

	@Value("org/springframework/batch/core/schema-oracle10g.sql")
	private Resource dataReopsitorySchema; 

	@Bean
	public ItemReader<HostSystemStatusPojo> reader() {
		// custom item reader (dummy), using an iterator within an internal
		// list
		HostSystemStatusItemReader reader = new HostSystemStatusItemReader();
		List<HostSystemStatusPojo> pojos = new ArrayList<HostSystemStatusPojo>();
		pojos.add(new HostSystemStatusPojo("BATCH", "EOD STARTED", "111", "SWEEPS"));
		reader.setPojos(pojos);
		reader.setIterator(reader.getPojos().iterator());
		return reader;
	}

	@Bean
	public ItemProcessor<HostSystemStatusPojo, HostSystemStatusPojo> processor() {
		return new HostSystemStatusItemProcessor();
	}

	@Bean
	public ItemWriter<HostSystemStatusPojo> writer(DataSource dataSource) {
		JdbcBatchItemWriter<HostSystemStatusPojo> writer = new JdbcBatchItemWriter<HostSystemStatusPojo>();
		writer.setSql(
				"UPDATE OLM_HOSTSYSTEMSTATUSES SET FLG_GLSTATUS = :flgGlStatus, FLG_GLSUBSTATUS = :flgGlSubStatus WHERE COD_GL = :codGl AND COD_MODULE = :codModule");
		writer.setDataSource(dataSource);
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<HostSystemStatusPojo>());
		return writer;
	}

	@Bean(name = "lmsBatchJob")
	public Job job(@Qualifier("hostEODStart") Step hostEODStart, @Qualifier("hostEODEnd") Step hostEODEnd) {
		return jobs.get("lmsBatchJob").start(hostEODStart).next(hostEODEnd).build();
	}

	@Bean
	protected Step hostEODStart(ItemReader<HostSystemStatusPojo> reader, ItemProcessor<HostSystemStatusPojo, HostSystemStatusPojo> processor,
			ItemWriter<HostSystemStatusPojo> writer) {
		return steps.get("hostEODStart").<HostSystemStatusPojo, HostSystemStatusPojo>chunk(10).reader(reader).processor(processor)
				.writer(writer).build();
	}
	
	@Bean
	protected Step hostEODEnd(ItemReader<HostSystemStatusPojo> reader, ItemProcessor<HostSystemStatusPojo, HostSystemStatusPojo> processor,
			ItemWriter<HostSystemStatusPojo> writer) {
		return steps.get("hostEODEnd").<HostSystemStatusPojo, HostSystemStatusPojo>chunk(10).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	@Bean
	public DataSource dataSource() throws SQLException {
		// oracle data source
		final DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
		dataSource.setUrl("jdbc:oracle:thin:@10.10.9.53:1521:SIR14894");
		dataSource.setUsername("JPMC_DEV_LMS_05");
		dataSource.setPassword("JPMC_DEV_LMS_05");
		return dataSource;
	}

	@Bean
	public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws MalformedURLException {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();

		databasePopulator.addScript(dropReopsitoryTables);
		databasePopulator.addScript(dataReopsitorySchema);
		databasePopulator.setIgnoreFailedDrops(true);

		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator);

		return initializer;
	}

	private JobRepository getJobRepository() throws Exception {
		JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
		factory.setDataSource(dataSource());
		factory.setTransactionManager(getTransactionManager());
		// JobRepositoryFactoryBean's methods Throws Generic Exception,
		// it would have been better to have a specific one
		factory.afterPropertiesSet();
		return (JobRepository) factory.getObject();
	}

	private PlatformTransactionManager getTransactionManager() {
		return new ResourcelessTransactionManager();
	}

	public JobLauncher getJobLauncher() throws Exception {
		SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
		// SimpleJobLauncher's methods Throws Generic Exception,
		// it would have been better to have a specific one
		jobLauncher.setJobRepository(getJobRepository());
		jobLauncher.afterPropertiesSet();
		return jobLauncher;
	}
}