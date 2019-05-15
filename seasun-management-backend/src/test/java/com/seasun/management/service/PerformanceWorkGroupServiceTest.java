package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.model.UserPerfWorkGroupVo;
import com.seasun.management.vo.PerformanceWorkGroupNodeVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class PerformanceWorkGroupServiceTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PerformanceWorkGroupServiceTest.class);

    @Autowired
    PerformanceWorkGroupService performanceWorkGroupService;

    @Autowired
    UserPerformanceService userPerformanceNewService;

    @Autowired
    WorkGroupMapper workGroupMapper;


    @Test
    public void testGetPerformanceTree() {

        PerformanceWorkGroupNodeVo tree = performanceWorkGroupService.getPerformanceTree();
        printTree(tree, "");
    }

    private void printTree(PerformanceWorkGroupNodeVo tree, String str) {
        str += "--";
        if (tree != null) {
            logger.info(str + tree.getTitle());
        }
        List<PerformanceWorkGroupNodeVo> list = tree.getNodes();
        if (list == null || list.size() == 0) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            printTree(list.get(i), str);
        }
    }


    @Test
    public void testAddSubPerformanceWorkGroup() {
        Long performanceWorkGroupId = 9999L;
        Long subGroupId = 7777L;
        Long managerId = 3244L;
        int strictType = 2;
        String newName = null;
        Long res = performanceWorkGroupService.addSubPerformanceWorkGroup(performanceWorkGroupId, subGroupId, managerId, strictType, null);
        logger.info(res + "");
    }

    @Test
    public void testConfirmPerformance() {
        userPerformanceNewService.confirmPerformance(2017, 1);
        userPerformanceNewService.confirmPerformance(2017, 2);
        userPerformanceNewService.confirmPerformance(2017, 3);
    }

    @Test
    public void testDelete() {
        performanceWorkGroupService.deleteSubPerformanceGroup(12L);
    }

    @Test
    public void getUsers() {
        List<UserPerfWorkGroupVo> users = performanceWorkGroupService.getUsers("chengbin");
        System.out.println(users.get(0));
        System.out.println("000");
    }

    @Test
    public void getPerformanceTree() {
        performanceWorkGroupService.getPerformanceTree();
    }

    @Test
    public void compareStringGroup() {
        List<String> tst = workGroupMapper.test();

        String res2 = "1030,1108,1109,1033,1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,152,919,954,964,969,972,973,974,975,977,981,982,983,984,985,986,987,988,989,992,993,994,995,996,997,998,999,1000,1001,1002,1004,548,702,814,966,1110,1112,1044,1045,1046,1047,1048,1049,1050,1051,1052,1053,1054,1055,1056,1057,1058,1059,1060,1061,1062,1063,1064,1065,1066,1067,1068,1069,1070,1071,1072,1073,1074,139,179,221,626,268,340,965,345,492,537,595,597,970,971,290,301,304,305,491,570,689,939,945,946,657,658,664,682,696,830,944,768,783,786,787,789,616,623,624,629,630,976,521,522,523,563,766,901,902,903,904,905,550,551,552,561,650,369,403,412,828,1008,717,767,925,1009,665,666,697,264,895,898,899,961,962,963,387,801,833,935,955,670,712,781,967,102,269,274,105,107,109,110,112,113,114,143,158,165,192,388,660,1115,276,360,437,466,506,117,375,407,444,473,132,141,186,280,445,376,396,937,938,1006,932,933,810,811,979,980,792,793,794,892,769,770,771,772,773,774,775,776,777,778,779,959,1005,692,694,797,940,956,1099,1100,1101,1102,1103,1104,1105,1094,1095,1096,1097,1098,1092,1093,1117,1118,1119,1120,1106,1107,1075,1076,1077,1078,1079,1080,1084,1085,1086,1087,1088,1089,1090,1091,1081,1082,1083,931,101,533,536,1012,1013,1014,1015,1016,1017,1018,1010,1011,273,283,285,287,417,91,93,257,258,259,260,302,430,791,1019,300,632,681,649,948,949,950,96,97,99,307,680,782,1007,804,914,678,690,915,917,918,701,802,908,941,942,943,663,913,951,952,953,267";
        Set<String> list1 = new HashSet<>(tst);
        Set<String> list2 = new HashSet<>(Arrays.asList(res2.split(",")));
        StringBuilder resultR1 = new StringBuilder();
        StringBuilder resultR2 = new StringBuilder();
        for (String x : list1) {
            if (!list2.contains(x)) {
                resultR1.append(x);
                resultR1.append(",");
            }
        }
        for (String x : list2) {
            if (!list1.contains(x)) {
                resultR2.append(x);
                resultR2.append(",");
            }
        }
        System.out.println(resultR1.toString());
        System.out.println("---------------------");
        System.out.println(resultR2.toString());
    }
}
