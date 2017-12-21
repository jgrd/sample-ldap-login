package eu.securityproject.beans;

import java.util.Properties;

/**
 * Created by Letizia Vitari on 19/12/17.
 */
public class LdapConnection {
	
	private Integer sslMode;
	private String host;
	private String port;
	
	
	public LdapConnection(Properties prop) {
		super();
		this.sslMode = Integer.parseInt(prop.getProperty("ldap.ssl").trim());
		this.host = prop.getProperty("ldap.host");
		this.port = prop.getProperty("ldap.port");
	}
	public Integer getSslMode() {
		return sslMode;
	}
	public void setSslMode(Integer sslMode) {
		this.sslMode = sslMode;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	
	public String getProviderUrl() {
		return "ldap://" + this.getHost() + ":" + this.getPort();
	}
	
	
}
