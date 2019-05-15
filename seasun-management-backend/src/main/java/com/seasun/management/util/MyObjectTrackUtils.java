package com.seasun.management.util;

import com.seasun.management.dto.BaseParentDto;
import com.seasun.management.model.PerformanceWorkGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyObjectTrackUtils<T extends BaseParentDto> {

    public List<T> getAllChildrenByParentId(Long parentId, List<T> all) {
        T root = all.stream()
                .filter(p -> p.getId().equals(parentId)).collect(Collectors.toList()).get(0);

        List<T> children = new ArrayList<>();
        fillChildren(children, root, all);
        return children;
    }

    private void fillChildren(List<T> children, T parent, List<T> all) {
        all.forEach(p -> {
            if (parent.getId().equals(p.getParent())) {
                children.add(p);
                fillChildren(children, p, all);
            }
        });
    }
}
