package com.seasun.management.service.impl;

import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.FnProjectStatDataDetailMapper;
import com.seasun.management.mapper.FnProjectStatDataMapper;
import com.seasun.management.mapper.FnStatMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.model.FnStat;
import com.seasun.management.service.FnProjectStatDataService;
import com.seasun.management.vo.FnProjectStatDataVo;
import com.seasun.management.vo.FnProjectStatDetailDataVo;
import com.seasun.management.vo.ProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FnProjectStatDataServiceImpl implements FnProjectStatDataService {
    @Autowired
    private FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private FnStatMapper fnStatMapper;

    @Autowired
    private FnProjectStatDataDetailMapper fnProjectStatDataDetailMapper;

    @Override
    public List<FnProjectStatDataVo> getFnProjectStatDataVosByCondition(FnProjectStatData dataCondition) {
        List<FnProjectStatDataVo> fnProjectStatDataVos = new ArrayList<>();

        List<FnProjectStatData> fnProjectStatDatas = fnProjectStatDataMapper.selectByCondition(dataCondition);
        List<ProjectVo> fnProjects = projectMapper.selectAllFNProject();

        for (ProjectVo project : fnProjects) {
            FnProjectStatDataVo fnProjectStatDataVo = findFnProjectStatDataVoByProjectId(fnProjectStatDataVos, project.getId());
            if (null == fnProjectStatDataVo) {
                fnProjectStatDataVo = new FnProjectStatDataVo();
                fnProjectStatDataVo.setProjectId(project.getId());
                fnProjectStatDataVo.setProjectName(project.getName());
            }

            for (int month = 0; month < 12; month++) {
                FnProjectStatData data = null;
                for (FnProjectStatData fnProjectStatData : fnProjectStatDatas) {
                    if (fnProjectStatData.getProjectId().equals(project.getId()) && fnProjectStatData.getMonth().equals(month + 1)) {
                        data = fnProjectStatData;
                        break;
                    }
                }
                if (null != data) {
                    fnProjectStatDataVo.getData().add(data);
                } else {
                    data = new FnProjectStatData();
                    data.setYear(dataCondition.getYear());
                    data.setProjectId(project.getId());
                    data.setMonth(month + 1);
                    data.setFnStatId(ReportHelper.OUTSOURCING_STAT_ID);

                    fnProjectStatDataVo.getData().add(data);
                }
            }

            fnProjectStatDataVos.add(fnProjectStatDataVo);
        }

        return fnProjectStatDataVos;
    }

    private FnProjectStatDataVo findFnProjectStatDataVoByProjectId(List<FnProjectStatDataVo> fnProjectStatDataVos, Long projectId) {
        FnProjectStatDataVo fnProjectStatDataVo = null;

        for (FnProjectStatDataVo data : fnProjectStatDataVos) {
            if (data.getProjectId().equals(projectId)) {
                fnProjectStatDataVo = data;
                break;
            }
        }

        return fnProjectStatDataVo;
    }

    @Override
    public void addProjectStatData(FnProjectStatData fnProjectStatData) {
        fnProjectStatData.setFnStatId(ReportHelper.OUTSOURCING_STAT_ID);
        fnProjectStatDataMapper.insert(fnProjectStatData);
    }

    @Override
    public void updateProjectStatData(FnProjectStatData fnProjectStatData) {
        fnProjectStatData.setFnStatId(ReportHelper.OUTSOURCING_STAT_ID);
        fnProjectStatDataMapper.updateByPrimaryKeySelective(fnProjectStatData);
    }

    @Override
    public void batchUpdateProjectStatData(List<FnProjectStatData> fnProjectStatData) {
        // 内存中分类
        List<FnProjectStatData> batchInsertData = new ArrayList<>();
        List<FnProjectStatData> batchUpdateData = new ArrayList<>();
        for (FnProjectStatData item : fnProjectStatData) {
            if (item.getId() == null) {
                batchInsertData.add(item);
            } else {
                batchUpdateData.add(item);
            }
        }

        // 批量处理
        if (batchInsertData.size() > 0) {
            fnProjectStatDataMapper.batchInsert(batchInsertData);
        }
        if (batchUpdateData.size() > 0) {
            fnProjectStatDataMapper.batchUpdateByPks(batchUpdateData);
        }

    }

    @Override
    public List<FnProjectStatDetailDataVo> getFnProjectStatMapDataVosByCondition(FnProjectStatData fnProjectStatData) {
        List<FnProjectStatDetailDataVo> dataRet = new ArrayList<>();
        List<FnProjectStatData> fnProjectStatDataList = fnProjectStatDataMapper.selectByCondition(fnProjectStatData);
        List<FnStat> fnStats = fnStatMapper.selectStatByProjectIdOrFixed(fnProjectStatData.getProjectId());
        List<FnProjectStatDataDetail> statDetailByProjectIdAndYear = fnProjectStatDataDetailMapper.getStatDetailByProjectIdAndYear(fnProjectStatData.getProjectId(), fnProjectStatData.getYear());
        for (FnStat fnStat : fnStats) {
            if (null == fnStat.getParentId()) {
                FnProjectStatDetailDataVo data = new FnProjectStatDetailDataVo();
                data.setStatId(fnStat.getId());
                data.setStatName(fnStat.getName());
                FnProjectStatData sumStatData = findFnProjectStatDataByCond(fnProjectStatDataList, fnStat.getId(), fnProjectStatData.getYear(), 0);
                data.setTotal(sumStatData == null ? 0f : sumStatData.getValue());

                for (Integer month = 1; month <= 12; month++) {
                    FnProjectStatDetailDataVo.ValueVo valueVo = new FnProjectStatDetailDataVo.ValueVo();
                    FnProjectStatData monthStatData = findFnProjectStatDataByCond(fnProjectStatDataList, fnStat.getId(), fnProjectStatData.getYear(), month);
                    if (monthStatData == null) {
                        valueVo.setDetailFlag(false);
                        valueVo.setValue(0f);
                    } else {
                        valueVo.setDetailFlag(monthStatData.getDetailFlag() == null ? false : monthStatData.getDetailFlag());
                        valueVo.setValue(monthStatData.getValue());
                    }
                    data.getValues().add(valueVo);
                }

                for (FnStat fnStatChild : fnStats) {
                    if (null != fnStatChild.getParentId() && fnStatChild.getParentId().equals(fnStat.getId())) {
                        FnProjectStatDetailDataVo dataChild = new FnProjectStatDetailDataVo();
                        dataChild.setStatId(fnStatChild.getId());
                        dataChild.setStatName(fnStatChild.getName());
                        FnProjectStatData childSumStatData = findFnProjectStatDataByCond(fnProjectStatDataList, fnStatChild.getId(), fnProjectStatData.getYear(), 0);
                        dataChild.setTotal(childSumStatData == null ? 0f : childSumStatData.getValue());

                        for (Integer month = 1; month <= 12; month++) {
                            FnProjectStatDetailDataVo.ValueVo valueVo = new FnProjectStatDetailDataVo.ValueVo();
                            FnProjectStatData childMonthStatData = findFnProjectStatDataByCond(fnProjectStatDataList, fnStatChild.getId(), fnProjectStatData.getYear(), month);
                            if (childMonthStatData == null) {
                                valueVo.setDetailFlag(false);
                                valueVo.setValue(0f);
                            } else {
                                valueVo.setValue(childMonthStatData.getValue());
                                valueVo.setDetailFlag(childMonthStatData.getDetailFlag() == null ? false : childMonthStatData.getDetailFlag());
                            }
                            dataChild.getValues().add(valueVo);
                        }

                        data.getChildren().add(dataChild);
                    }
                }

                dataRet.add(data);
            }
        }

        return dataRet;
    }

    //检查该项目费用是否含有费用明细
    private boolean checkIsContain(Long statId, Integer month, List<FnProjectStatDataDetail> list) {
        if (null != list && list.size() > 0 && null != statId) {
            for (FnProjectStatDataDetail item : list) {
                if (item.getStatId().equals(statId) && item.getMonth().equals(month)) {
                    return true;
                }
            }
        }
        return false;
    }

    private FnProjectStatData findFnProjectStatDataByCond(List<FnProjectStatData> datas, Long statId, Integer year, Integer month) {
        FnProjectStatData result = null;

        for (FnProjectStatData item : datas) {
            if (item.getFnStatId().equals(statId) && item.getYear().equals(year) && item.getMonth().equals(month)) {
                result = item;
                break;
            }
        }
        return result;
    }
}
