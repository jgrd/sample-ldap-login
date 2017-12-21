package eu.securityproject;

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.ServiceUnavailableException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import eu.securityproject.beans.LdapConnection;
import eu.securityproject.beans.User;
import eu.securityproject.exception.MySecureProjectException;
import eu.securityproject.utils.DigestSHA256;

/**
 * Created by Letizia Vitari on 19/12/17.
 */
public class LdapAccess  {

	private LdapAccess() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static User getLDAPUser(LdapConnection ldapConn, User user) throws MySecureProjectException, NamingException {
		
		DirContext dirContext = null;

		String dn = "uid=%LDAP_USERNAME%,ou=People,dc=myorg,dc=test";
		
		try {

			Hashtable env = new Hashtable();

			env.put(Context.PROVIDER_URL, ldapConn.getProviderUrl());
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			
			String securityPrincipal = dn.replace("%LDAP_USERNAME%", user.getUsername()); 
			env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
		    env.put(Context.SECURITY_CREDENTIALS, DigestSHA256.getSHA256(user.getPassword()));

			
			dirContext = new InitialDirContext(env);
			
			findAccountByUsername(dirContext, securityPrincipal, user);
			
			System.out.println("Welcome " + user.getName() + " " + user.getSurname() + "! ");
			
			dirContext.close();

			return user;

		} catch (NamingException ne) {

			if(ne instanceof AuthenticationException){
				throw new MySecureProjectException("LDAP connection error: Invalid credentials", ne); //Invalid Credentials
			}else if(ne instanceof ServiceUnavailableException){
				throw new MySecureProjectException("LDAP connection error: Service unavailable", ne); //
			}else if(ne instanceof CommunicationException){
				throw new MySecureProjectException("LDAP connection error: Connection refused", ne); //Connection refused
			}else{
				throw new MySecureProjectException("LDAP connection error: " + " " + ne.getMessage(), ne);
			}
				
		} catch (MySecureProjectException lce) {
			throw new MySecureProjectException(lce.getMessage(), lce);
		} catch (Exception e){
			throw new MySecureProjectException("LDAP Generic Error: Connection refused!", e);

		} finally{
			if(dirContext!=null) dirContext.close();
		}

	}


	private static void findAccountByUsername(DirContext dirContext,String securityPrincipal, User user) throws NamingException {

		String searchFilter = "(&(objectClass=person)(uid=" + user.getUsername() + "))";
		
		SearchControls sc = new SearchControls();
		sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
		
		NamingEnumeration<SearchResult> r = dirContext.search(securityPrincipal, searchFilter, sc);
		
		SearchResult sr = null;
		
		if(r.hasMoreElements()){
			sr = r.nextElement();
		}

		//valori che servono: sn, cn e l
		user.setSurname((String)sr.getAttributes().get("sn").get());
		user.setName((String)sr.getAttributes().get("cn").get());
		user.setSecret((String)sr.getAttributes().get("l").get());

	}
	
	
}
