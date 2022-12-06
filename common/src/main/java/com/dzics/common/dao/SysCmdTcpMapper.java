package com.dzics.common.dao;

import com.dzics.common.model.custom.CmdTcp;
import com.dzics.common.model.custom.TcpDescValue;
import com.dzics.common.model.custom.TcpValCmdName;
import com.dzics.common.model.entity.SysCmdTcp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.SysCmdTcpItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * tcp 指令标识表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysCmdTcpMapper extends BaseMapper<SysCmdTcp> {
    List<CmdTcp> getCmdTcpList();

    List<SysCmdTcpItemVo> listItem(@Param("field") String field, @Param("type") String type, @Param("id") Integer id);

    Integer examine(@Param("cmdName") String cmdName,
                    @Param("deviceItemValue") String deviceItemValue,
                    @Param("tcpDescription") String tcpDescription);

    /**
     * 根据指令名 指令值对应的描述 查询对象
     *
     * @param cmdName
     * @param tcpItemDescription
     * @return
     */
    SysCmdTcp getTcpItemValue(@Param("cmdName") String cmdName, @Param("tcpItemDescription") String tcpItemDescription);

    String getTcpValue(@Param("cmd") String cmd);

    List<TcpDescValue> getGroupType(@Param("cmdName") String cmdName);

    List<TcpValCmdName> getTcpValueList();


}
