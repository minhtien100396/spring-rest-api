package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create new subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        // check email exists
        boolean isEmailExist = this.subscriberService.isExistByEmail(subscriber.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + subscriber.getEmail() + " da ton tai");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update subscribers")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subscriber) throws IdInvalidException {
        // check id exists
        Optional<Subscriber> subscriberDBOptional = this.subscriberService.fetchById(subscriber.getId());
        if (!subscriberDBOptional.isPresent()) {
            throw new IdInvalidException("Subscriber voi Id = " + subscriber.getId() + " khong ton tai");
        }
        // create new resume
        return ResponseEntity.ok().body(this.subscriberService.update(subscriberDBOptional.get(), subscriber));
    }

}
