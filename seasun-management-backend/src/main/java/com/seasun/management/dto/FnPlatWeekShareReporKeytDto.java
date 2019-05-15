package com.seasun.management.dto;

public class FnPlatWeekShareReporKeytDto {
    private int index;
    private Long platId;
    private String sheetName;

    public FnPlatWeekShareReporKeytDto(){

    }
    public FnPlatWeekShareReporKeytDto(int index, Long platId, String sheetName) {
        this.index = index;
        this.platId = platId;
        this.sheetName = sheetName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
