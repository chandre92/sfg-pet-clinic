package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.Specialty;
import guru.springframework.sfgpetclinic.services.SpecialtiesService;
import org.springframework.stereotype.Service;

@Service
public class SpecialtiesServiceMap extends AbstractMapService<Specialty> implements SpecialtiesService {
}
