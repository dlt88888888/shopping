package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand> {

    @Insert("insert into tb_category_brand(category_id,brand_id)values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    void deleteRelationBrandAndCategory(Long bid);

    @Select("select b.* from tb_brand b inner join tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> selectBrandByCid(Long cid);

    @Delete("delete from tb_category_brand where category_id=#{cid} and brand_id=#{bid}")
    void deleteCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    @Select("SELECT cb.category_id FROM tb_category_brand cb WHERE brand_id=#{id}")
    List<Long> selectCategoryAndBrand(Long id);
}