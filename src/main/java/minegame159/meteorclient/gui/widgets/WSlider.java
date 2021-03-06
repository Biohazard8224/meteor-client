package minegame159.meteorclient.gui.widgets;

import minegame159.meteorclient.gui.GuiConfig;
import minegame159.meteorclient.gui.renderer.GuiRenderer;
import minegame159.meteorclient.gui.renderer.Region;
import minegame159.meteorclient.utils.Color;
import minegame159.meteorclient.utils.Utils;

import java.util.function.Consumer;

public class WSlider extends WWidget {
    private static final double HANDLE_SIZE = 15;

    public Consumer<WSlider> action;

    public double value;

    private final double min, max;
    private final double uWidth;

    private boolean handleMouseOver;
    private boolean dragging;
    private double lastMouseX;

    public WSlider(double value, double min, double max, double width) {
        this.min = min;
        this.max = max;
        this.uWidth = width;
        this.value = value;
    }

    @Override
    protected void onCalculateSize(GuiRenderer renderer) {
        width = uWidth;
        height = HANDLE_SIZE;
    }

    @Override
    protected boolean onMouseClicked(boolean used, int button) {
        if (used) return false;

        if (mouseOver) {
            double valueWidth = lastMouseX - (x + HANDLE_SIZE/2);
            value = (valueWidth / (width - HANDLE_SIZE)) * (max - min) + min;
            if (action != null) action.accept(this);

            dragging = true;
            return true;
        }

        return false;
    }

    @Override
    protected boolean onMouseReleased(boolean used, int button) {
        dragging = false;
        return mouseOver && !used;
    }

    @Override
    protected void onMouseMoved(double mouseX, double mouseY) {
        double valuePercentage = (value - min) / (max - min);
        double valueWidth = valuePercentage * (width - HANDLE_SIZE);

        double x = this.x + HANDLE_SIZE/2 + valueWidth - height / 2;
        handleMouseOver =  mouseX >= x && mouseX <= x + height && mouseY >= y && mouseY <= y + height;

        boolean mouseOverX = mouseX >= this.x + HANDLE_SIZE/2 && mouseX <= this.x + HANDLE_SIZE/2 + width - HANDLE_SIZE;
        mouseOver = mouseOverX && mouseY >= this.y && mouseY <= this.y + height;

        if (dragging) {
            if (mouseOverX) {
                valueWidth += mouseX - lastMouseX;
                valueWidth = Utils.clamp(valueWidth, 0, width - HANDLE_SIZE);

                value = (valueWidth / (width - HANDLE_SIZE)) * (max - min) + min;
                if (action != null) action.accept(this);
            } else {
                if (value > min && mouseX < this.x + HANDLE_SIZE/2) {
                    value = min;
                    if (action != null) action.accept(this);
                } else if (value < max && mouseX > this.x + HANDLE_SIZE/2 + width - HANDLE_SIZE) {
                    value = max;
                    if (action != null) action.accept(this);
                }
            }
        }

        lastMouseX = mouseX;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        value = Utils.clamp(value, min, max);
        double valuePercentage = (value - min) / (max - min);
        double valueWidth = valuePercentage * (width - HANDLE_SIZE);

        renderer.quad(Region.FULL, x + HANDLE_SIZE/2, y + 6, valueWidth, 3, GuiConfig.INSTANCE.sliderLeft);
        renderer.quad(Region.FULL, x + HANDLE_SIZE/2 + valueWidth, y + 6, width - valueWidth - HANDLE_SIZE, 3, GuiConfig.INSTANCE.sliderRight);

        Color handleColor;
        if (dragging) handleColor = GuiConfig.INSTANCE.sliderHandlePressed;
        else if (handleMouseOver) handleColor = GuiConfig.INSTANCE.sliderHandleHovered;
        else handleColor = GuiConfig.INSTANCE.sliderHandle;

        renderer.quad(Region.CIRCLE, x + valueWidth, y, HANDLE_SIZE, HANDLE_SIZE, handleColor);
    }
}
