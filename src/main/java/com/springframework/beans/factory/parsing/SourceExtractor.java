package com.springframework.beans.factory.parsing;

import com.springframework.core.io.Resource;


/**
 * @Author 虎哥
 * @Description //TODO
 * |要带着问题去学习,多猜想多验证|
 **/
public interface SourceExtractor {

    Object extractSource(Object sourceCandidate,  Resource definingResource);

}
