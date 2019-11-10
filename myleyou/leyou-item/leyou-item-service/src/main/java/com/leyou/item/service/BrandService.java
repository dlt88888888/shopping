package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {

        //初始化example
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name 模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);//注入插叙条件
        }

        //添加分页条件
        PageHelper.startPage(page, rows);//

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }
        //将结果包装成一个PageInfo结果集
        List<Brand> brands = this.brandMapper.selectByExample(example);
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {

        this.brandMapper.insertSelective(brand);


        cids.forEach(cid -> {
            this.brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });

    }

    @Transactional
    public void deleteBrandById(Long bid) {
        this.brandMapper.deleteRelationBrandAndCategory(bid);

        this.brandMapper.deleteByPrimaryKey(bid);
    }

    public List<Brand> queryBrandByCid(Long cid) {

        return this.brandMapper.selectBrandByCid(cid);
    }

    /**
     * 更新品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        this.brandMapper.updateByPrimaryKeySelective(brand);

        List<Long> cs= this.brandMapper.selectCategoryAndBrand(brand.getId());

        //先删除该品牌所属的类目
        cs.forEach(c->{
            this.brandMapper.deleteCategoryAndBrand(c,brand.getId());
        });

        //重新建立品牌与类别的关系

        cids.forEach(cid->{
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 根据id返回品牌
     * @param id
     * @return
     */

    public Brand queryBrandById(Long id) {
        Brand brand = this.brandMapper.selectByPrimaryKey(id);
        return brand;
    }
}
