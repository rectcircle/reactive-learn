package cn.rectcircle.reactivelearn.reactorlearn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * 
 * Reactor 3 学习
 *
 * @author sunben
 * @date 2018-07-25
 * @version 0.0.1
 */
public class ReactorLearn {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("===发布者===");
		/*
		 * 发布者对象：Flux、Mono 
		 * 一个Flux对象代表一个包含0..N个元素的响应式序列
		 * 一个Mono对象代表一个包含零/一个（0..1）元素的结果
		 */
		//从参数列表创建发布者
		Flux.just(1, 2, 3, 4, 5, 6);
		Mono.just(1);
		//从其他容器创建发布者
		Integer[] array = new Integer[] { 1, 2, 3, 4, 5, 6 };
		Flux.fromArray(array);
		List<Integer> list = Arrays.asList(array);
		Flux.fromIterable(list);
		Stream<Integer> stream = list.stream();
		Flux.fromStream(stream);

		/*
		 * 发布者数据准备终止信号有两种如下：
		 * 完成信号
		 * 错误信号
		 */
		// 只有完成信号的空数据流
		Flux.just();
		Flux.empty();
		Mono.empty();
		Mono.justOrEmpty(Optional.empty());
		// 只有错误信号的数据流
		Flux.error(new Exception("some error"));
		Mono.error(new Exception("some error"));

		System.out.println("===订阅===");
		Flux.just(1, 2, 3, 4, 5, 6).subscribe(System.out::print);
		System.out.println();
		Mono.just(1).subscribe(System.out::println);

		Flux.just(1, 2, 3, 4, 5, 6)
			.subscribe(
				System.out::println, //完成信号
				System.err::println, //错误信号
				() -> System.out.println("Completed!")); //监听完成信号，正常处理完成后执行（错误不处理）
		Mono.error(new Exception("some error"))
			.subscribe(
				System.out::println, 
				System.err::println,
				() -> System.out.println("Completed!")); //没有输出Completed!


		System.out.println("===操作符===");
		// Flux<E2> = Flux<E1>.map(E1 -> E2)
		Flux.range(1, 6)
			.map(i->i*i); //map：映射，结果为：(1, 4, 9, 16, 25, 36)
		// Flux<E2> = Flux<E1>.flatMap(E1 -> Flux<E2>)
		Flux.just("flux", "mono")
			.flatMap(s -> Flux.fromArray(s.split("\\s*")))
			.subscribe(System.out::println);
		// filter 过滤
		Flux.range(1, 6)
			.filter(i -> i % 2 == 1);
		// zip、zipWith 合并成二元组
		Flux.zip(Flux.just(1,2), Flux.just(3,4));
		Flux.just(1,2).zipWith(Flux.just(3,4));

		/*
		 * 其他操作符： 用于编程方式自定义生成数据流的create和generate等及其变体方法；
		 * 用于“无副作用的peek”场景的doOnNext、doOnError、doOncomplete、doOnSubscribe、doOnCancel等及其变体方法；
		 * 用于数据流转换的when、and/or、merge、concat、collect、count、repeat等及其变体方法；
		 * 用于过滤/拣选的take、first、last、sample、skip、limitRequest等及其变体方法；
		 * 用于错误处理的timeout、onErrorReturn、onErrorResume、doFinally、retryWhen等及其变体方法；
		 * 用于分批的window、buffer、group等及其变体方法； 用于线程调度的publishOn和subscribeOn方法。
		 */

		System.out.println("===异步模式===");
		
		/*
		 * Schedulers，提供对线程池的封装，可以方便的实现异步 
		 * 当前线程（Schedulers.immediate()）；
		 * 可重用的单线程（Schedulers.single()）。注意，这个方法对所有调用者都提供同一个线程来使用，
		 * 直到该调度器被废弃。如果你想使用独占的线程，请使用Schedulers.newSingle()；
		 * 弹性线程池（Schedulers.elastic()）。它根据需要创建一个线程池，重用空闲线程。线程池如果空闲时间过长 （默认为 60s）就会被废弃。对于
		 * I/O 阻塞的场景比较适用。Schedulers.elastic()能够方便地给一个阻塞 的任务分配它自己的线程，从而不会妨碍其他任务和资源；
		 * 固定大小线程池（Schedulers.parallel()），所创建线程池的大小与CPU个数等同；
		 * 自定义线程池（Schedulers.fromExecutorService(ExecutorService)）
		 * 基于自定义的ExecutorService创建 Scheduler（虽然不太建议，不过你也可以使用Executor来创建）。
		 */
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Mono.fromCallable(() -> { // 使用fromCallable声明一个基于Callable的Mono；
			try {
				TimeUnit.SECONDS.sleep(2);
				System.out.println(Thread.currentThread().getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "Hello, Reactor!";
		}).subscribeOn(Schedulers.elastic()) // 使用subscribeOn将任务调度到Schedulers内置的弹性线程池执行，弹性线程池会为Callable的执行任务分配一个单独的线程。
				.subscribe(System.out::println, null, countDownLatch::countDown);
		countDownLatch.await(10, TimeUnit.SECONDS);

		/*
		 * 使用subscribeOn、publishOn可以更改执行环境
		 * 
		 * publishOn会影响链中其后的操作符
		 * subscribeOn无论出现在什么位置，都只影响源头的执行环境
		 */


		System.out.println("===错误处理===");
		//一旦发生错误，将直接终止对后续数据的处理
		//最终错误处理
		Flux.range(1, 6)
			.map(i -> 10 / (i - 3)) // 会发生除零错误
			.map(i -> i * i)
			.subscribe(System.out::println, System.err::println);
		//捕获异常使用默认值
		Flux.range(1, 6)
			.map(i -> 10 / (i - 3))
			.onErrorReturn(0)
			.map(i -> i * i)
			.subscribe(System.out::println, s -> System.err.println("error:"+s));
		//根据异常信息，提供新的数据流
		Flux.range(1, 6)
			.map(i -> 10 / (i - 3))
			.onErrorResume(e -> Flux.just(7,8,9)) // 提供新的数据流
			.map(i -> i * i)
			.subscribe(System.out::println, System.err::println);
		// 捕获，并再包装为某一个业务相关的异常，然后再抛出业务异常
		// onErrorMap
		// 捕获，记录错误日志，然后继续抛出
		// doOnError
		// 使用 finally 来清理资源，或使用 Java 7 引入的 “try-with-resource”
		/*
			资源使用前声明
			Flux.using(
					() -> getResource(),    // 第一个参数获取资源；
					resource -> Flux.just(resource.getAll()),   // 第二个参数利用资源生成数据流；
					MyResource::clean   // 第三个参数最终清理资源。
			);
			使用doFinally
			LongAdder statsCancel = new LongAdder();    // 1

			Flux<String> flux =
			Flux.just("foo", "bar")
				.doFinally(type -> {
					if (type == SignalType.CANCEL)  // 2
					statsCancel.increment();  // 3
				})
				.take(1);   // 4
		*/
		//重试
		//retry(1)发生错误重试1次

		System.out.println("===背压===");
		Flux.range(1, 6) // 1
				.doOnRequest(n -> System.out.println("Request " + n + " values...")) // 2
				.subscribe(new BaseSubscriber<Integer>() { // 3
					@Override
					protected void hookOnSubscribe(Subscription subscription) { // 4
						System.out.println("Subscribed and make a request...");
						request(1); // 5
					}

					@Override
					protected void hookOnNext(Integer value) { // 6
						try {
							TimeUnit.SECONDS.sleep(1); // 7
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("Get value [" + value + "]"); // 8
						request(1); // 9
					}
				});

	}
}