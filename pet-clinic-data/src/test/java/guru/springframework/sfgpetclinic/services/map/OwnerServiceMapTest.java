package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({MockitoExtension.class})
class OwnerServiceMapTest {
    private static final Long FIRST_OWNER_ID = 1L;

    @InjectMocks
    OwnerServiceMap ownerServiceMap;

    @BeforeEach
    void setUp() {
        ownerServiceMap.save(Owner.builder()
                .id(FIRST_OWNER_ID)
                .build());
        ownerServiceMap.save(Owner.builder()
                .id(2L)
                .build());
    }

    @AfterEach
    void tearDown() {
        ownerServiceMap.findAll().forEach(ownerServiceMap::delete);
    }

    @Test
    void findAll() {
        // Act
        Set<Owner> owners = ownerServiceMap.findAll();

        // Assert
        assertThat(owners).hasSize(2);
    }

    @Test
    void findById() {
        // Act
        Owner owner = ownerServiceMap.findById(FIRST_OWNER_ID);

        // Assert
        assertThat(owner).isNotNull();
        assertThat(owner.getId()).isEqualTo(FIRST_OWNER_ID);
    }

    @Test
    void deleteById() {
        // Act
        ownerServiceMap.deleteById(FIRST_OWNER_ID);

        // Assert
        assertThat(ownerServiceMap.findById(FIRST_OWNER_ID)).isNull();
    }

    @Test
    void delete() {
        // Arrange
        Owner firstOwner = ownerServiceMap.findById(FIRST_OWNER_ID);

        // Act
        ownerServiceMap.delete(firstOwner);

        // Assert
        assertThat(ownerServiceMap.findById(firstOwner.getId())).isNull();
        assertThat(ownerServiceMap.findAll()).hasSize(1);
    }

    @Test
    void findByLastName() {
        // Arrange
        String lastName = "foobar";
        ownerServiceMap.save(Owner.builder().lastName(lastName).build());

        // Act
        Owner byLastNameOwner = ownerServiceMap.findByLastName(lastName);

        // Assert
        assertThat(byLastNameOwner).isNotNull();
        assertThat(byLastNameOwner.getLastName()).isEqualTo(lastName);
    }

    @Test
    void save() {
        // Act
        Owner owner = ownerServiceMap.save(Owner.builder().build());

        // Assert
        Owner searchResultOwner = ownerServiceMap.findById(owner.getId());
        assertThat(owner.getId()).isNotNull();
        assertThat(owner).isEqualTo(searchResultOwner);
    }

    @Test
    void saveExistingId() {
        // Arrange
        Long existingId = 3L;

        // Act
        Owner savedOwner = ownerServiceMap.save(Owner.builder().id(existingId).build());

        // Assert
        assertThat(savedOwner.getId()).isEqualTo(existingId);
    }
}