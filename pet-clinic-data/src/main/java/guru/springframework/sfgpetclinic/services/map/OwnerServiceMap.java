package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.Owner;
import guru.springframework.sfgpetclinic.services.OwnerService;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OwnerServiceMap extends AbstractMapService<Owner> implements OwnerService {
    @Override
    public Owner findByLastName(String lastName) {
        return this.findAll().stream()
                .filter(owner -> Objects.equals(lastName, owner.getLastName()))
                .findFirst()
                .orElse(null);
    }
}
