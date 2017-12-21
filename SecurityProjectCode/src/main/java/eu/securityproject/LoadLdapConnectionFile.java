package eu.securityproject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import eu.securityproject.beans.LdapConnection;
import eu.securityproject.exception.MySecureProjectException;

/**
 * Created by Letizia Vitari on 19/12/17.
 */
public class LoadLdapConnectionFile {


	public static LdapConnection loadConnectionPropertyFile() {

        final String fileName = "ldapconnection.properties";


         // A:
         //InputStream input = null;


        //B:
        FileInputStream input = null;

        try {

            
//          input = LoadLdapConnectionFile.class.getClassLoader().getResourceAsStream( fileName );

            input = new FileInputStream( fileName );
            if(input==null) throw new MySecureProjectException("Unable to find " + fileName);

            // Caricamento file di properties
            Properties prop = new Properties();
            prop.load(input);

            //popolamento org.myprogect.beans LdapConnection
            LdapConnection ldapConn = new LdapConnection(prop);

            return ldapConn;

        } catch (IOException ex) {
            throw new MySecureProjectException(ex.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    throw new MySecureProjectException(e.getMessage());
                }
            }
        }
    }


}
