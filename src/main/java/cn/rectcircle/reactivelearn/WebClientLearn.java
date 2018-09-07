package cn.rectcircle.reactivelearn;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import cn.rectcircle.reactivelearn.model.MyEvent;
import cn.rectcircle.reactivelearn.model.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class WebClientLearn {

	public void webClientTest1() throws InterruptedException {
		WebClient webClient = WebClient.create("http://localhost:8080"); // 创建一个web client
		Mono<String> resp = webClient
				.get()
				.uri("/hello")
				.retrieve() // 异步地获取response信息；
				.bodyToMono(String.class); // 将response body解析为字符串；
		resp.subscribe(System.out::println); // 输出
		TimeUnit.SECONDS.sleep(1); // 睡眠一秒等待结果
	}

	public void webClientTest2() throws InterruptedException {
		WebClient webClient = WebClient
			.builder()
			.baseUrl("http://localhost:8080")
			.build(); // 使用WebClientBuilder来构建WebClient对象；
		webClient.get()
				.uri("/user")
				.accept(MediaType.APPLICATION_STREAM_JSON) // 配置请求Header：Accept: application/stream+json；
				.exchange() // 获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”
				.flatMapMany(response -> response.bodyToFlux(User.class)) // 使用flatMap来将ClientResponse映射为Flux；
				.doOnNext(System.out::println) // 只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
				.blockLast(); // 上个例子中sleep的方式有点low，blockLast方法，顾名思义，在收到最后一个元素前会阻塞，响应式业务场景中慎用。
	}

	public void webClientTest3() throws InterruptedException {
		WebClient webClient = WebClient.create("http://localhost:8080");
		webClient.get()
				.uri("/times")
				.accept(MediaType.TEXT_EVENT_STREAM) // 配置请求Header：Accept: text/event-stream，即SSE；
				.retrieve()
				.bodyToFlux(String.class)
				.log() // 用log()代替doOnNext(System.out::println)来查看每个元素；
				.take(10) // 由于/times是一个无限流，这里取前10个，会导致流被取消；
				.blockLast();
	}

	public void webClientTest4() {
		Flux<MyEvent> eventFlux = Flux.interval(Duration.ofSeconds(1))
				.map(l -> new MyEvent(System.currentTimeMillis(), "message-" + l))
				.take(5); // 创建长度为5的流
		WebClient webClient = WebClient.create("http://localhost:8080");
		webClient.post()
				.uri("/events")
				.contentType(MediaType.APPLICATION_STREAM_JSON) // 声明请求体的数据格式为application/stream+json；
				.body(eventFlux, MyEvent.class) // 类型转换
				.retrieve()
				.bodyToMono(Void.class)
				.block();
	}

	public static void main(String[] args) throws InterruptedException {
		WebClientLearn test = new WebClientLearn();
		// test.webClientTest1();
		// test.webClientTest2();
		// test.webClientTest3();
		test.webClientTest4();
	}

}