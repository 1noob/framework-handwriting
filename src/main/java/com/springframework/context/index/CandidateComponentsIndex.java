package com.springframework.context.index;

import com.springframework.util.AntPathMatcher;
import com.springframework.util.ClassUtils;
import com.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public class CandidateComponentsIndex {
    public CandidateComponentsIndex(MultiValueMap<String, Entry> index) {
        this.index = index;
    }

    public Set<String> getCandidateTypes(String basePackage, String stereotype) {
        List<Entry> candidates = this.index.get(stereotype);
        if (candidates != null) {
            return candidates.parallelStream()
                    .filter(t -> t.match(basePackage))
                    .map(t -> t.type)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private static final AntPathMatcher pathMatcher = new AntPathMatcher(".");

    private final MultiValueMap<String, Entry> index;
    private static class Entry {

        private final String type;

        private final String packageName;

        Entry(String type) {
            this.type = type;
            this.packageName = ClassUtils.getPackageName(type);
        }

        public boolean match(String basePackage) {
            if (pathMatcher.isPattern(basePackage)) {
                return pathMatcher.match(basePackage, this.packageName);
            }
            else {
                return this.type.startsWith(basePackage);
            }
        }
    }
}
