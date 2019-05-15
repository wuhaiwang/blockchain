package com.seasun.management.service.impl;

import com.seasun.management.mapper.FnProjectSchedDataMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FnProjectSchedData;
import com.seasun.management.service.FnProjectSchedDataService;
import com.seasun.management.vo.FnProjectSchedDataVo;
import com.seasun.management.vo.ProjectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FnProjectSchedDataServiceImpl implements FnProjectSchedDataService {
    @Autowired
    private FnProjectSchedDataMapper fnProjectSchedDataMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Override
    public List<FnProjectSchedDataVo> getFnProjectSchedDataByCondition(FnProjectSchedData dataCondition) {
        List<FnProjectSchedDataVo> fnProjectSchedDataVos = new ArrayList<>();

        List<FnProjectSchedData> fnProjectSchedDatas = fnProjectSchedDataMapper.selectByCondition(dataCondition);

        List<ProjectVo> projectFnSched = projectMapper.selectAllFNProject();

        for (ProjectVo project : projectFnSched) {
            FnProjectSchedDataVo fnProjectSchedDataVo = findFnProjectSchedDataFromListWithProjectId(fnProjectSchedDataVos, project.getId());
            if (null != fnProjectSchedDataVo) {
                for (int month = 0; month < 12; month++) {
                    FnProjectSchedData data = null;
                    for (FnProjectSchedData fnProjectSchedData : fnProjectSchedDatas) {
                        if (fnProjectSchedData.getProjectId().equals(project.getId()) && fnProjectSchedData.getMonth().equals(month + 1)) {
                            data = fnProjectSchedData;
                            break;
                        }
                    }
                    if (null != data) {
                        fnProjectSchedDataVo.getData().add(data);
                    } else {
                        data = new FnProjectSchedData();
                        data.setYear(dataCondition.getYear());
                        data.setProjectId(project.getId());
                        data.setMonth(month + 1);

                        fnProjectSchedDataVo.getData().add(data);
                    }
                }
            } else {
                fnProjectSchedDataVo = new FnProjectSchedDataVo();
                fnProjectSchedDataVo.setProjectId(project.getId());
                fnProjectSchedDataVo.setProjectName(project.getName());
                fnProjectSchedDataVo.setCity(project.getCity());
                fnProjectSchedDataVo.setSetupDate(project.getCreateTime());

                // todo hedahai:重复代码,和上面分支重复
                for (int month = 0; month < 12; month++) {
                    FnProjectSchedData data = null;
                    for (FnProjectSchedData fnProjectSchedData : fnProjectSchedDatas) {
                        if (fnProjectSchedData.getProjectId().equals(project.getId()) && fnProjectSchedData.getMonth().equals(month + 1)) {
                            data = fnProjectSchedData;
                            break;
                        }
                    }
                    if (null != data) {
                        fnProjectSchedDataVo.getData().add(data);
                    } else {
                        data = new FnProjectSchedData();
                        data.setYear(dataCondition.getYear());
                        data.setProjectId(project.getId());
                        data.setMonth(month + 1);

                        fnProjectSchedDataVo.getData().add(data);
                    }
                }

                fnProjectSchedDataVos.add(fnProjectSchedDataVo);
            }
        }

        return fnProjectSchedDataVos;
    }

    private FnProjectSchedDataVo findFnProjectSchedDataFromListWithProjectId(List<FnProjectSchedDataVo> fnProjectSchedDatas, Long projectId) {
        FnProjectSchedDataVo fnProjectSchedDataVo = null;
        for (FnProjectSchedDataVo data : fnProjectSchedDatas) {
            if (data.getProjectId().equals(projectId)) {
                fnProjectSchedDataVo = data;
                break;
            }
        }
        return fnProjectSchedDataVo;
    }

    @Override
    public void batchUpdateProjectSchedData(List<FnProjectSchedData> fnProjectSchedData) {
        // 内存中分类
        List<FnProjectSchedData> batchInsertData = new ArrayList<>();
        List<FnProjectSchedData> batchUpdateData = new ArrayList<>();
        for (FnProjectSchedData item : fnProjectSchedData) {
            if (item.getId() == null) {
                batchInsertData.add(item);
            } else {
                batchUpdateData.add(item);
            }
        }

        // 批量处理
        if (batchInsertData.size() > 0) {
            fnProjectSchedDataMapper.batchInsert(batchInsertData);
        }
        if (batchUpdateData.size() > 0) {
            fnProjectSchedDataMapper.batchUpdateByPks(batchUpdateData);
        }
    }

    @Override
    public void updateProjectSchedData(FnProjectSchedData fnProjectSchedData) {
        fnProjectSchedDataMapper.updateByPrimaryKeySelective(fnProjectSchedData);
    }
}
