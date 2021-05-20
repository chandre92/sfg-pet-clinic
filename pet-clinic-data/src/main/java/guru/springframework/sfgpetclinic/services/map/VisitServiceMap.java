package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.Visit;
import guru.springframework.sfgpetclinic.services.VisitService;
import org.springframework.stereotype.Service;

@Service
public class VisitServiceMap extends AbstractMapService<Visit> implements VisitService {
    @Override
    public Visit save(Visit visit) {
        if (visit != null &&
                (visit.getPet() == null || visit.getPet().getId() == null
                || visit.getPet().getOwner() == null || visit.getPet().getOwner().getId() == null)) {
            throw new RuntimeException("Invalid Visit");
        }
        return super.save(visit);
    }
}
