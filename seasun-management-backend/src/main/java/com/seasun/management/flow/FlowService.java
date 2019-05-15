package com.seasun.management.flow;

import com.seasun.management.model.FInstance;
import com.seasun.management.model.FInstanceDetail;

import java.util.Map;

public interface FlowService {

    /**
     * 动作：创建
     * 功能：创建 instance 和 instance_detail的第一条记录
     *
     * @param flowName
     * @param businessKey
     * @param ext              透传参数
     * @param businessListener 可空
     */
    FInstance init(String flowName, Long businessKey, Map<Object, Object> ext, BusinessListener businessListener);


    /**
     * 动作：完成
     * 功能：完成某一个流程节点，若存在后续节点，同时创建下一条 instance_detail;
     * 若无后续节点，则直接结束该节点，同时更新f_instance为已完成
     *
     * @param fInstance
     * @param fInstanceDetail
     * @param remark
     * @param ext              透传参数
     * @param businessListener 可空
     */
    void complete(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener);

    /**
     * 动作：中断
     * 功能：中断并结束当前流程
     *
     * @param fInstance
     * @param fInstanceDetail
     * @param remark
     * @param ext              透传参数
     * @param businessListener 可空
     */
    void reject(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener);

    /**
     * 动作：打回并继续
     * 功能：删除fromSeq的instanceDetail,并修改toSeq的instanceDetail为processing
     *
     * @param fInstance
     * @param fInstanceDetail
     * @param remark
     * @param ext              透传参数
     * @param businessListener 可空
     */
    void rejectAndRollback(FInstance fInstance, FInstanceDetail fInstanceDetail, String remark, Map<Object, Object> ext, BusinessListener businessListener);
}
