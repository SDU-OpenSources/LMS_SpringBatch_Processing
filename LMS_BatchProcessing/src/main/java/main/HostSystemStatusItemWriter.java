package main;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class HostSystemStatusItemWriter implements ItemWriter<HostSystemStatusPojo> {

	public void write(List<? extends HostSystemStatusPojo> pojo) throws Exception {
		System.out.println("writing Pojo " + pojo);
	}

}
