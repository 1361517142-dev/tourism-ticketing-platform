package com.qinghuan.session;

import com.qinghuan.pojo.dto.SessionPageQueryDTO;
import com.qinghuan.pojo.dto.SessionTicketTypeConfigDTO;
import com.qinghuan.pojo.entity.AdmissionSession;
import com.qinghuan.pojo.entity.SessionTicketType;
import com.qinghuan.pojo.enums.AdmissionSessionStatus;
import com.qinghuan.pojo.vo.SessionTicketTypeVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SessionMapper {

    /**
     * 按日期和状态筛选指定景点的场次主体，供分页使用。
     */
    List<AdmissionSession> list(@Param("venueId") Long venueId,
                                @Param("query") SessionPageQueryDTO queryDTO);

    /**
     * 按场次 ID 和景点 ID 查询，作为所有单场次操作的范围约束。
     */
    AdmissionSession findById(@Param("id") Long id,
                              @Param("venueId") Long venueId);

    /**
     * 查询场次已配置的票种，并关联返回票种名称。
     */
    List<SessionTicketTypeVO> listTicketTypes(Long sessionId);

    /**
     * 统计当前景点内处于 ENABLED 状态的指定票种数量。
     */
    int countEnabledTicketTypes(@Param("venueId") Long venueId,
                                @Param("ids") List<Long> ticketTypeIds);

    /**
     * 写入场次主体，并回填数据库生成的场次 ID。
     */
    int insertSession(AdmissionSession session);

    /**
     * 批量写入同一场次的票种售价与配额配置。
     */
    void insertSessionTicketTypes(List<SessionTicketType> ticketTypes);

    /**
     * 仅更新当前景点的草稿场次，避免修改已经开放的场次。
     */
    int updateDraftSession(@Param("session") AdmissionSession session,
                           @Param("venueId") Long venueId);

    /**
     * 判断场次是否存在可售票种，且全部票种配额未超出场次容量。
     */
    boolean isOpenable(@Param("sessionId") Long sessionId,
                       @Param("totalCapacity") Integer totalCapacity);

    /**
     * 统计未取消、未关闭的订单，用于取消场次前的业务校验。
     */
    int countUnresolvedOrders(Long sessionId);

    /**
     * 在旧状态仍匹配时更新状态，防止并发事件覆盖。
     */
    int updateStatus(@Param("id") Long id,
                     @Param("venueId") Long venueId,
                     @Param("currentStatus") AdmissionSessionStatus currentStatus,
                     @Param("targetStatus") AdmissionSessionStatus targetStatus);

    /**
     * 删除场次下的全部票种配置。
     */
    void deleteSessionTicketTypes(Long sessionId);

    /**
     * 统计场次的全部订单，用于物理删除前校验。
     */
    int countOrders(Long sessionId);

    /**
     * 仅删除当前景点仍为草稿状态的场次主体。
     */
    int deleteDraftSession(@Param("id") Long id,
                           @Param("venueId") Long venueId);
}
