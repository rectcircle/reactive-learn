package cn.rectcircle.reactivelearn.route;


import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import cn.rectcircle.reactivelearn.handler.TimerHandler;

/**
 * 
 *
 * @author sunben
 * @date 2018-07-26
 * @version 0.0.1
 */
@Configuration
public class RouterConfig {
	@Autowired
	private TimerHandler timeHandler;

	@Bean
	public RouterFunction<ServerResponse> timeRouter() {
		return route(GET("/time"), timeHandler::getTime)
				.andRoute(GET("/date"), timeHandler::getDate)
				.andRoute(GET("/times"), timeHandler::sendTimePerSec); // 增加这一行
	}
	
}