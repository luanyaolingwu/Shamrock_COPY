package moe.protocol.servlet

import QQService.SvcDevLoginInfo
import QQService.SvcReqGetDevLoginInfo
import QQService.SvcRspGetDevLoginInfo
import com.qq.jce.wup.UniPacket
import moe.fuqiuluo.xposed.helper.LogCenter
import moe.protocol.servlet.utils.PlatformUtils
import mqq.app.MobileQQ
import mqq.app.Packet
import oicq.wlogin_sdk.tools.util
import java.lang.reflect.Modifier
import java.util.ArrayList


internal object QSafeSvc: BaseSvc() {

    suspend fun getOnlineClients(): ArrayList<SvcDevLoginInfo>? {
        val req = SvcReqGetDevLoginInfo()
        req.vecGuid = util.getGuidFromFile(MobileQQ.getContext())
        req.strAppName = MobileQQ.getMobileQQ().qqProcessName.split(":")[0]
        req.iLoginType = 1
        req.iRequireMax = 20
        req.iGetDevListType = 6

        val uniPacket = UniPacket()
        uniPacket.servantName = "StatSvc"
        uniPacket.funcName = "SvcReqGetDevLoginInfo"
        uniPacket.put("SvcReqGetDevLoginInfo", req)
        val resp = sendBufferAW("StatSvc.GetDevLoginInfo", false, uniPacket.encode())
            ?: return null

        return Packet.decodePacket(resp, "SvcRspGetDevLoginInfo",  SvcRspGetDevLoginInfo()).vecCurrentLoginDevInfo
    }


}