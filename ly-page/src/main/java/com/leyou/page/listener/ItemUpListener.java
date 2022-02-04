package com.leyou.page.listener;

import com.leyou.common.constants.RocketMQConstants;
import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.ITEM_PAGE_UP_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.ITEM_UP_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.ITEM_TOPIC_NAME;

@Slf4j
@Component
@RocketMQMessageListener(consumerGroup = ITEM_PAGE_UP_CONSUMER
        ,topic = ITEM_TOPIC_NAME
        ,selectorExpression = ITEM_UP_TAGS)
public class ItemUpListener implements RocketMQListener<Long> {
    @Autowired
    private PageService pageService;
    /**
     * 上架 生成静态页面
     * @param spuId
     */
    @Override
    public void onMessage(Long spuId) {
        pageService.createHtml(spuId);
    }
}
