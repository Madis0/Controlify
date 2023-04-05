package dev.isxander.controlify.rumble;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;

import java.util.function.Function;

public class ContinuousRumbleEffect implements RumbleEffect {
    private final Function<Integer, RumbleState> stateFunction;
    private final int priority;
    private final int timeout;
    private final int minTime;
    private int tick;
    private boolean stopped;

    public ContinuousRumbleEffect(Function<Integer, RumbleState> stateFunction, int priority, int timeout, int minTime) {
        this.stateFunction = stateFunction;
        this.priority = priority;
        this.timeout = timeout;
        this.minTime = minTime;
    }

    @Override
    public void tick() {
        tick++;
    }

    @Override
    public RumbleState currentState() {
        if (tick == 0)
            throw new IllegalStateException("Effect hasn't ticked yet.");

        return stateFunction.apply(tick - 1);
    }

    public void stop() {
        stopped = true;
    }

    @Override
    public int age() {
        return tick;
    }

    @Override
    public boolean isFinished() {
        return (stopped || (timeout > 0 && tick >= timeout)) && tick >= minTime;
    }

    @Override
    public int priority() {
        return this.priority;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Function<Integer, RumbleState> stateFunction;
        private int priority;
        private int timeout = -1;
        private int minTime;
        private InWorldProperties inWorldProperties;

        private Builder() {
        }

        public Builder byTick(Function<Integer, RumbleState> stateFunction) {
            this.stateFunction = stateFunction;
            return this;
        }

        public Builder constant(RumbleState state) {
            this.stateFunction = tick -> state;
            return this;
        }

        public Builder constant(float strong, float weak) {
            return this.constant(new RumbleState(strong, weak));
        }

        public Builder timeout(int timeoutTicks) {
            Validate.isTrue(timeoutTicks >= 0, "the timeout cannot be negative!");

            this.timeout = timeoutTicks;
            return this;
        }

        public Builder minTime(int minTimeTicks) {
            Validate.isTrue(minTimeTicks >= 0, "the minimum time cannot be negative!");

            this.minTime = minTimeTicks;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder inWorld(Vec3 sourceLocation, float minMagnitude, float maxMagnitude, float minDistance, float maxDistance, Function<Float, Float> fallofFunction) {
            this.inWorldProperties = new InWorldProperties(sourceLocation, minMagnitude, maxMagnitude, minDistance, maxDistance, fallofFunction);
            return this;
        }

        public ContinuousRumbleEffect build() {
            Validate.notNull(stateFunction, "stateFunction cannot be null!");
            Validate.isTrue(minTime <= timeout || timeout == -1, "the minimum time cannot be greater than the timeout!");

            var stateFunction = this.stateFunction;
            if (inWorldProperties != null)
                stateFunction = inWorldProperties.modify(stateFunction);

            return new ContinuousRumbleEffect(stateFunction, priority, timeout, minTime);
        }

        private record InWorldProperties(Vec3 sourceLocation, float minMagnitude, float maxMagnitude, float minDistance, float maxDistance, Function<Float, Float> fallofFunction) {
            private Function<Integer, RumbleState> modify(Function<Integer, RumbleState> stateFunction) {
                return tick -> {
                    float distanceSqr = (float) Mth.clamp(Minecraft.getInstance().player.distanceToSqr(sourceLocation), minDistance, maxDistance);
                    float magnitude = Mth.lerp(1f - fallofFunction.apply(distanceSqr / (maxDistance * maxDistance)), minMagnitude, maxMagnitude);
                    return stateFunction.apply(tick).mul(magnitude);
                };
            }
        }
    }
}
