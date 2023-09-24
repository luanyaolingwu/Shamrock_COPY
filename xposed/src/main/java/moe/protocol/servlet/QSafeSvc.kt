package moe.protocol.servlet

import QQService.SvcDevLoginInfo
import QQService.SvcReqGetDevLoginInfo
import QQService.SvcRspGetDevLoginInfo
import com.qq.jce.wup.UniPacket
import mqq.app.Packet
import java.util.ArrayList


internal object QSafeSvc: BaseSvc() {

    suspend fun getOnlineClients(): ArrayList<SvcDevLoginInfo>? {
        val req = SvcReqGetDevLoginInfo()
        val uniPacket = UniPacket()
        uniPacket.servantName = "StatSvc"
        uniPacket.funcName = "SvcReqGetDevLoginInfo"

        uniPacket.put<Any>("SvcReqGetDevLoginInfo", req)

        val resp = sendBufferAW("StatSvc.GetDevLoginInfo", false, uniPacket.encode())
            ?: error("unable to fetch contact model_show")
        return Packet.decodePacket(resp, "rsp",  SvcRspGetDevLoginInfo()).vecCurrentLoginDevInfo
    }


}