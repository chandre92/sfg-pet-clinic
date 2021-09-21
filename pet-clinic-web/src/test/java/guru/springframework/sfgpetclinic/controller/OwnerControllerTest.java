package guru.springframework.sfgpetclinic.controller;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {
    private static final String OWNERS_LAST_NAME = "foobar";
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

    @Test
    void findOwners() throws Exception {
        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/find"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("owner"))
                .andExpect(MockMvcResultMatchers.view().name("owner/findOwners"));

        verifyNoInteractions(ownerServiceMock);
    }

    @Test
    void processFindFormReturnEmpty() throws Exception {
        // Arrange
        when(ownerServiceMock.findAllByLastName(OWNERS_LAST_NAME)).thenReturn(emptyList());

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("lastName", OWNERS_LAST_NAME)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("owner/findOwners"));

        verify(ownerServiceMock).findAllByLastName(OWNERS_LAST_NAME);
    }

    @Test
    void processFindFormReturnSingleResult() throws Exception {
        // Arrange
        Long ownerId = 123L;
        Owner owner = new Owner();
        owner.setLastName(OWNERS_LAST_NAME);
        owner.setId(ownerId);

        when(ownerServiceMock.findAllByLastName(OWNERS_LAST_NAME)).thenReturn(Collections.singletonList(owner));

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("lastName", OWNERS_LAST_NAME)
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + ownerId));

        verify(ownerServiceMock).findAllByLastName(OWNERS_LAST_NAME);
    }

    @Test
    void processFindFormReturnSeveralResults() throws Exception {
        // Arrange
        Long firstOwnerId = 123L;
        Owner firstOwner = new Owner();
        firstOwner.setLastName(OWNERS_LAST_NAME);
        firstOwner.setId(firstOwnerId);

        Long secondOwnerId = 789L;
        Owner secondOwner = new Owner();
        secondOwner.setLastName(OWNERS_LAST_NAME);
        secondOwner.setId(secondOwnerId);

        when(ownerServiceMock.findAllByLastName(OWNERS_LAST_NAME)).thenReturn(Arrays.asList(firstOwner, secondOwner));

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("lastName", OWNERS_LAST_NAME)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("owner/ownersList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("selections"));

        verify(ownerServiceMock).findAllByLastName(OWNERS_LAST_NAME);
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

    @Test
    void initCreateForm() throws Exception {
        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(OwnerController.CREATE_OR_UPDATE_OWNER_FORM))
                .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));
        verifyNoInteractions(ownerServiceMock);
    }

    @Test
    void processCreateForm() throws Exception {
        // Arrange
        Long ownerId = 1234L;

        when(ownerServiceMock.save(any())).thenReturn(Owner.builder().id(ownerId).build());

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/owners/new"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + ownerId))
                // will have the same Owner instance that we get on processCreateForm entry
                .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));

        verify(ownerServiceMock).save(any());
        verifyNoMoreInteractions(ownerServiceMock);
    }

    @Test
    void initUpdateForm() throws Exception {
        // Arrange
        Long ownerId = 123L;
        when(ownerServiceMock.findById(ownerId)).thenReturn(Owner.builder().id(ownerId).build());

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + ownerId + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(OwnerController.CREATE_OR_UPDATE_OWNER_FORM))
                .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));
        verify(ownerServiceMock).findById(ownerId);
        verifyNoMoreInteractions(ownerServiceMock);
    }

    @Test
    void processUpdateForm() throws Exception {
        // Arrange
        Long ownerId = 1234L;

        when(ownerServiceMock.save(any())).thenReturn(Owner.builder().id(ownerId).build());

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + ownerId + "/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + ownerId))
                // will have the same Owner instance that we get on processCreateForm entry
                .andExpect(MockMvcResultMatchers.model().attributeExists("owner"));

        verify(ownerServiceMock).save(any());
        verifyNoMoreInteractions(ownerServiceMock);
    }
}