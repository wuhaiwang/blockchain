package com.seasun.management.vo;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class PerformanceFMConfirmVo {

    public static class PermanentConfirmSolution {
        private Long userId;
        private Long fromProject;
        private Long toProject;
        private Integer solution;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Integer getSolution() {
            return solution;
        }

        public void setSolution(Integer solution) {
            this.solution = solution;
        }

        public Long getFromProject() {
            return fromProject;
        }

        public void setFromProject(Long fromProject) {
            this.fromProject = fromProject;
        }

        public Long getToProject() {
            return toProject;
        }

        public void setToProject(Long toProject) {
            this.toProject = toProject;
        }
    }

    private List<PermanentConfirmSolution> removedMembers;
    private List<PermanentConfirmSolution> changedMembers;
    private String fileName;
    private Long platId;


    public List<PermanentConfirmSolution> getRemovedMembers() {
        return removedMembers;
    }

    public void setRemovedMembers(List<PermanentConfirmSolution> removedMembers) {
        this.removedMembers = removedMembers;
    }

    public List<PermanentConfirmSolution> getChangedMembers() {
        return changedMembers;
    }

    public void setChangedMembers(List<PermanentConfirmSolution> changedMembers) {
        this.changedMembers = changedMembers;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
