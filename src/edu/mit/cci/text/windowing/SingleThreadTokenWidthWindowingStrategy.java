package edu.mit.cci.text.windowing;

import java.util.List;

/**
 * Created by josh on 1/14/14.
 */
public class SingleThreadTokenWidthWindowingStrategy extends PrecomputedTimeWindowStrategy {


    public SingleThreadTokenWidthWindowingStrategy(List<Windowable> current, int numTokensWidth, int numTokensDelta) {
       super(WindowingUtils.analyzeSingleThreadBySize(current, numTokensWidth, numTokensDelta));
    }
}
