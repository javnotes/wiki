package com.example.wiki.mapper;

import com.example.wiki.domain.Doc;
import com.example.wiki.domain.DocExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DocMapperCust {
    void increaseViewCount(@Param("id") Long id);
}
