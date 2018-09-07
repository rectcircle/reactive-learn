package cn.rectcircle.reactivelearn.reactorlearn;

import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * 
 * Reactor 3 学习
 *
 * @author sunben
 * @date 2018-07-25
 * @version 0.0.1
 */
public class ReactorLearnTest {
	private Flux<Integer> generateFluxFrom1To6() {
		return Flux.just(1, 2, 3, 4, 5, 6);
	}

	private Mono<Integer> generateMonoWithError() {
		return Mono.error(new Exception("some error"));
	}

	@Test
	public void testViaStepVerifier() {
		StepVerifier.create(generateFluxFrom1To6()).expectNext(1, 2, 3, 4, 5, 6).expectComplete().verify();
		StepVerifier.create(generateMonoWithError()).expectErrorMessage("some error").verify();
	}
}