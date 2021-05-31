package context.logic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import persisted.Event;
import persisted.RecurrentEvent;
import persisted.RecurrentRuleReferencePointType;
import persisted.RecurrentRuleType;
import repository.EventRepo;
import repository.RecurrentEventUnificationRepo;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * This class is responsible for generating the Event instances that result from RecurrentEvents.
 */
@Component
public class EventGenerator {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private RecurrentEventUnificationRepo recurrentEventRepo;

    private static final Logger logger = LoggerFactory.getLogger(EventGenerator.class);

    /**
     * Generates events if necessary
     */
    @Transactional
    public void generateEvents() throws EventGenerationException {
        List<RecurrentEvent> recurrentEventList = recurrentEventRepo.findAll();

        // Enough events generated when at this point
        LocalDateTime abortPoint = LocalDateTime.now();

        for(RecurrentEvent recurrentEvent : recurrentEventList) {
            Set<Event> allEvents = recurrentEvent.getEventList();
            Optional<Event> latestEventOptional = allEvents.stream().reduce(
                    (x,y) -> x.getLocalDateTime().compareTo(y.getLocalDateTime()) > 0 ? x : y
            );

            LocalDateTime eventSpecificAbortPoint = abortPoint.compareTo(recurrentEvent.getEndPoint()) < 0 ? abortPoint : recurrentEvent.getEndPoint();

            if (latestEventOptional.isPresent()) {
                generateEventsFor(recurrentEvent, latestEventOptional.get().getLocalDateTime(), eventSpecificAbortPoint);
            }else {
                generateEventsFor(recurrentEvent, recurrentEvent.getStartPoint(), eventSpecificAbortPoint);
            }
        }
    }

    private static class BeginBasedConfig {
        /**
         * This is a dummy object for data transfer between functions
         * @param startingReferencePoint the starting point for the Event generation
         * @param cycleIncreaseDuration The duration to increase by in each cycle
         * @param reoccurringTruncationRequired True if truncating is required else false
         */
        public BeginBasedConfig(LocalDateTime startingReferencePoint, Duration cycleIncreaseDuration, boolean reoccurringTruncationRequired) {
            this.startingReferencePoint = startingReferencePoint;
            this.cycleIncreaseDuration = cycleIncreaseDuration;
            this.reoccurringTruncationRequired = reoccurringTruncationRequired;
        }

        private final LocalDateTime startingReferencePoint;

        private final Duration cycleIncreaseDuration;

        private final boolean reoccurringTruncationRequired;

        public LocalDateTime getStartingReferencePoint() {
            return startingReferencePoint;
        }

        public Duration getCycleIncreaseDuration() {
            return cycleIncreaseDuration;
        }

        public boolean isReoccurringTruncationRequired() {
            return reoccurringTruncationRequired;
        }

    }

    /**
     * Returns the time config for the specified circumstances
     * @param recurrentEvent The event to determine the config for
     * @param statTime The start time of the config
     * @return The time config
     */
    private BeginBasedConfig getTimeConfig(RecurrentEvent recurrentEvent, LocalDateTime statTime) {
        LocalDateTime startingReferencePoint = null;
        Duration cycleIncreaseDuration = null;

        boolean reoccurringTruncationRequired = false;

        RecurrentRuleReferencePointType referencePointType = recurrentEvent.getRule().getReferencePointType();
        if (referencePointType == RecurrentRuleReferencePointType.hour) {
            startingReferencePoint = statTime.truncatedTo(ChronoUnit.HOURS);
            cycleIncreaseDuration = Duration.ofHours(1);
        } else if (referencePointType == RecurrentRuleReferencePointType.day) {
            startingReferencePoint = statTime.truncatedTo(ChronoUnit.DAYS);
            cycleIncreaseDuration = Duration.ofDays(1);
        } else if (referencePointType == RecurrentRuleReferencePointType.week) {
            startingReferencePoint = statTime.truncatedTo(ChronoUnit.WEEKS);
            cycleIncreaseDuration = Duration.ofDays(7);
        } else if (referencePointType == RecurrentRuleReferencePointType.month) {
            // Java can't truncate to months so this workaround has to do...
            startingReferencePoint = statTime.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
            // The longest months have 31 days
            cycleIncreaseDuration = Duration.ofDays(32);
            // Months differ in length
            reoccurringTruncationRequired = true;
        } else if (referencePointType == RecurrentRuleReferencePointType.year) {
            // Java can't truncate to years so this workaround has to do it
            startingReferencePoint = statTime.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
            // The longest years have 365 days
            cycleIncreaseDuration = Duration.ofDays(366);
            // Years differ in length
            reoccurringTruncationRequired = true;
        }

        return new BeginBasedConfig(startingReferencePoint, cycleIncreaseDuration, reoccurringTruncationRequired);
    }

    /**
     * generates begin based Events
     * @param recurrentEvent The parent Recurrent Event
     * @param statTime The startTime for generation
     * @param abortPoint The abort Point
     * @throws EventGenerationException If something goes wrong during generation
     */
    private void generateBeginBasedEvents(RecurrentEvent recurrentEvent, LocalDateTime statTime, LocalDateTime abortPoint) throws EventGenerationException {
        BeginBasedConfig beginBasedConfig = getTimeConfig(recurrentEvent, statTime);

        LocalDateTime currentReferencePoint = beginBasedConfig.getStartingReferencePoint();
        Duration durationToAddInGeneratorCycle = beginBasedConfig.getCycleIncreaseDuration();
        boolean reoccurringTruncationRequired = beginBasedConfig.isReoccurringTruncationRequired();

        RecurrentRuleReferencePointType referencePointType = recurrentEvent.getRule().getReferencePointType();

        if (currentReferencePoint != null && durationToAddInGeneratorCycle != null) {
            while (true) {
                LocalDateTime nextDateTime = currentReferencePoint.plus(durationToAddInGeneratorCycle);
                if(nextDateTime.compareTo(abortPoint) > 0) {
                    break;
                }
                Event event = new Event();
                event.setEventType(recurrentEvent.getType());
                event.setName(recurrentEvent.getName());
                event.setActor(recurrentEvent.getActor());
                event.setRecurrentParent(recurrentEvent);
                event.setAmount(recurrentEvent.getAmount());

                //Round back to hit the exact beginnings
                if (reoccurringTruncationRequired) {
                    if (referencePointType == RecurrentRuleReferencePointType.month) {
                        nextDateTime = nextDateTime.with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
                    } else if (referencePointType == RecurrentRuleReferencePointType.year) {
                        nextDateTime = nextDateTime.with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
                    } else {
                        throw new EventGenerationException("Impossible state reached");
                    }
                }

                LocalDateTime eventTime = nextDateTime.plus(recurrentEvent.getRule().getInterval());
                event.setLocalDateTime(eventTime);

                // append event to recurrent event
                recurrentEvent.getEventList().add(event);

                // persist Event
                eventRepo.save(event);

                logger.debug("Generated begin based event on " + event.getLocalDateTime().toString());

                // prepare currentReferencePoint for next iteration
                currentReferencePoint = nextDateTime;
            }
            // update RecurrentEvent
            recurrentEventRepo.save(recurrentEvent);

        } else {
            throw new EventGenerationException("No option picked!");
        }
    }

    /**
     * generates Interval based events
     * @param recurrentEvent The parent recurrent event
     * @param statTime The start time for generation
     * @param abortPoint The abort point
     */
    private void generateIntervalEvents(RecurrentEvent recurrentEvent, LocalDateTime statTime, LocalDateTime abortPoint) {
        LocalDateTime currentDateTime = statTime;

        while (true) {
            LocalDateTime nextDateTime = currentDateTime.plus(recurrentEvent.getRule().getInterval());

            if(nextDateTime.compareTo(abortPoint) > 0) {
                break;
            }

            Event event = new Event();
            event.setEventType(recurrentEvent.getType());
            event.setName(recurrentEvent.getName());
            event.setActor(recurrentEvent.getActor());
            event.setRecurrentParent(recurrentEvent);
            event.setAmount(recurrentEvent.getAmount());

            event.setLocalDateTime(nextDateTime);
            recurrentEvent.getEventList().add(event);

            // persist Event
            eventRepo.save(event);

            logger.debug("Generated interval event on " + event.getLocalDateTime().toString());

            // prepare currentReferencePoint for next iteration
            currentDateTime = nextDateTime;
        }

        // update RecurrentEvent
        recurrentEventRepo.save(recurrentEvent);
    }

    /**
     * Generates events for the specified Recurrent event between start Point and abort Point
     * @param recurrentEvent The RecurrentEvent
     * @param statTime The stating point at which event generation starts
     * @param abortPoint The abort point at which event generation stops
     * @throws EventGenerationException In case no event type is set
     */
    private void generateEventsFor(RecurrentEvent recurrentEvent, LocalDateTime statTime, LocalDateTime abortPoint) throws EventGenerationException {
        if (recurrentEvent.getRule().getType() == RecurrentRuleType.beginBased) {
            generateBeginBasedEvents(recurrentEvent, statTime, abortPoint);
        } else if (recurrentEvent.getRule().getType() == RecurrentRuleType.interval) {
            generateIntervalEvents(recurrentEvent, statTime, abortPoint);
        } else {
            throw new EventGenerationException("No RecurrentEventType set.");
        }
    }

}
