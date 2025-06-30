package com.jessy_barthelemy.strongify.interfaces;

import com.jessy_barthelemy.strongify.helper.TimeSpan;

public interface TimeSpanManager {
    void onTimeSpanChanged(TimeSpan timeSpan, int sourceId);
}
