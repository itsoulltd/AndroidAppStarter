package lab.infoworks.libshared;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import lab.infoworks.libshared.util.crypto.definition.Cryptor;

@RunWith(AndroidJUnit4ClassRunner.class)
public class CryptorTest {

    @Test
    public void testCryptor(){
        String secret = "my-country-man";

        Cryptor cryptor = Cryptor.create();
        String plainText = "How are you!";

        String encrypt = cryptor.encrypt(secret, plainText);

        String decrypted = cryptor.decrypt(secret, encrypt);

        Assert.assertEquals(plainText, decrypted);
    }

}