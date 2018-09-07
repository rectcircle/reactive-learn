package cn.rectcircle.reactivelearn.controller;

// import java.text.SimpleDateFormat;
// import java.util.Date;

// import org.springframework.http.MediaType;
// import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.context.request.async.DeferredResult;
// import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import reactor.core.publisher.Mono;

/**
 * 
 *
 * @author sunben
 * @date 2018-07-26
 * @version 0.0.1
 */
@RestController
public class HelloController {
	@GetMapping("/hello")
	public Mono<String> hello() {
		return Mono.just("Hello World");
	}

	// private final SseEmitter emitter = new SseEmitter();

	// @Scheduled(fixedRate = 1000)
	// void timerHandler() {
	// 	try {
	// 		emitter.send(new SimpleDateFormat("HH:mm:ss").format(new Date()), MediaType.TEXT_PLAIN);
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 		emitter.completeWithError(e);
	// 	}
	// }
	// //TO SEE https://github.com/aliakh/demo-spring-sse
	// @GetMapping(path = "/times")
	// public SseEmitter getInfiniteMessages() {
	// 	return emitter;
	// }


}