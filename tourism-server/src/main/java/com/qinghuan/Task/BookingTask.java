package com.qinghuan.Task;

import com.qinghuan.booking.BookingService;
import com.qinghuan.pojo.entity.BookingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class BookingTask {
    private BookingService bookingService;
    public BookingTask(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /*
     * 每分钟清理一次超时未支付订单
     */
    @Scheduled(cron = "0 * * * * *")
    public void autoCancelOrder() {
        log.info("开始清理超时未支付订单");
        // 获取所有超时未支付订单
        List<BookingOrder> timeoutOrders = bookingService.listTimeoutOrders();
        for (BookingOrder order : timeoutOrders) {
            try {
                // 取消超时订单
                bookingService.cancelTimeoutOrder(order.getId());
            } catch (Exception e) {
                log.error("取消超时订单失败：{}，订单号：{}", e.getMessage(), order.getOrderNo());
            }
        }
    }
}
