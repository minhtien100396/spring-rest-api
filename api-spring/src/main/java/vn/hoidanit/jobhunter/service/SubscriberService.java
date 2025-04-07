package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean isExistByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subscriber) {
        // check skill
        if (subscriber.getSkills().size() > 0 && subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Optional<Subscriber> fetchById(Long id) {
        return this.subscriberRepository.findById(id);
    }

    public Subscriber update(Subscriber subscriberDB, Subscriber subscriber) {
        // check skill
        if (subscriber.getSkills().size() > 0 && subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriberDB.setSkills(dbSkills);
        }

        return this.subscriberRepository.save(subscriberDB);
    }

}
