package dev.isxander.controlify.controller.joystick;

import dev.isxander.controlify.controller.ControllerConfig;
import dev.isxander.controlify.controller.joystick.mapping.JoystickMapping;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;

public class JoystickConfig extends ControllerConfig {
    private Map<String, Float> deadzones;

    private transient JoystickController<?> controller;

    public JoystickConfig(JoystickController<?> controller) {
        Validate.notNull(controller);
        setup(controller);
    }

    @Override
    public void setDeadzone(int axis, float deadzone) {
        if (axis < 0)
            throw new IllegalArgumentException("Axis cannot be negative!");

        deadzones.put(controller.mapping().axes()[axis].identifier(), deadzone);
    }

    @Override
    public float getDeadzone(int axis) {
        if (axis < 0)
            throw new IllegalArgumentException("Axis cannot be negative!");

        return deadzones.getOrDefault(controller.mapping().axes()[axis].identifier(), 0.2f);
    }

    void setup(JoystickController<?> controller) {
        this.controller = controller;
        if (this.deadzones == null) {
            deadzones = new HashMap<>();
            for (int i = 0; i < controller.mapping().axes().length; i++) {
                JoystickMapping.Axis axis = controller.mapping().axes()[i];

                if (axis.requiresDeadzone())
                    deadzones.put(axis.identifier(), 0.2f);
            }
        }
    }
}
