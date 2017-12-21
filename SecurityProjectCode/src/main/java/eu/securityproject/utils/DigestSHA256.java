package eu.securityproject.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestSHA256 {

	private DigestSHA256(){
	}

	public static byte[] getSHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md=MessageDigest.getInstance( "SHA-256" );
		md.update(text.toString().getBytes("iso-8859-1"));
		return md.digest();
	}
	
}
