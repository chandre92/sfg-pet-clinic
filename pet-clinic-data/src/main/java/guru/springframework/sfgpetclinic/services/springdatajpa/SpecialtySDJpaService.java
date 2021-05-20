package guru.springframework.sfgpetclinic.services.springdatajpa;

import guru.springframework.sfgpetclinic.model.Specialty;
import guru.springframework.sfgpetclinic.repositories.SpecialtiesRepository;
import guru.springframework.sfgpetclinic.services.SpecialtyService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Profile("springdatajpa")
public class SpecialtySDJpaService implements SpecialtyService {
    private final SpecialtiesRepository specialtiesRepository;

    public SpecialtySDJpaService(SpecialtiesRepository specialtiesRepository) {
        this.specialtiesRepository = specialtiesRepository;
    }

    @Override
    public Set<Specialty> findAll() {
        Set<Specialty> specialties = new HashSet<>();

        specialtiesRepository.findAll().forEach(specialties::add);

        return specialties;
    }

    @Override
    public Specialty findById(Long aLong) {
        return specialtiesRepository.findById(aLong).orElse(null);
    }

    @Override
    public Specialty save(Specialty object) {
        return specialtiesRepository.save(object);
    }

    @Override
    public void delete(Specialty object) {
        specialtiesRepository.delete(object);
    }

    @Override
    public void deleteById(Long aLong) {
        specialtiesRepository.deleteById(aLong);
    }
}
