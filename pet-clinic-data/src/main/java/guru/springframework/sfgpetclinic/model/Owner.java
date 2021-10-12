package guru.springframework.sfgpetclinic.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "owners")
public class Owner extends Person {
    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "telephone")
    private String telephone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner")
    private Set<Pet> pets = new HashSet<>();

    @Builder
    public Owner(Long id,
                 String firstName,
                 String lastName,
                 String address,
                 String city,
                 String telephone,
                 @Singular(ignoreNullCollections = true) Set<Pet> pets) {
        super(id, firstName, lastName);
        this.address = address;
        this.city = city;
        this.telephone = telephone;
        this.pets = pets;
    }

    public Pet getPet(String petName) {
        return getPet(petName, false);
    }

    public Pet getPet(String petName, boolean ignoreNew) {
        return pets.stream()
                .filter(pet -> !ignoreNew || !pet.isNew())
                .filter(pet -> pet.getName().equalsIgnoreCase(petName))
                .findFirst()
                .orElse(null);
    }
}
