package com.seasun.management.service;

import com.seasun.management.dto.cp.ExchangeRateDto;
import com.seasun.management.model.cp.OrdissuesEx;
import com.seasun.management.vo.BaseQueryResponseVo;
import com.seasun.management.vo.cp.CpBudgetAppendInfoVo;
import com.seasun.management.vo.cp.ProjectBudgetInfo;
import com.seasun.management.vo.cp.ProjectBudgetVo;
import com.seasun.management.vo.cp.ProjectOutcourceInfoVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CpInfoService {

    BaseQueryResponseVo getOutsourcers(Integer year, Integer currentPage, Integer pageSize, List<Integer> grade, String makingType, String styleType, String sortField);

    BaseQueryResponseVo getProjectInfos(Integer year, Integer currentPage, Integer pageSize,String sortField);

    /**
     * 获取制作类型和风格
     *
     * @return
     */
    public Map<String, List<String>> getSupport();


    /**
     * @param cpProjectId
     * @param itProjectId
     */
    public int createProjectRelation(Long cpProjectId, Long itProjectId);

    public int createProjectBudget(Long cpProjectId, BigDecimal amount, Integer year);

    public int appendBudget(Long cpBudgetId, List<CpBudgetAppendInfoVo> list);

    public ProjectBudgetVo getBudgetAmounts(Long cpProjectId, Integer year);

    public BaseQueryResponseVo projects(Integer currentPage, Integer pageSize, Integer year,  String keyWord);

    ProjectBudgetInfo getSeasunBudgetInfo(Integer year);

    ProjectOutcourceInfoVo getProjectInfo(Long id, Integer year, Integer quarter, Integer month, Integer currentPage, Integer pageSize);

    Map<String, BigDecimal> getRate();

    BigDecimal getOrdissueExAmountOfMoney(OrdissuesEx ordissuesEx, BigDecimal rate);

    public int updateCpBudgetAmount(Long cpBudgetId, BigDecimal amount);

    public List<ExchangeRateDto> getExchangeRates();

    public Long addExchangeRate(ExchangeRateDto rate);

    public int updateExchangeRate(Long id, BigDecimal rate);

    public int deleteExchangeRate(Long id);
    public Map<String,BigDecimal> getProjectInfo(String projectName,String date) ;
}

