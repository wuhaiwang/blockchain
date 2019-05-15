package com.seasun.management.service;

import com.seasun.management.model.MySalaryKey;
import com.seasun.management.model.UserSalary;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.vo.*;

import java.util.List;

public interface UserSalaryChangeService {

    /**
     * web端：下载调薪记录的接口
     *
     * @param year    指定年份
     * @param quarter 指定季度
     * @param columns 需要的属性列表（新版属性可能有改变）
     * @return
     */
    String downloadSalaryFile(int year, int quarter, String[] columns);

    UserSalary getUserCurrentSalaryInfo(Long userId);

    /**
     * web端：查看下属调薪主界面接口
     *
     * @param workGroupId 组的ID
     * @param year        指定年份
     * @param quarter     指定季度
     * @return 主界面信息
     */
    SubordinateSalaryChangeAppVo getSubSalaryChangeWeb(Long workGroupId, Integer year, Integer quarter);

    //app

    /**
     * app：查看下属调薪主界面接口
     *
     * @param workGroupId 组的ID
     * @param year        指定年份
     * @param quarter     指定季度
     * @return 主界面信息
     */
    SubordinateSalaryChangeAppVo getSubSalaryChange(Long workGroupId, Integer year, Integer quarter);

    /**
     * 查看下属组的所有成员
     *
     * @param workGroupId 下属组的ID
     * @param year
     * @param quarter
     * @param isFinish    此组状态：是不是已经完成
     * @return 此组中所有的人员，组结构打平
     */
    List<OrdinateSalaryChangeAppVo> getMemberSalaryChange(Long workGroupId, Integer year, Integer quarter, Boolean isFinish);

    /**
     * 查看个人的调薪情况
     *
     * @param isHistory
     * @param userId
     * @param year
     * @param quarter
     * @return 个人信息
     */
    IndividualSalaryChangeVo getIndividualDetail(Boolean isHistory, Long userId, Integer year, Integer quarter);

    /**
     * 删选出子团队的可调薪/待淘汰状态
     *
     * @param isFinish
     * @param workGroupId
     * @param status      可调薪/待淘汰
     * @param groupIds
     * @param year
     * @param quarter
     * @return 多个小组成员情况
     */
    List<List<OrdinateSalaryChangeAppVo>> getSubGroupMembers(Boolean isFinish, Long workGroupId, String status, List<Long> groupIds, Integer year, Integer quarter);

    /**
     * 修改个人调薪记录
     *
     * @param vo          前端返回给后端的个人绩效数据结构，（grade和evaluate 如果有修改会传给后端，没有修改，字段传null）
     * @param workGroupId 这个人所属的组
     */
    void modifyOrdinateSalaryChange(IndividualSalaryChangeVo vo, Long workGroupId);

    /**
     * 打回一个组的调薪
     *
     * @param year        指定年份
     * @param quarter     指定季度
     * @param workGroupId 打回小组的workGroupId
     */
    void requestReject(Integer year, Integer quarter, Long workGroupId);

    /**
     * 提交本组调薪
     *
     * @param workGroupId 提交组的workGroupId
     * @param year        指定年份
     * @param quarter     指定季度
     */
    void groupCommit(Long workGroupId, Integer year, Integer quarter);

    /**
     * 代提交一级子团队的调薪
     *
     * @param workGroupId 要提交小组的workGroupId
     * @param year        指定年份
     * @param quarter     指定季度
     */
    void assistSubmit(Long workGroupId, Integer year, Integer quarter);

    /**
     * 老k确认全员的调薪
     *
     * @param year
     * @param quarter
     */
    void leaderFinish(Integer year, Integer quarter);

    /**
     * 查找绩效完成月份（前端调用此接口进行判断，只有当绩效完成已经三个月，说明调薪才开始）
     *
     * @param year    指定年份
     * @param quarter 指定季度
     * @return 绩效完成了几个月 eg:（1/2/3）
     */
    int performanceMonthCount(Integer year, Integer quarter);

    /**
     * 取得此组所有的历史的提交状态
     *
     * @param workGroupId 组的workGroupId
     * @return 组的历史状态，eg:{...{2017,1,已完成},{2017,2,进行中}...}
     */
    List<SubordinateSalaryChangeAppVo.GroupStatus> getAllTimeGroupStatus(Long workGroupId);

//
//    List<UserSalaryChange> cloneDateObjectPerformanceTest();


    void receiveSalaryKey(MySalaryKey mySalaryKey);
}
