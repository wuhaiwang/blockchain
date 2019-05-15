package com.seasun.management.vo;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ContactsAppVo {
    private Long workGroupId;

    private String workGroupName;

    private Integer total;
    
    private Integer internTotal;

    private List<Group> currentGroups;

    private List<OrgWorkGroupMemberAppVo> members;

    private List<Group> allGroups;

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
    
    public Integer getInternTotal() {
		return internTotal;
	}
    
    public void setInternTotal(Integer internTotal) {
		this.internTotal = internTotal;
	}

    public List<Group> getCurrentGroups() {
    		if(currentGroups != null) {
    			currentGroups.sort(new Comparator<Group>() {
    				@Override
    				public int compare(Group o1, Group o2) {
    					return Collator.getInstance(Locale.CHINA).compare(o1.getName(), o2.getName());
    				}
    			});
    		}
        return currentGroups;
    }

    public void setCurrentGroups(List<Group> currentGroups) {
        this.currentGroups = currentGroups;
    }

    public List<OrgWorkGroupMemberAppVo> getMembers() {
    		if(members != null) {
        		members.sort(new Comparator<OrgWorkGroupMemberAppVo>() {
        			@Override
        			public int compare(OrgWorkGroupMemberAppVo o1, OrgWorkGroupMemberAppVo o2) {
        				if (o1.getLeaderFlag() != null && o2.getLeaderFlag() != null && Boolean.logicalXor(o1.getLeaderFlag(), o2.getLeaderFlag())) {
        					return o1.getLeaderFlag() ? -1 : 1;
        				} else {
        					return o1.getLoginId().compareTo(o2.getLoginId());
        				}
        			}
        		});
    		}
        return members;
    }

    public void setMembers(List<OrgWorkGroupMemberAppVo> members) {
        this.members = members;
    }

    public List<Group> getAllGroups() {
        return allGroups;
    }

    public void setAllGroups(List<Group> allGroups) {
        this.allGroups = allGroups;
    }

    public static class Group {
        private Long groupId;

        private String name;

        private Integer total;
        
        private Integer internTotal;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
        
        public Integer getInternTotal() {
			return internTotal;
		}
        
        public void setInternTotal(Integer internTotal) {
			this.internTotal = internTotal;
		}
    }
}
