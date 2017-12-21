package eu.securityproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.naming.NamingException;

import com.google.zxing.WriterException;

import eu.securityproject.beans.LdapConnection;
import eu.securityproject.beans.User;
import eu.securityproject.exception.MySecureProjectException;

/**
 * Created by Letizia Vitari on 19/12/17.
 */
public class Main {

    public static void main(String ... argv) {

        BufferedReader br = null;

        try {

            //1: Caricamento parametri di connessione LDAP
            LdapConnection ldapConn = LoadLdapConnectionFile.loadConnectionPropertyFile();


            //2: Richiesta parametri di autenticazione
            br = new BufferedReader(new InputStreamReader(System.in));

            Boolean isAValidField = Boolean.FALSE;
            String username = null;
            
            U: while(!isAValidField) {
                System.out.print("Enter Username : ");
                username = br.readLine();

                if (username == null || ("").equals(username.trim())) {
                    System.out.println("Username is not valid! Try again. ");
                    continue U;
                }

                isAValidField = Boolean.TRUE;
            }

            System.out.print("Enter Password : ");
            String password = br.readLine();

            //Popolamento bean User
            User user = new User(username, password);

            //3:Connessione LDAP e recupero dati necessari per generazione QRCode
            LdapAccess.getLDAPUser(ldapConn, user);


            //4: Generazione QRCode
            QRCodeManagement.createQRCodeImage(user);

            isAValidField = Boolean.FALSE;

            System.out.print("Your QRCode has been created. Enter a valid OTP: ");
            U: while(!isAValidField) {
                String otp = br.readLine();
                //5: Controllo validità codice OTP
                if (otp == null || ("").equals(otp.trim()) || !OTPManagement.isValidOtp(otp, user.getSecret())) {
                    System.out.print("OTP is not valid! Try again. OTP: ");
                    continue U;
                }

                isAValidField = Boolean.TRUE;
            }

            System.out.println("User correctly authenticated");

        } catch (MySecureProjectException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (WriterException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
        } catch (InvalidKeyException e) {
            System.out.println(e.getMessage());
        } catch (NamingException e) {
            System.out.println(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}
