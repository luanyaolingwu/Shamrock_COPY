package moe.protocol.servlet

import mqq.app.MobileQQ
import oicq.wlogin_sdk.tools.util


internal object QSafeSvc: BaseSvc() {

    fun getOnlineClients()  {
        val createToServiceMsg = createToServiceMsg("StatSvc.GetDevLoginInfo")
        createToServiceMsg.extraData.putLong("iLoginType", 1L)
        createToServiceMsg.extraData.putLong("iNextItemIndex", 0)
        createToServiceMsg.extraData.putLong("iRequireMax", 20L)
        createToServiceMsg.extraData.putLong("iTimeStamp", System.currentTimeMillis() / 1000)
        createToServiceMsg.extraData.putString("strAppName", "Shamrock")
        createToServiceMsg.extraData.putByteArray("vecGuid", util.getGuidFromFile(MobileQQ.getContext()))
        createToServiceMsg.extraData.putLong("iGetDevListType", 5L)
        send(createToServiceMsg)

    }

}