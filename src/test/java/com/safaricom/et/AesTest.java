package com.safaricom.et;
import java.util.logging.Logger;
import com.bigdata.postgres.Aes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AesTest {
        Logger logger = Logger.getLogger(AesTest.class.getName());
    @Test
    void shouldShowSimpleAssertion() {
        Assertions.assertEquals("Invalids", Aes.encrypt("sss","254727128043", "Kenneth.Kipkogei","cuallingpartynumbersmssqldb"));
//        logger.info("Done 1st test");

//            Assertions.assertEquals("Tywy7Y272MmuDlrewpOV9A==a", Aes.encrypt("254727128043","Kenneth.Kipkogei","callingpartynumbersmssqldb"));
//
//        Assertions.assertEquals("254727128043", Aes.decrypt("Tywy7Y272MmuDlrewpOV9A==","Kenneth.Kipkogei","callingpartynumbersmssqldb"));
//
//        Assertions.assertEquals("254727128043", Aes.decrypt("Tywy7Y272MmuDlrewpOV9A==","postgress","callingpartynumbersmssqldb"));

    }
}
