package repository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import persisted.RecurrentEvent;

import java.util.List;

import java.util.UUID;

@Component
public class RecurrentEventUnificationRepo {

    @Autowired
    private RecurrentEventRepo recurrentEventRepo;

    @Autowired
    private RecurrentRuleRepo recurrentRuleRepo;

    public <S extends RecurrentEvent> S save(S entity) {
        recurrentRuleRepo.save(entity.getRule());
        return recurrentEventRepo.save(entity);
    }

    public <S extends RecurrentEvent> void delete(S entity) {
        recurrentEventRepo.delete(entity);
    }

    public RecurrentEvent getOne(UUID uuid) {
        return recurrentEventRepo.getOne(uuid);
    }

    public List<RecurrentEvent> findAll() {
        return recurrentEventRepo.findAll();
    }
}
