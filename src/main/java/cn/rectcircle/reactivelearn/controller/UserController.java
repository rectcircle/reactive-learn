package cn.rectcircle.reactivelearn.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.rectcircle.reactivelearn.model.User;
import cn.rectcircle.reactivelearn.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 *
 * @author sunben
 * @date 2018-07-26
 * @version 0.0.1
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping("")
	public Mono<User> save(User user) {
		return userService.save(user);
	}

	@DeleteMapping("/{username}")
	public Mono<Long> deleteByUsername(@PathVariable String username) {
		return userService.deleteByUsername(username);
	}

	@GetMapping("/{username}")
	public Mono<User> findByUsername(@PathVariable String username) {
		return userService.findByUsername(username);
	}

	@GetMapping("")
	public Flux<User> findAll() {
		return userService.findAll().delayElements(Duration.ofSeconds(1));
	}

	@GetMapping(value = "stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<User> findAllStream() {
		return this.userService.findAll().delayElements(Duration.ofSeconds(2));
	}

}