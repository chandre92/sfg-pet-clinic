package guru.springframework.sfgpetclinic.services.map;

import guru.springframework.sfgpetclinic.model.BaseEntity;
import guru.springframework.sfgpetclinic.services.CrudService;

import java.util.*;

public abstract class AbstractMapService<T extends BaseEntity> implements CrudService<T, Long> {
    protected Map<Long, T> map = new HashMap<>();

    @Override
    public Set<T> findAll() {
        return new HashSet<>(map.values());
    }

    @Override
    public T findById(Long id) {
        return map.get(id);
    }

    @Override
    public T save(T object) {
        if (object != null && object.getId() == null) {
            object.setId(getNextId());
            map.put(object.getId(), object);
        }
        return object;
    }

    @Override
    public void deleteById(Long id) {
        map.remove(id);
    }

    @Override
    public void delete(T object) {
        map.remove(object.getId());
    }

    private Long getNextId() {
        return map.isEmpty() ? 1 : Collections.max(map.keySet()) + 1;
    }
}
