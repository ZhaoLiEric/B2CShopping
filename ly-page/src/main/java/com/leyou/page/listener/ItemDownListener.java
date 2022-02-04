package com.leyou.page.listener;

import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.ITEM_PAGE_DOWN_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_DOWN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = ITEM_PAGE_DOWN_CONSUMER
        ,topic = ITEM_TOPIC_NAME
        ,selectorExpression = ITEM_DOWN_TAGS)
public class ItemDownListener implements RocketMQListener<Long> {
    @Autowired
    private PageService pageService;
    /**
     * 下架 删除静态页面
     * @param spuId
     */
    @Override
    public void onMessage(Long spuId) {

        pageService.removePage(spuId);
    }
}
