package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.util.err.IdInvalidException;

@RestController
public class Hello {
    @GetMapping("/")
    public String geString() throws IdInvalidException {
        if (true) {
            throw new IdInvalidException("Invalid");
        }
        return "Invalid";

    }
}
