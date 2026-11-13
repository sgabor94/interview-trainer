package com.interviewtrainer.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test double for {@link LlmClient}: returns pre-programmed outputs per output type and records
 * every request, so tests can check both the result handling and the prompts that were sent.
 * Active when {@code llm.provider=fake} (set in the test configuration).
 */
@Component
@ConditionalOnProperty(name = "llm.provider", havingValue = "fake")
public class FakeLlmClient implements LlmClient {

    public static final String MODEL = "fake-model";

    private final Map<Class<?>, Deque<Object>> outputs = new HashMap<>();
    private final List<LlmRequest> requests = new ArrayList<>();

    /** Queues outputs for {@code type}; each call to {@link #generate} returns the next one. */
    @SafeVarargs
    public final <T> FakeLlmClient willReturn(Class<T> type, T... values) {
        Deque<Object> queue = outputs.computeIfAbsent(type, t -> new ArrayDeque<>());
        queue.addAll(List.of(values));
        return this;
    }

    @Override
    public synchronized <T> LlmResult<T> generate(LlmRequest request, Class<T> outputType) {
        requests.add(request);
        Deque<Object> queue = outputs.get(outputType);
        if (queue == null || queue.isEmpty()) {
            throw new IllegalStateException("FakeLlmClient has no output queued for " + outputType.getSimpleName()
                    + "; call willReturn(" + outputType.getSimpleName() + ".class, ...) first");
        }
        return new LlmResult<>(outputType.cast(queue.poll()), MODEL, LlmUsage.NONE);
    }

    public List<LlmRequest> requests() {
        return List.copyOf(requests);
    }

    public LlmRequest lastRequest() {
        if (requests.isEmpty()) {
            throw new IllegalStateException("No request has been sent to FakeLlmClient");
        }
        return requests.getLast();
    }

    public void reset() {
        outputs.clear();
        requests.clear();
    }
}
