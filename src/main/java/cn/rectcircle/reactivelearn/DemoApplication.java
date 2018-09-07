package cn.rectcircle.reactivelearn;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.scheduling.annotation.EnableScheduling;

import cn.rectcircle.reactivelearn.model.MyEvent;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(MongoOperations mongo) { // 注入 mongo
		return (String... args) -> { // 命令行参数
			mongo.dropCollection(MyEvent.class); // 首先删除集合中的全部数据
			mongo.createCollection(
				MyEvent.class, 
				CollectionOptions 
					.empty() //创建一个空集合
					.maxDocuments(10) //最多10条记录
					.size(100000) //限制总大小
					.capped()); //生成配置
		};
	}

}
