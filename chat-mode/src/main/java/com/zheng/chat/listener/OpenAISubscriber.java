package com.zheng.chat.listener;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description -------
 * @date 2024/1/17 16:45:36
 */
@Slf4j
public class OpenAISubscriber implements Subscriber<String>, Disposable {
    private final FluxSink<String> emitter;



    public OpenAISubscriber(FluxSink<String> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onSubscribe(Subscription s) {

    }

    @Override
    public void onNext(String s) {
        log.info("消息返回{}",s);
    }

    @Override
    public void onError(Throwable t) {
        emitter.complete();
    }

    @Override
    public void onComplete() {
        emitter.complete();
    }

    @Override
    public void dispose() {
        log.warn("OpenAI返回数据关闭");
        emitter.complete();
    }
}
