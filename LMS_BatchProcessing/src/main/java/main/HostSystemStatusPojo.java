package main;

public class HostSystemStatusPojo {

	private String flgGlStatus;
	private String flgGlSubStatus;
	private String codGl;
	private String codModule;

	public HostSystemStatusPojo() {

	}

	public HostSystemStatusPojo(String flgGlStatus, String flgGlSubStatus, String codGl, String codModule) {
		this.flgGlStatus = flgGlStatus;
		this.flgGlSubStatus = flgGlSubStatus;
		this.codGl = codGl;
		this.codModule = codModule;
	}

	@Override
	public String toString() {
		return flgGlStatus + "," + flgGlSubStatus + "," + codGl + "," + codModule;
	}

	public String getFlgGlStatus() {
		return flgGlStatus;
	}

	public void setFlgGlStatus(String flgGlStatus) {
		this.flgGlStatus = flgGlStatus;
	}

	public String getFlgGlSubStatus() {
		return flgGlSubStatus;
	}

	public void setFlgGlSubStatus(String flgGlSubStatus) {
		this.flgGlSubStatus = flgGlSubStatus;
	}

	public String getCodGl() {
		return codGl;
	}

	public void setCodGl(String codGl) {
		this.codGl = codGl;
	}

	public String getCodModule() {
		return codModule;
	}

	public void setCodModule(String codModule) {
		this.codModule = codModule;
	}

}
