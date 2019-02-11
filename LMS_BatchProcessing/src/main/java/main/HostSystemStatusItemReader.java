package main;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class HostSystemStatusItemReader implements ItemReader<HostSystemStatusPojo> {

	private List<HostSystemStatusPojo> pojos;

	private Iterator<HostSystemStatusPojo> iterator;

	public HostSystemStatusPojo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (getIterator().hasNext()) {
			return getIterator().next();
		}
		return null;
	}

	public List<HostSystemStatusPojo> getPojos() {
		return pojos;
	}

	public void setPojos(List<HostSystemStatusPojo> pojos) {
		this.pojos = pojos;
	}

	public Iterator<HostSystemStatusPojo> getIterator() {
		return iterator;
	}

	public void setIterator(Iterator<HostSystemStatusPojo> iterator) {
		this.iterator = iterator;
	}
}