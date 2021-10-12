package guru.springframework.sfgpetclinic.controller;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.model.Pet;
import guru.springframework.sfgpetclinic.services.OwnerService;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.PetTypeService;
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
import org.springframework.web.bind.WebDataBinder;

import java.util.HashSet;

import static guru.springframework.sfgpetclinic.controller.PetController.PETS_CREATE_OR_UPDATE_FORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

    private static final Long OWNER_ID = 1234L;

    @Mock
    PetTypeService petTypeService;

    @Mock
    PetService petService;

    @Mock
    OwnerService ownerService;

    @InjectMocks
    PetController petController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
    }

    @Test
    public void populatePetTypes() {
        // Act
        petController.populatePetTypes();

        // Assert
        verify(petTypeService).findAll();
        verifyNoMoreInteractions(petTypeService);
        verifyNoInteractions(petService, ownerService);
    }

    @Test
    public void findOwner() {
        // Act
        petController.findOwner(OWNER_ID);

        // Assert
        verify(ownerService).findById(OWNER_ID);
        verifyNoMoreInteractions(ownerService);
        verifyNoInteractions(petService, petTypeService);
    }

    @Test
    public void initOwnerBinder() {
        // Arrange
        WebDataBinder webDataBinder = mock(WebDataBinder.class);

        // Act
        petController.initOwnerBinder(webDataBinder);

        // Assert
        verify(webDataBinder).setDisallowedFields("id");
        verifyNoMoreInteractions(webDataBinder);
        verifyNoInteractions(ownerService, petService, petTypeService);
    }

    @Test
    public void initCreationForm() throws Exception {
        // Arrange
        Owner owner = Owner.builder().id(OWNER_ID).build();
        when(ownerService.findById(OWNER_ID)).thenReturn(owner);

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + OWNER_ID + "/pets/new"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(PETS_CREATE_OR_UPDATE_FORM))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pet", "types", "owner"));
        verify(petTypeService).findAll();
        verify(ownerService).findById(OWNER_ID);
        verifyNoMoreInteractions(petTypeService, ownerService);
        verifyNoInteractions(petService);
    }

    @Test
    public void processCreationForm_withDuplicate() throws Exception {
        // Arrange
        String petName = "foobar";
        Pet existingPet = Pet.builder().id(123L).name(petName).build();
        Owner owner = Owner.builder().id(OWNER_ID).pet(existingPet).build();
        when(ownerService.findById(OWNER_ID)).thenReturn(owner);

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + OWNER_ID + "/pets/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("name", petName)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(PETS_CREATE_OR_UPDATE_FORM))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pet", "types", "owner"));

        verify(petTypeService).findAll();
        verify(ownerService).findById(OWNER_ID);
        verifyNoMoreInteractions(petTypeService, ownerService);
        verifyNoInteractions(petService);
    }

    @Test
    public void processCreationForm_success() throws Exception {
        // Arrange
        String petName = "foobar";

        // Lombok doesn't support builder with default empty mutable collections
        Owner owner = new Owner(OWNER_ID, null, null, null, null, null, new HashSet<>());
        when(ownerService.findById(OWNER_ID)).thenReturn(owner);
        Pet expectedPet = Pet.builder().name(petName).owner(owner).build();

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + OWNER_ID + "/pets/new")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", "")
                        .param("name", petName)
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + OWNER_ID))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pet", "types", "owner"));

        assertThat(owner.getPets()).hasSize(1);
        verify(petTypeService).findAll();
        verify(ownerService).findById(OWNER_ID);
        verify(petService).save(expectedPet);
        verifyNoMoreInteractions(petTypeService, ownerService);
    }

    @Test
    public void initUpdateForm() throws Exception {
        // Arrange
        Long petId = 123L;
        when(petService.findById(petId)).thenReturn(Pet.builder().id(petId).build());

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/owners/" + OWNER_ID + "/pets/" + petId + "/edit"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name(PETS_CREATE_OR_UPDATE_FORM))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pet"));

        verify(petTypeService).findAll();
        verify(ownerService).findById(OWNER_ID);
        verify(petService).findById(petId);
        verifyNoMoreInteractions(petTypeService, ownerService, petService);
    }

    @Test
    public void processUpdateForm() throws Exception {
        // Arrange
        Long petId = 123L;
        String petName = "foobar";
        Owner owner = Owner.builder().id(OWNER_ID).build();
        when(ownerService.findById(OWNER_ID)).thenReturn(owner);
        Pet expectedPet = Pet.builder().id(petId).name(petName).owner(owner).build();

        // Act && Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/owners/" + OWNER_ID + "/pets/" + petId + "/edit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("id", petId.toString())
                        .param("name", petName)
                )
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/owners/" + OWNER_ID))
                .andExpect(MockMvcResultMatchers.model().attributeExists("pet", "types", "owner"));

        verify(petTypeService).findAll();
        verify(ownerService).findById(OWNER_ID);
        verify(petService).save(expectedPet);
        verifyNoMoreInteractions(petTypeService, ownerService);
    }
}