package com.qinghuan.booking;

import com.qinghuan.pojo.dto.OrderCreateDTO;
import com.qinghuan.pojo.dto.OrderPageQueryDTO;
import com.qinghuan.pojo.dto.VenueOrderPageQueryDTO;
import com.qinghuan.pojo.vo.OrderCreatedVO;
import com.qinghuan.pojo.vo.OrderDetailVO;
import com.qinghuan.pojo.vo.OrderSummaryVO;
import com.qinghuan.pojo.vo.PageResult;

public interface BookingService {

    /** 创建当前登录游客的订单，并完成库存预占。 */
    public OrderCreatedVO createOrder(OrderCreateDTO createOrderDTO);

    /** 分页查询当前登录游客自己的订单。 */
    PageResult<OrderSummaryVO> pageMyOrders(OrderPageQueryDTO queryDTO);

    /** 获取当前登录游客自己的订单详情。 */
    OrderDetailVO getMyOrder(Long orderId);

    /** 分页查询当前登录账号所属景点的订单。 */
    PageResult<OrderSummaryVO> pageVenueOrders(VenueOrderPageQueryDTO queryDTO);

    /** 获取当前登录账号所属景点的订单详情。 */
    OrderDetailVO getVenueOrder(Long orderId);

    // 整单退款
    void refundOrder(Long orderId);
}
