package cn.rectcircle.reactivelearn.reactorlearn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import reactor.core.publisher.Flux;

class MyEventSource {

	private List<MyEventListener> listeners;

	public MyEventSource() {
		this.listeners = new ArrayList<>();
	}

	public void register(MyEventListener listener) { // 注册监听器；
		listeners.add(listener);
	}

	public void newEvent(MyEvent event) {
		for (MyEventListener listener : listeners) {
			listener.onNewEvent(event); // 向监听器发出新事件；
		}
	}

	public void eventStopped() {
		for (MyEventListener listener : listeners) {
			listener.onEventStopped(); // 告诉监听器事件源已停止；
		}
	}

	public static class MyEvent {
		private Date timeStemp;
		private String message;
		public MyEvent(){

		}
		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}
		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}
		public MyEvent(Date timeStemp, String message){
			this.timeStemp = timeStemp;
			this.message = message;
		}
		/**
		 * @return the timeStemp
		 */
		public Date getTimeStemp() {
			return timeStemp;
		}
		/**
		 * @param timeStemp the timeStemp to set
		 */
		public void setTimeStemp(Date timeStemp) {
			this.timeStemp = timeStemp;
		}
		
		@Override
		public String toString() {
			return "MyEvent["+"timeStemp="+timeStemp+", message="+message+"]";
		}
	}
}

interface MyEventListener {
	void onNewEvent(MyEventSource.MyEvent event);

	void onEventStopped();
}

public class DataStreamLearn {

	public static void testGenerate(){
		// final AtomicInteger count = new AtomicInteger(1); // 外部变量，用于计数；
		// Flux.generate(sink -> {
		// 	sink.next(count.get() + " : " + new Date()); // 生成函数，向“池子”放自定义的数据；
		// 	try {
		// 		TimeUnit.SECONDS.sleep(1);
		// 	} catch (InterruptedException e) {
		// 		e.printStackTrace();
		// 	}
		// 	if (count.getAndIncrement() >= 5) {
		// 		sink.complete(); // 完成信号
		// 	}
		// }).subscribe(System.out::println); // 订阅

		// //使用伴随状态
		// Flux.generate(() -> 1, // 1
		// 		(count, sink) -> { // 2
		// 			sink.next(count + " : " + new Date());
		// 			try {
		// 				TimeUnit.SECONDS.sleep(1);
		// 			} catch (InterruptedException e) {
		// 				e.printStackTrace();
		// 			}
		// 			if (count >= 5) {
		// 				sink.complete();
		// 			}
		// 			return count + 1; // 3
		// 		}).subscribe(System.out::println);

		// 完成后处理
		Flux.generate(() -> 1, (count, sink) -> {
			sink.next(count + " : " + new Date());
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (count >= 5) {
				sink.complete();
			}
			return count + 1;
		}, System.out::println) // 完成处理函数
				.subscribe(System.out::println);
	}

	public static void testCreate() throws InterruptedException{
        MyEventSource eventSource = new MyEventSource();    // 创建一个事件源
        Flux.create(sink -> { //立即执行
					eventSource.register(new MyEventListener() {    // 向事件源注册用匿名内部类创建的监听器；
						{
							System.out.println("注册了一个监听器");
						}
                        @Override
                        public void onNewEvent(MyEventSource.MyEvent event) {
                            sink.next(event);       // 当事件到来后，传递数据
                        }

                        @Override
                        public void onEventStopped() {
                            sink.complete();        // 当监听到停止信号停止数据
                        }
                    });
                }
        ).subscribe(System.out::println);       // 订阅数据源
		System.out.println("完成响应链条");
        for (int i = 0; i < 20; i++) {  // 向事件源中添加20个事件
            Random random = new Random();
            TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
            eventSource.newEvent(new MyEventSource.MyEvent(new Date(), "Event-" + i));  
        }
        eventSource.eventStopped(); // 停止事件
	}

	public static void main(String[] args) throws InterruptedException {
		System.out.println("===testGenerate===");
		testGenerate();
		System.out.println("===testCreate===");
		testCreate();

	}
}