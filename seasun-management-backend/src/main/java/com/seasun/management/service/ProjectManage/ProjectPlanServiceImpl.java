package com.seasun.management.service.ProjectManage;

import com.seasun.management.mapper.PmMilestoneMapper;
import com.seasun.management.model.PmMilestone;
import com.seasun.management.service.ProjectPlanService;
import com.seasun.management.util.MyDateUtils;
import com.seasun.management.vo.AppAnnualPlanVo;
import com.seasun.management.vo.PMilestoneDto;
import com.seasun.management.vo.PMilestoneRequestDto;
import com.seasun.management.vo.WebAnnualPlanVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProjectPlanServiceImpl implements ProjectPlanService {

    @Autowired
    private PmMilestoneMapper pmMilestoneMapper;
    @Override
    public AppAnnualPlanVo getAppAnnulPlan() {
        AppAnnualPlanVo result = new AppAnnualPlanVo();
        List<PMilestoneDto> pMilestoneDtoList = pmMilestoneMapper.getAllPmMilestone();
        if (pMilestoneDtoList == null || pMilestoneDtoList.size() == 0) {
            return result;
        }
        Date lastDate = pMilestoneDtoList.get(pMilestoneDtoList.size() - 1).getMilestoneDay();
        int lastYear = MyDateUtils.getYearInteger(lastDate);

        // App 端只能看到发布的里程碑,有记录的N年和N-1年
        int[] showYears = {lastYear,lastYear-1};
        result.setAnnualPlans(collectMilestones(splitMilestones(pMilestoneDtoList, showYears)));
        result.setLastUpdateTime(pmMilestoneMapper.selectLastUpdateTime());
        return result;
    }

    // 根据 里程碑日期 (在showYears中) 分组为 Map
    private Map<String, List<PMilestoneDto>> splitMilestones(List<PMilestoneDto> pMilestoneDtoList, int[] showYears) {
        Map<String, List<PMilestoneDto>> result = new LinkedHashMap<>();
        for (PMilestoneDto dto : pMilestoneDtoList) {

            // App 端只能看到发布的里程碑,有记录的N年和N-1年
            if (dto.getPublishFlag() != null && dto.getPublishFlag()
                    &&
                    (dto.getMilestoneDay().toString().contains(showYears[0] + "")
                    || dto.getMilestoneDay().toString().contains(showYears[1] + ""))
                    ) {
                String dateString = MyDateUtils.formatDateToAppString(dto.getMilestoneDay());
                if (result.containsKey(dateString)) {
                    result.get(dateString).add(dto);
                } else {
                    List<PMilestoneDto> pMilestones = new ArrayList<>();
                    pMilestones.add(dto);
                    result.put(dateString, pMilestones);
                }
            }
        }
        return result;
    }

    // 将 map 组合为 List 返回给 App 端
    private List<AppAnnualPlanVo.AnnualPlan> collectMilestones(Map<String, List<PMilestoneDto>> pMilestoneMap) {
        List<AppAnnualPlanVo.AnnualPlan> result = new ArrayList<>();
        for (Map.Entry<String, List<PMilestoneDto>> entry : pMilestoneMap.entrySet()) {
            AppAnnualPlanVo.AnnualPlan appAnnualPlanVo = new AppAnnualPlanVo.AnnualPlan();
            appAnnualPlanVo.setMilestoneDay(entry.getKey());
            appAnnualPlanVo.setPlanList(entry.getValue());
            result.add(appAnnualPlanVo);
        }
        return result;
    }

    @Override
    public WebAnnualPlanVo getWebAnnualPlan() {
        WebAnnualPlanVo webAnnualPlanVo = new WebAnnualPlanVo();

        List<PMilestoneDto> pMilestoneDtoList = pmMilestoneMapper.getAllPmMilestone();

        Date currentYearFirst = MyDateUtils.getCurrentYearFirst();
        Date afterCurrentYear = MyDateUtils.getAfterCurrentLast(2);

        webAnnualPlanVo.setPlans(pMilestoneDtoList);

        //设置最后更新时间
        webAnnualPlanVo.setLastUpdateTime(pmMilestoneMapper.selectLastUpdateTime());

        return webAnnualPlanVo;
    }

    @Override
    public void deletePMilestone(long id) {
        pmMilestoneMapper.deleteByPrimaryKey(id);
    }

    @Override
    public long addPMilestone(PMilestoneRequestDto vo) {
        PmMilestone pmMilestone = new PmMilestone();
        BeanUtils.copyProperties(vo, pmMilestone);
        pmMilestone.setCreateTime(new Date());
        pmMilestone.setUpdateTime(new Date());
        pmMilestone.setPublishFlag(true);
        pmMilestoneMapper.insertSelective(pmMilestone);
        return pmMilestone.getId();
    }

    @Override
    public long updatePMilestone(PMilestoneRequestDto pMilestoneVo) {
        // 当里程碑已发布时，前端会传递 push_flag 为true, 后端不再判断
        PmMilestone pmMilestoneDB = pmMilestoneMapper.selectByPrimaryKey(pMilestoneVo.getId());
        Long projectId = pmMilestoneDB.getProjectId();
        BeanUtils.copyProperties(pMilestoneVo, pmMilestoneDB);
        pmMilestoneDB.setPublishFlag(true);
        pmMilestoneDB.setProjectId(projectId);
        pmMilestoneDB.setUpdateTime(new Date());
        pmMilestoneMapper.updateByPrimaryKey(pmMilestoneDB);
        return pmMilestoneDB.getId();
    }

    @Override
    @Transactional
    public void releasePMilestone(int releaseYear) {
        Date startDate = MyDateUtils.getYearFirst(releaseYear);
        Date endDate = MyDateUtils.getYearLast(releaseYear + 2);
        pmMilestoneMapper.updateRecordStatus(startDate, endDate);
    }

    /**
     * 查询某节点后的里程碑事件
     *
     * @param startTime 开始日期
     */
    private List<PMilestoneDto> getPmMilestoneByTimeRange(Date startTime, Date endTime, List<PMilestoneDto> pMilestoneDtoList) {
        List<PMilestoneDto> resultList = new ArrayList<>();

        for (PMilestoneDto pMilestoneDto : pMilestoneDtoList) {
            if (pMilestoneDto.getMilestoneDay().getTime() > startTime.getTime() && pMilestoneDto.getMilestoneDay().getTime() < endTime.getTime()) {
                resultList.add(pMilestoneDto);
            }
        }
        return resultList;
    }


}
