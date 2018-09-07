package cn.rectcircle.reactivelearn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.rectcircle.reactivelearn.dao.MyEventRepository;
import cn.rectcircle.reactivelearn.model.MyEvent;
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
@RequestMapping("/events")
public class MyEventController {
	@Autowired
	private MyEventRepository myEventRepository;

	@PostMapping(path = "", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE) // application/stream+json
	public Mono<Void> loadEvents(@RequestBody Flux<MyEvent> events) {
		// POST方法的接收数据流的Endpoint，所以传入的参数是一个Flux，
		//返回结果其实就看需要了，我们用一个Mono<Void>作为方法返回值，表示如果传输完的话只给一个“完成信号”就OK了；
		return myEventRepository.insert(events).then(); // then方法表示“忽略数据元素，只返回一个完成信号”。
	}

	@GetMapping(path = "", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<MyEvent> getEvents() { // 2
		// return myEventRepository.findAll().log();
		return myEventRepository.findBy().log();
	}
}