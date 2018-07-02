package com.example.exp.sleep.View;

/**
 * The TestView interface provides four funtions to  update the data.
 */

public interface TestView {
    void addPoint2(Double x, Double y);
    void setLux(Float lux);
    void invalidate(int mode);
    boolean post(Runnable runnable);
}
