package com.seasun.management.dto;

import java.util.List;

/**
 * Created by hedahai on 2017/8/7.
 */
public class FnPlatShareUserCountListDto {
    private List<FnPlatShareUserCountDto> fnPlatShareUserCountDtoList;

    public List<FnPlatShareUserCountDto> getFnPlatShareUserCountDtoList() {
        return fnPlatShareUserCountDtoList;
    }

    public void setFnPlatShareUserCountDtoList(List<FnPlatShareUserCountDto> fnPlatShareUserCountDtoList) {
        this.fnPlatShareUserCountDtoList = fnPlatShareUserCountDtoList;
    }

    public FnPlatShareUserCountDto getFnPlatShareUserCount(Integer year, Integer month) {
        if (null != fnPlatShareUserCountDtoList) {
            for (FnPlatShareUserCountDto fnPlatShareUserCountDto : fnPlatShareUserCountDtoList) {
                if (year.equals(fnPlatShareUserCountDto.getYear()) && month.equals(fnPlatShareUserCountDto.getMonth())) {
                    return fnPlatShareUserCountDto;
                }
            }
        }
        return null;
    }
}
