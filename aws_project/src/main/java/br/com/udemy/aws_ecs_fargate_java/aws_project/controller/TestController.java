package br.com.udemy.aws_ecs_fargate_java.aws_project.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@Log4j2
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/dog/{name}")
    public ResponseEntity<?> dogTest(@PathVariable String name) {
        log.info("Test controller - dog name: {}", name);
        return ResponseEntity.ok("Name: " + name);
    }

    @GetMapping("/random-number")
    public ResponseEntity<?> getRandomNumber(@RequestParam(required = false) Integer begin, @RequestParam(required = false) Integer end) {
        final Integer realBegin = Optional.ofNullable(begin).orElse(0);
        final Integer realEnd = Optional.ofNullable(end).orElse(Integer.MAX_VALUE);
        final Integer result = new Random().ints(realBegin, realEnd).limit(1).findFirst().orElse(-1);
        log.info("Test controller - random number: {}", result);
        return ResponseEntity.ok("Number: " + result);
    }

}
