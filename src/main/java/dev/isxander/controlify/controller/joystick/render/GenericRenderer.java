package dev.isxander.controlify.controller.joystick.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.controlify.bindings.JoystickAxisBind;
import dev.isxander.controlify.controller.joystick.JoystickState;
import dev.isxander.controlify.gui.DrawSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public abstract class GenericRenderer implements JoystickRenderer {
    protected final @Nullable String annotation;
    protected final Minecraft minecraft = Minecraft.getInstance();

    public GenericRenderer(@Nullable String annotation) {
        this.annotation = annotation;
    }

    @Override
    public DrawSize render(PoseStack poseStack, int x, int centerY, int size) {
        if (annotation != null) {
            minecraft.font.draw(
                    poseStack,
                    annotation,
                    x + size + 2 - minecraft.font.width(annotation),
                    centerY + size/2f - minecraft.font.lineHeight * 0.75f,
                    -1
            );
        }

        return null;
    }

    public static class Button extends GenericRenderer implements JoystickRenderer.Button {
        private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("controlify", "textures/gui/joystick/generic/button.png");

        public Button(@Nullable String annotation) {
            super(annotation);
        }

        @Override
        public DrawSize render(PoseStack poseStack, int x, int centerY, int size) {
            RenderSystem.setShaderTexture(0, BUTTON_TEXTURE);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            poseStack.pushPose();
            poseStack.translate(x, centerY, 0);

            float scale = (float) size / 22f;
            poseStack.scale(scale, scale, 1);
            poseStack.translate(0f, -DEFAULT_SIZE / scale / 2f, 0);

            GuiComponent.blit(poseStack, 0, 0, 0, 0, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);

            poseStack.popPose();
            
            super.render(poseStack, x, centerY, size);

            return new DrawSize(size, size);
        }

        @Override
        public DrawSize render(PoseStack poseStack, int x, int centerY, int size, boolean down) {
            return this.render(poseStack, x, centerY, size);
        }

        @Override
        public DrawSize render(PoseStack poseStack, int x, int centerY, boolean down) {
            return this.render(poseStack, x, centerY, DEFAULT_SIZE);
        }
    }

    public static class Axis extends GenericRenderer implements JoystickRenderer.Axis {
        private static final ResourceLocation AXIS_TEXTURE = new ResourceLocation("controlify", "textures/gui/joystick/generic/axis.png");

        public Axis(@Nullable String annotation) {
            super(annotation);
        }

        @Override
        public DrawSize render(PoseStack poseStack, int x, int centerY, int size, JoystickAxisBind.AxisDirection direction) {
            RenderSystem.setShaderTexture(0, AXIS_TEXTURE);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            poseStack.pushPose();
            poseStack.translate(x, centerY, 0);

            float scale = (float) size / 22f;
            poseStack.scale(scale, scale, 1);
            poseStack.translate(0f, -DEFAULT_SIZE / scale / 2f, 0);

            GuiComponent.blit(
                    poseStack,
                    0, 0,
                    direction.ordinal() * DEFAULT_SIZE, 0,
                    DEFAULT_SIZE, DEFAULT_SIZE,
                    DEFAULT_SIZE * JoystickAxisBind.AxisDirection.values().length, DEFAULT_SIZE
            );

            poseStack.popPose();

            super.render(poseStack, x, centerY, size);

            return new DrawSize(size, size);
        }
    }

    public static class Hat extends GenericRenderer implements JoystickRenderer.Hat {
        private static final ResourceLocation HAT_TEXTURE = new ResourceLocation("controlify", "textures/gui/joystick/generic/hat.png");

        public Hat(@Nullable String annotation) {
            super(annotation);
        }

        @Override
        public DrawSize render(PoseStack poseStack, int x, int centerY, int size, JoystickState.HatState hatState) {
            RenderSystem.setShaderTexture(0, HAT_TEXTURE);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            poseStack.pushPose();
            poseStack.translate(x, centerY, 0);

            float scale = (float) size / 22f;
            poseStack.scale(scale, scale, 1);
            poseStack.translate(0f, -DEFAULT_SIZE / scale / 2f, 0);

            GuiComponent.blit(
                    poseStack,
                    0, 0,
                    hatState.ordinal() * DEFAULT_SIZE, 0,
                    DEFAULT_SIZE, DEFAULT_SIZE,
                    DEFAULT_SIZE * JoystickState.HatState.values().length, DEFAULT_SIZE
            );

            poseStack.popPose();

            super.render(poseStack, x, centerY, size);

            return new DrawSize(size, size);
        }
    }
}
