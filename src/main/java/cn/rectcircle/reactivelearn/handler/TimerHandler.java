package cn.rectcircle.reactivelearn.handler;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 
 *
 * @author sunben
 * @date 2018-07-26
 * @version 0.0.1
 */
@Service
public class TimerHandler {
	public Mono<ServerResponse> getTime(ServerRequest req){
		return ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(
					Mono.just(
						"Now is " + 
							new SimpleDateFormat("HH:mm:ss").format(new Date())), 
						String.class);
	}

	public Mono<ServerResponse> getDate(ServerRequest serverRequest) {
		return ok()
				.contentType(MediaType.TEXT_PLAIN)
				.body(
					Mono.just(
						"Today is " + 
							new SimpleDateFormat("yyyy-MM-dd").format(new Date())), 
						String.class);
	}

	public Mono<ServerResponse> sendTimePerSec(ServerRequest serverRequest) {
		return ok()
				.contentType(MediaType.TEXT_EVENT_STREAM) // 使用SSE技术，MediaType.TEXT_EVENT_STREAM表示Content-Type为text/event-stream
				.body( 
					Flux.interval(Duration.ofSeconds(1)) // 利用interval生成每秒一个数据的流
						.map(l -> new SimpleDateFormat("HH:mm:ss").format(new Date())),
					String.class);
	}
}