package com.youlexuan.sellergoods.service;

import com.youlexuan.entity.PageResult;
import com.youlexuan.pojo.TbBrand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有TbBrand数据
     * @return
     */
    public List<TbBrand> findBrandAll();

    /**
     * 返回分页列表
     * @param pageNum   当前页
     * @param pageSize  显示多少条数据
     * @return
     */
    public PageResult findPage(int pageNum,int pageSize);

    /**
     * 添加
     * @param brand
     */
    public void add(TbBrand brand);

    /**
     * 根据id获取实体
     * @param id
     * @return
     */
    public TbBrand findOne(Long id);

    /**
     * 修改
     * @param brand
     */
    public void update(TbBrand brand);

    /**
     * 批量删除
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 根据品牌或品牌首字母进行模糊查询，并分页
     * @param brand
     * @param pageNum   当前页
     * @param pageSize  每页记录数
     * @return
     */
    public PageResult findPage(TbBrand brand,int pageNum,int pageSize);

    /**
     * 品牌下拉框数据
     * @return
     */
    List<Map> selectOptionList();
}
