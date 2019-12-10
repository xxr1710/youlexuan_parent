package com.youlexuan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.youlexuan.entity.PageResult;
import com.youlexuan.group.Goods;
import com.youlexuan.mapper.*;
import com.youlexuan.pojo.*;
import com.youlexuan.pojo.TbGoodsExample.Criteria;
import com.youlexuan.sellergoods.service.GoodsService;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //设置未申请状态
        goods.getGoods().setAuditStatus("0");
        goodsMapper.insert(goods.getGoods());
        //int i = 1/0;
        //设置id
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        //插入商品拓展数据
        goodsDescMapper.insert(goods.getGoodsDesc());
        //插入商品SKU列表数据
        saveItemList(goods);
        /*if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : goods.getItemList()) {
                //标题
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);
                item.setGoodsId(goods.getGoods().getId());
                item.setSellerId(goods.getGoods().getSellerId());
                item.setCategoryid(goods.getGoods().getCategory3Id());
                //创建日期
                item.setCreateTime(new Date());
                //修改日期
                item.setUpdateTime(new Date());
                //品牌名称
                TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
                item.setBrand(brand.getName());
                //分类名称
                TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
                item.setCategory(itemCat.getName());
                //商家名称
                TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
                item.setSeller(seller.getNickName());
                //图片地址（取spu的第一个图片）
                List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
                if (imageList.size() > 0) {
                    item.setImage((String) imageList.get(0).get("url"));
                }
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            //存标题
            String title = goods.getGoods().getGoodsName();
            item.setTitle(title);

            //没有规格的默认数据
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(99999);
            item.setStatus("0");
            item.setIsDefault("0");
            item.setSpec("{}");
            //封装规格商品数据
            setItemValus(goods, item);

            //插入数据库
            itemMapper.insert(item);
        }*/

    }

    public void setItemValus(Goods goods, TbItem item) {

        //商家id
        item.setGoodsId(goods.getGoods().getId());
        //卖家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //分类id
        item.setCategoryid(goods.getGoods().getCategory3Id());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        //取品牌
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //取分类
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //存图片 JSON转型 map
        List<Map> urls = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (urls.size() > 0) {
            //[{"color":"红色","url":"http://192.168.188.146/group1/M00/00/00/wKi8kl3JyeeAJqYxAATdw4ZsQyQ147.jpg"},
            // {"color":"蓝色","url":"http://192.168.188.146/group1/M00/00/00/wKi8kl3JyfCAQGBgAAT5imdysJk573.jpg"}]
            item.setImage((String) urls.get(0).get("url"));
        }

    }

    @Override
    public List<TbItem> findItemListByGoodsIdAndStatus(Long[] goodsIds, String status) {
        //修改item表的状态值
        for (Long goodsId : goodsIds) {
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            List<TbItem> itemList = itemMapper.selectByExample(example);
            for (TbItem item : itemList) {
                item.setStatus("1");
                itemMapper.updateByPrimaryKey(item);
            }
        }
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goods.getGoods().setAuditStatus("0");//设置未申请状态:如果是经过修改的商品，需要重新设置状态
        goodsMapper.updateByPrimaryKey(goods.getGoods());//保存商品表
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//保存商品扩展表
        //删除原有的sku列表数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //添加新的sku列表数据
        saveItemList(goods);//插入商品SKU列表数据
    }
    private void saveItemList(Goods goods){
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            //遍历规格
            for (TbItem item : goods.getItemList()){
                String title = goods.getGoods().getGoodsName();
                Map<String, Object> specMap = JSON.parseObject(item.getSpec());
                for (String key : specMap.keySet()) {
                    title += " " + specMap.get(key);
                }
                item.setTitle(title);
                setItemValus(goods, item);
                itemMapper.insert(item);
            }
        }else {
            // 不启用规格
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());// 商品KPU+规格描述串作为SKU名称
            item.setPrice(goods.getGoods().getPrice());// 价格
            item.setStatus("1");// 状态
            item.setIsDefault("1");// 是否默认
            item.setNum(99999);// 库存数量
            item.setSpec("{}");
            setItemValus(goods, item);
            itemMapper.insert(item);
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);

        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        goods.setItemList(tbItems);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            criteria.andIsDeleteEqualTo("0");
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

}
