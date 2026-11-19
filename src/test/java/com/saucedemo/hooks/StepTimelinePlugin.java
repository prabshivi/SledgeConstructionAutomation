package com.saucedemo.hooks;

import com.saucedemo.reporting.PlaywrightReportGenerator.Step;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/** Cucumber plugin collecting step text / status / duration per scenario for the HTML report. */
public class StepTimelinePlugin implements ConcurrentEventListener {
    private static final Map<String, List<Step>> STEPS = new ConcurrentHashMap<>();

    public static List<Step> stepsFor(String scenarioName) {
        return STEPS.getOrDefault(scenarioName, List.of());
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestStepFinished.class, this::onStep);
    }

    private void onStep(TestStepFinished e) {
        if (!(e.getTestStep() instanceof PickleStepTestStep ps)) return;
        Result r = e.getResult();
        String err = r.getError() == null ? null : r.getError().toString();
        STEPS.computeIfAbsent(e.getTestCase().getName(), k -> new CopyOnWriteArrayList<>())
                .add(new Step(ps.getStep().getKeyword(), ps.getStep().getText(), r.getStatus().name().equals("PASSED") ? "PASSED" : r.getStatus().name(),
                        r.getDuration().toMillis(), err));
    }
}
