package dev.akarah.event.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

public final class EventPublisher<T> implements Flow.Publisher<T> {
    final List<EventSubscription<? super T>> subscribers = new ArrayList<>();

    public List<EventSubscription<? super T>> subscribers() {
        return this.subscribers;
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        var sub = new EventSubscription<T>(
                this,
                subscriber
        );
        subscriber.onSubscribe(sub);
        this.subscribers.add(sub);
    }

    public void subscribe(Consumer<? super T> consumer) {
        this.subscribe(new Flow.Subscriber<T>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
                consumer.accept(item);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void pushEvent(T event) {
        for(var subscriber : this.subscribers) {
            subscriber.tryForward(event);
        }
    }
}
