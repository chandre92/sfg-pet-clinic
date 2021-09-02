package guru.springframework.sfgpetclinic.controller;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    @Mock
    OwnerService ownerServiceMock;

    @InjectMocks
    OwnerController controller;

    Set<Owner> ownerSet;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ownerSet = Set.of(
                Owner.builder().id(1L).build(),
                Owner.builder().id(2L).build()
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/", "/index", "/index.html"})
    void listOwners(String path) throws Exception {
        // Arrange
        when(ownerServiceMock.findAll()).thenReturn(ownerSet);

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners" + path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("owner/index"))
                .andExpect(MockMvcResultMatchers.model().attribute("owners", ownerSet));

        verify(ownerServiceMock).findAll();
        verifyNoMoreInteractions(ownerServiceMock);
    }

    @Test
    void findOwners() throws Exception {
        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/find"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("notimplemented"));
    }

    @Test
    void showOwners() throws Exception {
        // Arrange
        Long ownerId = 123L;
        Owner owner = Owner.builder().id(ownerId).build();

        when(ownerServiceMock.findById(ownerId)).thenReturn(owner);

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + ownerId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("owner/ownerDetails"))
                .andExpect(MockMvcResultMatchers.model().attribute("owner", owner));
        verify(ownerServiceMock).findById(ownerId);
        verifyNoMoreInteractions(ownerServiceMock);
    }
}