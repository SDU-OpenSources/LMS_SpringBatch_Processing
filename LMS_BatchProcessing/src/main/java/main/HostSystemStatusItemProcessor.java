package main;

import org.springframework.batch.item.ItemProcessor;

public class HostSystemStatusItemProcessor implements
		ItemProcessor<HostSystemStatusPojo, HostSystemStatusPojo> {

		public HostSystemStatusPojo process(final HostSystemStatusPojo pojo) throws Exception {
		final String flgGlStatus = encode(pojo.getFlgGlStatus());
		final String flgGlSubStatus = encode(pojo.getFlgGlSubStatus());
		final String codGl = encode(pojo.getCodGl());
		final String codModule = encode(pojo.getCodModule());

		final HostSystemStatusPojo encodedPojo = new HostSystemStatusPojo(flgGlStatus, flgGlSubStatus, codGl, codModule);

		return encodedPojo;

	}

	private String encode(String word) {
		/*
		 * You can process your inputs here
		StringBuffer str = new StringBuffer(word);
		return str.reverse().toString();*/
		return word;
	}
}