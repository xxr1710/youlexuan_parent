package com.youlexuan.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
import com.youlexuan.group.Goods;
import com.youlexuan.pojo.TbGoods;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    @Autowired
    private Destination topicPageDestination;//用于生成商品详细页的消息目标(发布订阅)

	/*@Reference
	private ItemSearchService itemSearchService;*/

    /*@Reference
    private ItemPageService itemPageService;*/

    @Autowired
    private Destination queueSolrDestination;

    @Autowired
    private JmsTemplate jmsTemplate;

    /**
     * 生成静态页
     *
     * @param
     */
    /*@RequestMapping("/genHtml")
    public void genHtml(Long goodsId) {
        itemPageService.genItemHtml(goodsId);
    }*/
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            //按照SPU ID查询 SKU列表（状态为1）
            if ("1".equals(status)) {
                List<TbItem> itemList = goodsService.findItemListByGoodsIdAndStatus(ids, status);
                //调用搜索接口实现数据批量导入
                if (itemList.size() > 0) {
                    //itemSearchService.importList(itemList);		//将数据导入到solr
                    final String jsonString = JSON.toJSONString(itemList);
                    jmsTemplate.send(queueSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(jsonString);
                        }
                    });
                    //静态页生成
                    for (final Long goodsId : ids) {
                        jmsTemplate.send(topicPageDestination, new MessageCreator() {
                            @Override
                            public Message createMessage(Session session) throws JMSException {
                                return session.createTextMessage(goodsId + "");
                            }
                        });
                    }
                } else {
                    System.out.println("没有明确的数据");
                }
                //静态页生成
                /*for (Long goodsId : ids) {
                    itemPageService.genItemHtml(goodsId);
                }*/
            }
            return new Result(true, "修改状态成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改状态失败");
        }
    }

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            goodsService.add(goods);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);

            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        return goodsService.findPage(goods, page, rows);
    }

}
