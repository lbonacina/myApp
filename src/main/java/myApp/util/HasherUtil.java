package myApp.util;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

/**
 * Created with IntelliJ IDEA.
 * User: Luigi
 * Date: 15/02/13
 * Time: 19.18
 */
public class HasherUtil {

    public static void main(String[] args) {

        String plainTextPassword = args[0];

        // basic way to produce hash password + salt and store them in two separate columns
        //We'll use a Random Number Generator to generate salts.  This
        //is much more secure than using a username as a salt or not
        //having a salt at all.  Shiro makes this easy.
        //Note that a normal app would reference an attribute rather
        //than create a new RNG every time:
        //RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        //Object salt = rng.nextBytes();
        //RandomNumberGenerator generator = new SecureRandomNumberGenerator();
        //ByteSource nextBytes = generator.nextBytes();
        //Now hash the plain-text password with the random salt and multiple
        //iterations and then Base64-encode the value (requires less space than Hex):
        //String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, nextBytes.toBase64(), 1024).toBase64();
        //System.out.println("Plain Test Password : " + plainTextPassword + ", hashedPasswordBase64 : " + hashedPasswordBase64 + ", salt : " + nextBytes.toBase64());

        // insteam we use the provided PasswordService provided by Shiro that user SHA256 iterated 500.000 times with salt and then
        // produce a single string with algorithm+salt+hash
        // in the realm we use the provided PasswordMatcher
        // jpaRealm.setCredentialsMatcher(new PasswordMatcher());
        PasswordService svc = new DefaultPasswordService();
        String encrypted = svc.encryptPassword(plainTextPassword);
        System.out.println("Plain Test Password : " + plainTextPassword + ", hashedPasswordBase64 : " + encrypted );
    }
}
