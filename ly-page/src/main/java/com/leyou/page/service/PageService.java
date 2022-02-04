package com.leyou.page.service;

import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.seckill.client.SeckillClient;
import com.leyou.seckill.pojo.SeckillPolicyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;
    /**
     * 获取 模板页面需要的动态数据
     * 使用远程调用 feign
     * @param spuId
     */
    public Map<String,Object> loadItemData(Long spuId) {
//        获取spu对象
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        分类id的集合
        List<Long> categoryIds = spuDTO.getCategoryIds();
//        categories  spu的分类集合分类1，分类2，分类3
//        获取分类对象的集合
        List<CategoryDTO> categoryDTOList = itemClient.findCategoryListByIds(categoryIds);
//        brand       spu的品牌对象
        Long brandId = spuDTO.getBrandId();
        BrandDTO brandDTO = itemClient.findbrandById(brandId);
//        spuName     spu的名字
        String spuName = spuDTO.getName();
//        subTitle    促销信息
        String subTitle = spuDTO.getSubTitle();
//        detail      spuDetail的对象
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailBySpuId(spuId);
//        skus        sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
//        specs       获取规格组 和 规格名字的对应
        List<SpecGroupDTO> groupDTOList = itemClient.findSpecsByCid(spuDTO.getCid3());

        Map<String,Object> map = new HashMap<>();
        map.put("categories",categoryDTOList);
        map.put("brand",brandDTO);
        map.put("spuName",spuName);
        map.put("subTitle",subTitle);
        map.put("detail",spuDetailDTO);
        map.put("skus",skuDTOList);
        map.put("specs",groupDTOList);
        return map;
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

//    静态页目录
    private String pagePath = "D:\\Develop\\nginx-1.13.12\\html\\item";
    /**
     * 生成静态页面
     */
    public void createHtml(Long spuId){
//        context上下文
        Context context = new Context();
//        获取动态数据
        Map<String, Object> data = this.loadItemData(spuId);
//        把动态数据写入context
        context.setVariables(data);
//        模板解析器（springboot已经整合）
//        静态页的目录
        File dir = new File(pagePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        PrintWriter writer = null;
//        构造静态页面
        File file = new File(dir, spuId + ".html");
        try{
//        构建写文件用的IO
            writer = new PrintWriter(file,"UTF-8");
//        使用templateEngine 生成静态页面
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    /**
     * 删除静态页面
     * @param spuId
     */
    public void removePage(Long spuId) {
        File dir = new File(pagePath);
        if(!dir.exists()){
           return;
        }
//        构造静态页面
        File file = new File(dir, spuId + ".html");
//        删除文件
        file.delete();
    }

    @Autowired
    private SeckillClient secKillClient;

    private String secKillFilePath = "D:\\Develop\\nginx-1.13.12\\html\\seckill";
    /**
     * 生成 秒杀列表页和秒杀详情页
     * @param date
     */
    public void createSecKillListAndDetailHtml(String date) {


//        从seckill中获取秒杀的列表
        List<SeckillPolicyDTO> secKillPolicyList = secKillClient.findSecKillPolicyList(date);
        this.createSecKillLisHtml(secKillPolicyList);
        for (SeckillPolicyDTO seckillPolicyDTO : secKillPolicyList) {
            this.createSeckillDetailHtml(seckillPolicyDTO);
        }

    }

    /**
     * 创建秒杀详情页面
     * @param seckillPolicyDTO
     */
    private void createSeckillDetailHtml(SeckillPolicyDTO seckillPolicyDTO) {

        //       秒杀规则id
        Long secKillId = seckillPolicyDTO.getId();
        //        模板上下文
        Context context = new Context();
        //获取秒杀商品的规格参数信息
        Map<String, Object> itemData = loadSecKillDetailData(seckillPolicyDTO);
        //设置 动态数据
        context.setVariables(itemData);
        //        模板目录,nginx下的html下的seckill
        File dir = new File(secKillFilePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        //        要生成的文件名
        File itemFile = new File(dir,secKillId+".html");
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(itemFile,"UTF-8");
            //详情页模板名称为 seckill-item
            templateEngine.process("seckill-item",context,printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }
    }

    /**
     * 创建秒杀列表页面
     * @param secKillPolicyList
     */
    private void createSecKillLisHtml(List<SeckillPolicyDTO> secKillPolicyList) {
        Context context = new Context();
        Map<String,Object> map = new HashMap<>();
        map.put("seckillList",secKillPolicyList);
        context.setVariables(map);
        File dir = new File(secKillFilePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        PrintWriter writer = null;
        try{
            File file = new File(dir, "list.html");
            writer = new PrintWriter(file,"UTF-8");
            templateEngine.process("seckill-index",context,writer);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    /**
     * 获取秒杀详情信息，商品的规格参数
     * @param seckillPolicyDTO 秒杀 规则对象
     * @return
     */
    public Map<String, Object> loadSecKillDetailData(SeckillPolicyDTO seckillPolicyDTO) {
        Map<String,Object> map = new HashMap<>();
        Long spuId = seckillPolicyDTO.getSpuId();
        //获取spu detail
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailBySpuId(spuId);
        //规格组 和 组内规格
        List<SpecGroupDTO> specGroupDTOList = itemClient.findSpecsByCid(seckillPolicyDTO.getCid3());
        map.put("seckillgoods",seckillPolicyDTO);
        map.put("detail",spuDetailDTO);
        map.put("specs",specGroupDTOList);
        return map;
    }
}
