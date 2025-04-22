package dev.akarah.event.handler;

import java.util.concurrent.Flow;

public class EventSubscription<T> implements Flow.Subscription {
    EventPublisher<? super T> publisher;
    Flow.Subscriber<? super T> subscriber;

    long requests;
    boolean cancelled = false;

    public EventSubscription(
            EventPublisher<? super T> publisher,
            Flow.Subscriber<? super T> subscriber
    ) {
        this.publisher = publisher;
        this.subscriber = subscriber;
    }

    public EventPublisher<? super T> publisher() {
        return this.publisher;
    }

    public Flow.Subscriber<? super T> subscriber() {
        return this.subscriber;
    }

    @Override
    public void request(long n) {
        this.requests += 1;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public void tryForward(T event) {
        if(this.requests <= 0) {
            return;
        }
        if(this.cancelled) {
            return;
        }
        this.subscriber.onNext(event);
    }
}
