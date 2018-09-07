package cn.rectcircle.reactivelearn.dao;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import cn.rectcircle.reactivelearn.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, String> { // User和ID的类型
	Mono<User> findByUsername(String username); // 类似JPA的方式
	Mono<Long> deleteByUsername(String username);
}