package com.bls.patronage;

import com.bls.patronage.model.IdentifiableEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTest {

    private String testUUID;

    @Before
    public void setup() {
        testUUID = "12345678-9012-3456-7890-123456789012";
    }

    @Test
    public void creatingIdentifiableEntity() {
        IdentifiableEntity identifiableEntity = new IdentifiableEntity(
                UUID.fromString(testUUID)
        ) {
        };
        assertThat(
                identifiableEntity.getId()
        ).isEqualToComparingFieldByField(
                UUID.fromString(testUUID)
        );
    }

    @Test
    public void creatingIdentifiableEntityWithoutParameters() {
        IdentifiableEntity identifiableEntity = new IdentifiableEntity() {
        };

        assertThat(identifiableEntity.getId()).isNull();
    }
}
