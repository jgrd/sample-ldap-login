# sample-ldap-login
Security Specialist Assigment Test

__Istruzioni:__
1. Configurare il file ldapconnection.properties inserendo host e port del proprio LDAP Server e salvare.
2. Aprire il promt dei comandi e lanciare il comando *java -jar VitariSecurityProject.jar*.


__Soluzione adottata:__

La classe main è *Main.java* nel percorso *\eu\securityproject*.
La classe è stata implementata in modo che, come primo step, venga recuperato il file di configurazione per la connessione all'LDAP Server (*LoadLdapConnectionFile.java*).
Questo per evitare di inserire dati di autenticazione inutilmente nel caso di file inesistente. In questo caso il programma termina.
Diversamente, il programma procede chiedendo all'utente di inserire username e password che vengono salvati nel bean User (*User.java*).


A questo punto, si procede con l'autenticazione all'LDAP Server (*LdapAccess.java*) successivamente al popolamento dell'Hashtable con tutti gli attributi necessari: 

```java
Hashtable env = new Hashtable();
env.put(Context.PROVIDER_URL, "ldap://" + host + ":" + port);
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
env.put(Context.SECURITY_AUTHENTICATION, "simple");		
env.put(Context.SECURITY_PRINCIPAL, "cn=%LDAP_USERNAME%,dc=myorg,dc=test"); 
env.put(Context.SECURITY_CREDENTIALS, DigestSHA256.getSHA256(password));
```

- LDAP_USERNAME viene sostituito com lo username; 
- La password inserita da console viene hashata con algoritmo SHA256.
- La connessione è con SSL disabilitato.

Viene effettuata connessione al serve con autenticazione dell'utente. 
Se l'utenza non esiste, il programma termina con messaggio di credenziali errate.
Diversamente, vengono recuperate gli attributi dell'utente necessari per il proseguimento del programma: 
- *cn* e *sn* per il messaggio di benvenuto 
- *l* per la generazione del QRCode.


Per la generazione del QRCode (*QRCodeManagement.java*) è stata utilizzata la libreria com.google.zxing a partire dal QRCode testuale così composto:
```java
StringBuffer qrCodeText = new StringBuffer("otpauth://totp/").append(user.getUsername())
    .append("?secret=").append(secret)  //l
    .append("&issuer=").append("MySecurityProject")  //etichetta a piacere
    .append("&algorithm=").append("SHA256") 
    .append("&digits=").append(digit) //8
    .append("&period=").append(period) //20
    ;
```

Il QRCode viene salvato come immagine nella stessa directory del jar, nel formato .png e con nome uguale allo username dell'utente autenticato.
(Tramite app FreeOTP il QRCode viene scannerizzato per la genaerazione dell'OTP).

Il programma prosegue con la richiesta di inserimento dell'OTP e la sua validazione (*OTPManagement.java*).
Oltre a validare la lunghezza, viene controllato che l'OTP atteso è quello appena inserito dall'utente entro i limiti di validità di 20 secondi.
Se tutto è corretto il programma termina con messaggio di successo altrimenti prosegue con la ichiesta di un successiovo OTP.
