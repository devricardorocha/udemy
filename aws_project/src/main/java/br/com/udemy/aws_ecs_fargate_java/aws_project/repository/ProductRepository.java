package br.com.udemy.aws_ecs_fargate_java.aws_project.repository;

import br.com.udemy.aws_ecs_fargate_java.aws_project.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findByCode(String code);

}
