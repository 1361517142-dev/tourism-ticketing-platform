package com.qinghuan.booking;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qinghuan.annotation.RefreshCreateTimeOrUpdateTime;
import com.qinghuan.auth.context.UserContext;
import com.qinghuan.common.exception.BusinessException;
import com.qinghuan.common.exception.ErrorCode;
import com.qinghuan.pojo.dto.OrderCreateDTO;
import com.qinghuan.pojo.dto.OrderCreateItemRequest;
import com.qinghuan.pojo.dto.OrderPageQueryDTO;
import com.qinghuan.pojo.dto.VenueOrderPageQueryDTO;
import com.qinghuan.pojo.entity.BookingOrder;
import com.qinghuan.pojo.entity.BookingOrderItem;
import com.qinghuan.pojo.entity.Ticket;
import com.qinghuan.pojo.entity.Visitor;
import com.qinghuan.pojo.enums.BookingOrderEvent;
import com.qinghuan.pojo.enums.BookingOrderStatus;
import com.qinghuan.pojo.enums.TicketStatus;
import com.qinghuan.pojo.vo.*;
import com.qinghuan.session.SessionInventoryService;
import com.qinghuan.session.SessionService;
import com.qinghuan.ticket.TicketService;
import com.qinghuan.visitor.VisitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 订单业务实现。
 * 创建订单时由后端校验参观人和场次票种，并保存不可变的下单快照。
 */
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingMapper bookingMapper;
    private final VisitorService visitorService;
    private final SessionInventoryService sessionInventoryService;
    private final TicketService ticketService;
    private final SessionService sessionService;

    public BookingServiceImpl(BookingMapper bookingMapper,
                              VisitorService visitorService,
                              SessionInventoryService sessionInventoryService,
                              TicketService ticketService, SessionService sessionService) {
        this.bookingMapper = bookingMapper;
        this.visitorService = visitorService;
        this.sessionInventoryService = sessionInventoryService;
        this.ticketService = ticketService;
        this.sessionService = sessionService;
    }

    /**
     * 分页查询当前游客的订单。
     * userId 只从登录上下文获取，避免客户端通过查询参数越权读取他人订单。
     */
    @Override
    public PageResult<OrderSummaryVO> pageMyOrders(OrderPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<OrderSummaryVO> page = (Page<OrderSummaryVO>) bookingMapper.listMyOrders(
                UserContext.getRequired().userId(), queryDTO.getStatus());
        return toPageResult(page);
    }

    /** 获取当前游客的订单头，并一次性补充全部明细及票券。 */
    @Override
    public OrderDetailVO getMyOrder(Long orderId) {
        OrderDetailVO detail = bookingMapper.findMyOrderDetail(
                orderId, UserContext.getRequired().userId());
        return completeOrderDetail(detail, orderId);
    }

    /**
     * 运营端分页查询只使用 JWT 中的 venueId 作为数据范围。
     * 订单号、场次和参观日期都只是当前景点范围内的附加筛选条件。
     */
    @Override
    public PageResult<OrderSummaryVO> pageVenueOrders(VenueOrderPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(), queryDTO.getSize());
        Page<OrderSummaryVO> page = (Page<OrderSummaryVO>) bookingMapper.listVenueOrders(
                UserContext.getRequired().venueId(), queryDTO);
        return toPageResult(page);
    }

    /** 获取当前景点的订单详情，并补充购买人、明细及票券信息。 */
    @Override
    public OrderDetailVO getVenueOrder(Long orderId) {
        OrderDetailVO detail = bookingMapper.findVenueOrderDetail(
                orderId, UserContext.getRequired().venueId());
        return completeOrderDetail(detail, orderId);
    }

    /** 整单退款 */
    @Override
    @Transactional
    public void refundOrder(Long orderId) {
        // 获取当前订单
        BookingOrder order = bookingMapper.findOrderByOrderId(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        // 判断所属游客账号是否合法
        if (!order.getUserId().equals(UserContext.getRequired().userId())) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN, "无权访问该订单");
        }
        // 状态转换
        BookingOrderStatus oldStatus = order.getStatus();
        BookingOrderStatus newStatus;
        try {
            newStatus = oldStatus.next(BookingOrderEvent.REFUND_SUCCESS);
        } catch (IllegalStateException exception) {
            throw new BusinessException(ErrorCode.CONFLICT, "只有已支付订单可以退款");
        }
        // 业务validate检查
        // 场次必须尚未开始
        LocalDateTime sessionStartTime = sessionService.getSessionStartTime(order.getSessionId());
        if (!LocalDateTime.now().isBefore(sessionStartTime)) {
            throw new BusinessException(
                    ErrorCode.CONFLICT, "场次已开始，不能进行整单退款");
        }
        // 获取订单对应的票券
        List<Ticket> tickets = ticketService.listTicketsByOrderId(orderId);
        if (tickets.stream().anyMatch(ticket -> ticket.getStatus() == TicketStatus.USED)) {
            throw new BusinessException(
                    ErrorCode.CONFLICT, "订单内已使用的票券不能进行整单退款");
        }

        // 创建更新订单信息
        // 按订单明细汇总每种场次票的数量，退款完成后按原数量归还库存。
        Map<Long, Integer> ticketTypeQuantities = bookingMapper.listOrderItems(orderId).stream()
                .collect(Collectors.groupingBy(
                        OrderItemVO::getSessionTicketTypeId,
                        Collectors.summingInt(item -> 1)));

        order.setRefundAt(LocalDateTime.now());
        order.setStatus(newStatus);
        Integer okNumber = bookingMapper.updateOrder(order, oldStatus);

        if (okNumber == 0) {
            throw new BusinessException(
                    ErrorCode.CONFLICT, "订单状态发生变化");
        }

        // 修改属于当前订单的有效票券状态为 VOID。
        List<Ticket> validTickets = tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.VALID)
                .toList();
        validTickets.forEach(ticket -> ticket.setStatus(TicketStatus.VOID));
        if (!validTickets.isEmpty()
                && ticketService.updateTickets(validTickets) != validTickets.size()) {
            // 如果票券在退款过程中被并发核销，则回滚整笔退款。
            throw new BusinessException(ErrorCode.CONFLICT, "票券状态发生变化，退款失败");
        }


        // 释放库存
        sessionInventoryService.releaseInventory(order.getSessionId(), ticketTypeQuantities);

    }


    /** 创建订单、明细快照并预占库存，任一步失败都回滚。 */
    @Override
    @Transactional
    public OrderCreatedVO createOrder(OrderCreateDTO createOrderDTO) {
        // 数据库同样限制一个订单内参观人唯一；这里提前返回更明确的业务错误。
        List<Long> visitorIds = createOrderDTO.items().stream()
                .map(OrderCreateItemRequest::visitorId)
                .toList();
        if (new HashSet<>(visitorIds).size() != visitorIds.size()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "同一个参观人不能重复下单");
        }

        // 只允许使用当前游客名下且已启用的参观人，同时保留原始证件信息用于订单快照。
        Map<Long, Visitor> visitorsById = visitorService.listActiveVisitorsForOrder().stream()
                .collect(Collectors.toMap(Visitor::getId, Function.identity()));
        if (!visitorsById.keySet().containsAll(visitorIds)) {
            throw new BusinessException(
                    ErrorCode.CONFLICT, "参观人不存在、不属于当前游客或已停用");
        }

        // 汇总每种场次票的购买数量，库存服务据此进行原子扣减。
        Map<Long, Integer> ticketTypeQuantities = createOrderDTO.items().stream()
                .collect(Collectors.groupingBy(
                        OrderCreateItemRequest::sessionTicketTypeId,
                        Collectors.summingInt(item -> 1)));

        // 场次模块统一校验场次开放时间、票种归属、销售状态和可售余量。
        List<Long> sessionTicketTypeIds = ticketTypeQuantities.keySet().stream().toList();
        List<SessionTicketTypeVO> ticketTypes = sessionInventoryService.getOrderableTicketTypes(
                createOrderDTO.sessionId(), sessionTicketTypeIds);
        Map<Long, SessionTicketTypeVO> ticketTypesById = ticketTypes.stream()
                .collect(Collectors.toMap(
                        SessionTicketTypeVO::getSessionTicketTypeId,
                        Function.identity()));

        // 价格只能取后端保存的场次售价，不能信任前端传入的金额。
        BigDecimal totalAmount = createOrderDTO.items().stream()
                .map(item -> ticketTypesById.get(item.sessionTicketTypeId()).getSalePrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 订单号使用无分隔符 UUID，长度正好符合 order_no 的 32 字符限制。
        BookingOrder bookingOrder = new BookingOrder();
        bookingOrder.setOrderNo(UUID.randomUUID().toString().replace("-", ""));
        bookingOrder.setUserId(UserContext.getRequired().userId());
        bookingOrder.setSessionId(createOrderDTO.sessionId());
        bookingOrder.setQuantity(createOrderDTO.items().size());
        bookingOrder.setTotalAmount(totalAmount);

        // 零元订单无需经过支付接口，创建后直接视为已支付。
        if (totalAmount.signum() == 0) {
            bookingOrder.setStatus(BookingOrderStatus.PAID);
            bookingOrder.setPaidAt(LocalDateTime.now());
        } else {
            bookingOrder.setStatus(BookingOrderStatus.PENDING_PAYMENT);
            bookingOrder.setExpireAt(LocalDateTime.now().plusMinutes(15));
        }

        // 主键回填后才能为每条订单明细设置 orderId。
        bookingMapper.insertOrder(bookingOrder);
        List<BookingOrderItem> orderItems = createOrderDTO.items().stream()
                .map(item -> toOrderItem(
                        bookingOrder.getId(), item,
                        visitorsById.get(item.visitorId()),
                        ticketTypesById.get(item.sessionTicketTypeId())))
                .toList();
        bookingMapper.insertOrderItems(orderItems);

        // 免费票在创建订单的同一事务中生成，避免出现已支付但无票的订单。
        if (bookingOrder.getStatus() == BookingOrderStatus.PAID) {
            ticketService.createTicketsForOrder(
                    bookingOrder.getId(), bookingOrder.getQuantity());
        }

        // 库存扣减失败会抛出异常，依靠外层事务回滚订单、明细和已生成票券。
        sessionInventoryService.reserveInventory(
                createOrderDTO.sessionId(), ticketTypeQuantities);

        return new OrderCreatedVO(
                bookingOrder.getId(), bookingOrder.getOrderNo(),
                bookingOrder.getTotalAmount(), bookingOrder.getStatus(),
                bookingOrder.getExpireAt());
    }

    /** 将当前资料和成交价格复制为订单明细快照，防止后续资料修改影响历史订单。 */
    private BookingOrderItem toOrderItem(
            Long orderId, OrderCreateItemRequest request,
            Visitor visitor, SessionTicketTypeVO ticketType) {
        BookingOrderItem orderItem = new BookingOrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setVisitorId(visitor.getId());
        orderItem.setSessionTicketTypeId(request.sessionTicketTypeId());
        orderItem.setVisitorName(visitor.getName());
        orderItem.setVisitorIdType(visitor.getIdType());
        orderItem.setVisitorIdNumber(visitor.getIdNumber());
        orderItem.setTicketTypeName(ticketType.getTicketTypeName());
        orderItem.setUnitPrice(ticketType.getSalePrice());
        return orderItem;
    }

    /**
     * 详情头查询已经带上游客或景点范围；查询不到时统一返回 NOT_FOUND，
     * 既符合接口语义，也不会向调用者暴露订单是否属于其他账号。
     */
    private OrderDetailVO completeOrderDetail(OrderDetailVO detail, Long orderId) {
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        detail.setItems(bookingMapper.listOrderItems(orderId));
        return detail;
    }

    /** 将 PageHelper 的分页对象转换为项目统一分页响应。 */
    private PageResult<OrderSummaryVO> toPageResult(Page<OrderSummaryVO> page) {
        return new PageResult<>(
                page.getResult(), page.getTotal(), page.getPageNum(), page.getPageSize());
    }
}
