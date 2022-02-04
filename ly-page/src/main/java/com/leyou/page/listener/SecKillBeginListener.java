package com.leyou.page.listener;

import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.leyou.common.constants.RocketMQConstants.CONSUMER.SECKILL_BEGIN_CONSUMER;
import static com.leyou.common.constants.RocketMQConstants.TAGS.SECKILL_BEGIN_TAGS;
import static com.leyou.common.constants.RocketMQConstants.TOPIC.SECKILL_TOPIC_NAME;

/**
 * 秒杀业务  监听
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = SECKILL_TOPIC_NAME,
        selectorExpression = SECKILL_BEGIN_TAGS,
        consumerGroup = SECKILL_BEGIN_CONSUMER)
public class SecKillBeginListener implements RocketMQListener<String> {
    @Autowired
    private PageService pageService;

    /**
     * 监听 创建秒杀列表和详情页 消息
     *
     * @param date
     */
    @Override
    public void onMessage(String date) {
        log.info("[静态页服务] - 秒杀开始 接收到消息 : 时间：{}", date);
        pageService.createSecKillListAndDetailHtml(date);

    }
}