package com.seasun.management.service;

import com.seasun.management.model.CostCenter;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Subcompany;

import java.util.List;

public interface SubCompanyService {

    List<Subcompany> getSubcompanys();

    void updateSubcompany(Subcompany subcompany);

    void deleteSubcompanyById(Long id);

    void addSubcompany(Subcompany subcompany);

    List<IdNameBaseObject> getSimpleSubcompanys();

    List<CostCenter> getCostCenters();

    void updateCostCenters(CostCenter costCenter);

    void addCostCenters(CostCenter costCenter);
}
