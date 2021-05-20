package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import guru.springframework.sfgpetclinic.services.PetService;
import guru.springframework.sfgpetclinic.services.PetTypeService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Profile({"default", "map"})
public class OwnerServiceMap extends AbstractMapService<Owner> implements OwnerService {
    private final PetTypeService petTypeService;
    private final PetService petService;

    public OwnerServiceMap(PetTypeService petTypeService, PetService petService) {
        this.petTypeService = petTypeService;
        this.petService = petService;
    }

    @Override
    public Owner findByLastName(String lastName) {
        return this.findAll().stream()
                .filter(owner -> Objects.equals(lastName, owner.getLastName()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Owner save(Owner object) {
        if (object != null) {
            if (object.getPets() != null) {
                object.getPets().forEach(pet -> {
                    if (pet.getPetType() == null) {
                        throw new RuntimeException("Pet Type is required");
                    }

                    pet.setPetType(petTypeService.save(pet.getPetType()));
                    pet.setId(petService.save(pet).getId());
                });
            }
        }
        return super.save(object);
    }
}
