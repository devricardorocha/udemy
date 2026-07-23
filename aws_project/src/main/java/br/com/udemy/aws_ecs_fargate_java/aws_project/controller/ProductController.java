package br.com.udemy.aws_ecs_fargate_java.aws_project.controller;

import br.com.udemy.aws_ecs_fargate_java.aws_project.model.Product;
import br.com.udemy.aws_ecs_fargate_java.aws_project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping
    public Iterable<Product> findAll() {
        return this.productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        return ResponseEntity.ofNullable(
                productRepository.findById(id).orElse(null));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Product> findById(@PathVariable String code) {
        return ResponseEntity.ofNullable(
                productRepository.findByCode(code).orElse(null));
    }

    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        Product created = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {

        if (productRepository.existsById(id)) {
            product.setId(id);
            Product created = productRepository.save(product);
            return new ResponseEntity<>(created, HttpStatus.OK);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

}
