
package com.seasun.management.service.performanceCore.historyTrack;

import java.util.List;

public interface HistoryTrackService<T> {

    List<T> getAllHistoryMembersByWorkGroupIdAndTime(Long workGroupId, int year, int month, int quarter);

}