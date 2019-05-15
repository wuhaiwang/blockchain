package com.seasun.management.flow;

public interface BusinessListener {

    /**
     * 处理"开始"业务逻辑
     *
     * @param flowInfo
     * @return 返回指定的下一步子节点信息。若无需指定，则返回 new NextStepInfo();
     */
    NextStepInfo onInit(FlowInfo flowInfo);

    /**
     * 处理"完成"业务逻辑
     *
     * @param flowInfo
     * @return 返回指定的下一步子节点信息。若无需指定，则返回 new NextStepInfo();
     */
    NextStepInfo onComplete(FlowInfo flowInfo);

    /**
     * 处理"中断"业务逻辑
     *
     * @param flowInfo
     */
    void onReject(FlowInfo flowInfo);

    /**
     * 处理"打回并继续"业务逻辑
     *
     * @param flowInfo
     * @return 返回指定的下一步子节点信息。必须指定，不允许为空对象;
     */
    NextStepInfo onRejectAndRollback(FlowInfo flowInfo);

}
