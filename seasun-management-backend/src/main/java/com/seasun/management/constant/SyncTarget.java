package com.seasun.management.constant;

public enum SyncTarget {
    user, user_work_group, user_children_info, user_edu_experience, user_work_experience, user_certification, user_bank_card, user_favorite, user_nda, department, r_user_project_perm,
    r_user_work_group_perm, project, project_role, batch, floor, subcompany, user_floor, work_group, user_transfer_post, user_performance_work_group;

    @Override
    public String toString() {
        return super.toString().replace("_", "-");
    }
}
