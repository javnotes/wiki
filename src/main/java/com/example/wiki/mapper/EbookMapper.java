package com.example.wiki.mapper;

import com.example.wiki.domain.Ebook;
import com.example.wiki.domain.EbookExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface EbookMapper {
    long countByExample(EbookExample example);

    int deleteByExample(EbookExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Ebook row);

    int insertSelective(Ebook row);

    List<Ebook> selectByExample(EbookExample example);

    Ebook selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") Ebook row, @Param("example") EbookExample example);

    int updateByExample(@Param("row") Ebook row, @Param("example") EbookExample example);

    int updateByPrimaryKeySelective(Ebook row);

    int updateByPrimaryKey(Ebook row);
}