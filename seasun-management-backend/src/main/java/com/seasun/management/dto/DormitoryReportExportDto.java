package com.seasun.management.dto;

import java.util.List;
import java.util.Map;

/**
 * 分摊报表导出
 */
public class DormitoryReportExportDto {
    /**
     * 入住信息列表
     * 公司	项目	员工宿舍入住人天数	占比
     * 公寓总人数	主卧人天数	主卧占比 次卧人天数	次卧占比 第三间房人天数	第三间房占比
     */
    private List<UserDormitoryReportDto> list;
    /**
     * 床位数参数列表
     * 本月员工宿舍床位数
     * 本月人才公寓主卧数
     * 本月人才公寓次卧数
     * 本月人才公寓第三间房数
     */
    private Map<String,UserDormitoryParamDto> parameters;
    /**
     * 当月天数
     */
    private Integer mDays;

    private Integer allDormitoryRoom;//合计:员工宿舍入住人天数

    private Integer allApartmentRoom;//合计:公寓总人天数
    private Integer aRoom;//合计:公寓主卧人天数
    private Integer bRoom;//合计:公寓次卧人天数
    private Integer cRoom;//合计:公寓第三间房人天数

    public List<UserDormitoryReportDto> getList() {
        return list;
    }

    public void setList(List<UserDormitoryReportDto> list) {
        this.list = list;
    }

    public Integer getmDays() {
        return mDays;
    }

    public void setmDays(Integer mDays) {
        this.mDays = mDays;
    }

    public Integer getAllDormitoryRoom() {
        return allDormitoryRoom;
    }

    public void setAllDormitoryRoom(Integer allDormitoryRoom) {
        this.allDormitoryRoom = allDormitoryRoom;
    }

    public Integer getAllApartmentRoom() {
        return allApartmentRoom;
    }

    public void setAllApartmentRoom(Integer allApartmentRoom) {
        this.allApartmentRoom = allApartmentRoom;
    }

    public Integer getaRoom() {
        return aRoom;
    }

    public void setaRoom(Integer aRoom) {
        this.aRoom = aRoom;
    }

    public Integer getbRoom() {
        return bRoom;
    }

    public void setbRoom(Integer bRoom) {
        this.bRoom = bRoom;
    }

    public Integer getcRoom() {
        return cRoom;
    }

    public void setcRoom(Integer cRoom) {
        this.cRoom = cRoom;
    }

    public Map<String, UserDormitoryParamDto> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, UserDormitoryParamDto> parameters) {
        this.parameters = parameters;
    }
}
