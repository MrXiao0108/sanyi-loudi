package com.dzics.business.service;

import com.dzics.common.model.entity.SysCmdTcp;
import com.dzics.common.model.request.AddAndUpdCmdTcpVo;
import com.dzics.common.model.request.CmdTcpItemVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

/**
 * <p>
 * tcp 指令标识表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface BusinessSysCmdTcpService {



    Result<SysCmdTcp> add(String sub, AddAndUpdCmdTcpVo cmdTcpVo);

    Result del(Integer id);

    Result update(AddAndUpdCmdTcpVo cmdTcpVo);

    Result getById(Integer id);

    Result listItem(PageLimit pageLimit, Integer id);

    Result<SysCmdTcp> addItem(CmdTcpItemVo cmdTcpItemVo);

    Result delItem(Integer id);

    Result<SysCmdTcp> updateItem(CmdTcpItemVo cmdTcpItemVo);

    Result<SysCmdTcp> getByIdItem(Integer id);

    Result list(PageLimit pageLimit, String tcpName, String tcpValue, Integer tcpType, String tcpDescription, Integer deviceType);
}
