package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.repositories.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerSDJpaServiceTest {

    @InjectMocks
    OwnerSDJpaService service;

    @Mock
    OwnerRepository ownerRepository;

    @Test
    void findAll() {
        // Arrange
        List<Owner> owners = Arrays.asList(
                Owner.builder().id(1L).build(),
                Owner.builder().id(2L).build()
        );
        when(ownerRepository.findAll()).thenReturn(owners);

        // Act
        Set<Owner> allOwners = service.findAll();

        // Assert
        assertThat(allOwners).containsAll(owners);
        verify(ownerRepository).findAll();
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void findById() {
        // Arrange
        Long id = 1L;
        Owner owner = Owner.builder().id(id).build();
        when(ownerRepository.findById(id)).thenReturn(Optional.of(owner));

        // Act
        Owner byId = service.findById(id);

        // Assert
        assertThat(byId).isEqualTo(owner);
        verify(ownerRepository).findById(id);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void findByIdNotFound() {
        // Arrange
        Long id = 1L;
        when(ownerRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Owner byId = service.findById(id);

        // Assert
        assertThat(byId).isNull();
        verify(ownerRepository).findById(id);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void save() {
        // Arrange
        Owner owner = Owner.builder().build();
        when(ownerRepository.save(owner)).thenReturn(owner);

        // Act
        Owner save = service.save(owner);

        // Assert
        assertThat(save).isEqualTo(owner);
        verify(ownerRepository).save(owner);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void delete() {
        // Arrange
        Owner owner = Owner.builder().build();

        // Act
        service.delete(owner);

        // Assert
        verify(ownerRepository).delete(owner);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void deleteById() {
        // Arrange
        Long id = 1L;

        // Act
        service.deleteById(id);

        // Assert
        verify(ownerRepository).deleteById(id);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    void findByLastName() {
        // Arrange
        String lastName = "Smith";
        Owner owner = Owner.builder().lastName(lastName).build();

        when(ownerRepository.findByLastName(lastName)).thenReturn(owner);

        // Act
        Owner byLastName = service.findByLastName(lastName);

        // Assert
        assertThat(byLastName).isEqualTo(owner);
        verify(ownerRepository).findByLastName(lastName);
        verifyNoMoreInteractions(ownerRepository);
    }
}