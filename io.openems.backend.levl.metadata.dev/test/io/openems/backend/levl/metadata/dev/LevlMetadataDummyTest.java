package io.openems.backend.levl.metadata.dev;

import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static org.assertj.core.api.Assertions.assertThat;

class LevlMetadataDummyTest {

    @Test
    void hashPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        LevlMetadataDummy dummy = new LevlMetadataDummy(null);

        assertThat(dummy.hashPassword("secret")).isEqualTo("cc66cf4dce72f1e7173733036c6c0e0b5ff877ba1eebf70acf93275bc50686d37c8cba2d84963587a8c4efbceb1b5edfb5eefa1405b495fd07c021e7deddbde3");
    }

}