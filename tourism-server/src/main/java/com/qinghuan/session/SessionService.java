package com.qinghuan.session;

import com.qinghuan.pojo.dto.SessionPageQueryDTO;
import com.qinghuan.pojo.dto.SessionWriteDTO;
import com.qinghuan.pojo.enums.SessionEvent;
import com.qinghuan.pojo.vo.PageResult;
import com.qinghuan.pojo.vo.SessionVO;

public interface SessionService {

    /**
     * 分页返回当前景点的场次及其票种配置。
     */
    PageResult<SessionVO> pageSessions(SessionPageQueryDTO queryDTO);

    /**
     * 新建 DRAFT 场次，并初始化场次和票种的剩余数量。
     */
    Long createSession(SessionWriteDTO writeDTO);

    /**
     * 查询当前景点内指定场次的完整信息。
     */
    SessionVO getSession(Long sessionId);

    /**
     * 修改草稿场次，并整组替换票种售价和配额。
     */
    void updateDraftSession(Long sessionId, SessionWriteDTO writeDTO);

    /**
     * 按当前状态和业务事件执行一次状态流转。
     */
    void handleSessionEvent(Long sessionId, SessionEvent event);

    /**
     * 删除没有订单的草稿场次及其票种配置。
     */
    void deleteDraftSession(Long sessionId);
}
