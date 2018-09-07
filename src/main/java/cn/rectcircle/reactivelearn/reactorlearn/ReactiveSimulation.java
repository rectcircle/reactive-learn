package cn.rectcircle.reactivelearn.reactorlearn;

import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


abstract class Flux<T> implements Publisher<T> {
	
	public abstract void subscribe(Subscriber<? super T> s);

	//工具方法，从参数列表中生成发布者
	public static <T> Flux<T> just(T... data) {
		return new FluxArray<>(data);
	}

	static class FluxArray<T> extends Flux<T> {
		private T[] array; // 1

		public FluxArray(T[] data) {
			this.array = data;
		}

		@Override
		public void subscribe(Subscriber<? super T> actual) {
			actual.onSubscribe(new ArraySubscription<>(actual, array)); // 构建一个Subscription并作为Subscriber的参数，调用
		}

		static class ArraySubscription<T> implements Subscription { // 持有数据和Subscriber
			final Subscriber<? super T> actual;
			final T[] array; // 数据的备份
			int index;
			boolean canceled;

			public ArraySubscription(Subscriber<? super T> actual, T[] array) {
				this.actual = actual;
				this.array = array;
			}

			@Override
			public void request(long n) {
				if (canceled) {
					return;
				}
				long length = array.length;
				for (int i = 0; i < n && index < length; i++) {
					actual.onNext(array[index++]); // 调用Subscriber.next
				}
				if (index == length) {
					actual.onComplete(); // 完成后调用Subscriber.onComplete
				}
			}

			@Override
			public void cancel() { // 取消操作
				this.canceled = true;
			}
		}
	}

	public <V> Flux<V> map(Function<? super T, ? extends V> mapper) { // 实现一个操作符map
		return new FluxMap<>(this, mapper); // 构建一个新的Publisher包裹this
	}

	static class FluxMap<T, R> extends Flux<R>{
		private final Flux<? extends T> source; 
		private final Function<?super T,?extends R>mapper;
		
		public FluxMap(Flux<? extends T> source, Function<? super T, ? extends R> mapper){
			this.source = source;
			this.mapper = mapper;
		}

		@Override
		public void subscribe(Subscriber<? super R> actual) {
			source.subscribe(new Subscriber<T>(){ //一个代理调用
					@Override
					public void onSubscribe(Subscription s) {
						actual.onSubscribe(s);
					}

					@Override
					public void onNext(T t) {
						actual.onNext(mapper.apply(t)); //在此应用变换
					}

					@Override
					public void onError(Throwable t) {
						actual.onError(t);
					}

					@Override
					public void onComplete() {
						actual.onComplete();
					}
				});
		}
	}

	public void subscribe(Consumer<? super T> consumer, //简化调用操作
			Consumer<? super Throwable> errorConsumer,
			Runnable completeConsumer){
		this.subscribe(new Subscriber<T>() {
			@Override
			public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(T t) {
				consumer.accept(t);
			}

			@Override
			public void onError(Throwable t) {
				errorConsumer.accept(t);
			}

			@Override
			public void onComplete() {
				completeConsumer.run();
			}
		});
	}
}

public class ReactiveSimulation {

	public static void main(String[] args) {
		Flux
			.just(1, 2, 3, 4, 5)
			.map(i->i*2)
			.subscribe(new Subscriber<Integer>() { // 1
				@Override
				public void onSubscribe(Subscription s) {
					System.out.println("onSubscribe");
					s.request(6); // 2
				}

				@Override
				public void onNext(Integer integer) {
					System.out.println("onNext:" + integer);
				}

				@Override
				public void onError(Throwable t) {

				}

				@Override
				public void onComplete() {
					System.out.println("onComplete");
				}
		});

		Flux
			.just(1, 2, 3, 4, 5)
			.map(i->i*2)
			.subscribe(System.out::println, t-> t.printStackTrace() , ()->System.out.println("完成"));
	}
}