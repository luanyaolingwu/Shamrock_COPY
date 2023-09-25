@file:OptIn(DelicateCoroutinesApi::class)

package moe.protocol.servlet

import VIP.GetCustomOnlineStatusReq
import VIP.GetCustomOnlineStatusRsp
import com.qq.jce.wup.UniPacket
import com.tencent.common.app.AppInterface
import com.tencent.mobileqq.data.Card
import com.tencent.mobileqq.profilecard.api.IProfileDataService
import com.tencent.mobileqq.profilecard.api.IProfileProtocolService
import com.tencent.mobileqq.profilecard.observer.ProfileCardObserver
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.contentType
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import moe.fuqiuluo.xposed.tools.GlobalClient
import moe.fuqiuluo.xposed.tools.slice
import mqq.app.MobileQQ
import mqq.app.Packet
import tencent.im.oidb.cmd0x11b2.oidb_0x11b2
import tencent.im.oidb.oidb_sso
import kotlin.coroutines.resume

internal object CardSvc: BaseSvc() {
    private val GetModelShowLock = Mutex()
    private val refreshCardLock = Mutex()

    suspend fun getModelShow(uin: Long = app.longAccountUin): String {
        return GetModelShowLock.withLock {
            val uniPacket = UniPacket()
            uniPacket.servantName = "VIP.CustomOnlineStatusServer.CustomOnlineStatusObj"
            uniPacket.funcName = "GetCustomOnlineStatus"
            val getCustomOnlineStatusReq = GetCustomOnlineStatusReq()
            getCustomOnlineStatusReq.lUin = uin
            getCustomOnlineStatusReq.sIMei = ""
            uniPacket.put("req", getCustomOnlineStatusReq)

            val resp = sendBufferAW("VipCustom.GetCustomOnlineStatus", false, uniPacket.encode())
                ?: error("unable to fetch contact model_show")
            Packet.decodePacket(resp, "rsp",  GetCustomOnlineStatusRsp()).sBuffer
        }
    }

    suspend fun setModelShow(model: String) {
        val pSKey = TicketSvc.getPSKey(app.currentUin)
        val url = "https://club.vip.qq.com/srf-cgi-node?srfname=VIP.CustomOnlineStatusServer.CustomOnlineStatusObj.SetCustomOnlineStatus&ts=${System.currentTimeMillis()}&daid=18&g_tk=${TicketSvc.getCSRF(pSKey)}&pt4_token=${TicketSvc.getPt4Token(pSKey, "club.vip.qq.com") ?: ""}"
        val cookie = TicketSvc.getCookie("club.vip.qq.com")
        GlobalClient.post(url) {
            contentType(Json)
            header("Cookie", cookie)
            setBody("""{
  "servicesName": "VIP.CustomOnlineStatusServer.CustomOnlineStatusObj",
  "cmd": "SetCustomOnlineStatus",
  "args": [
    {
      "sModel": "$model",
      "iAppType": 3,
      "sIMei": "",
      "sVer": "",
      "sManu": "undefined",
      "lUin": ${app.currentUin},
      "bShowInfo": ${model.isNotEmpty()},
      "sModelShow": "$model",
      "bRecoverDefault": ${model.isEmpty()}
    }
  ]
}""")
        }
    }

    suspend fun getSharePrivateArkMsg(peerId: Long): String {
        val reqBody = oidb_0x11b2.BusinessCardV3Req()
        reqBody.uin.set(peerId)
        reqBody.jump_url.set("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=$peerId")

        val buffer = sendOidbAW("OidbSvcTrpcTcp.0x11ca_0", 4790, 0, reqBody.toByteArray())
            ?: error("unable to fetch contact ark_json_text")
        val body = oidb_sso.OIDBSSOPkg()
        body.mergeFrom(buffer.slice(4))
        val rsp = oidb_0x11b2.BusinessCardV3Rsp()
        rsp.mergeFrom(body.bytes_bodybuffer.get().toByteArray())
        return rsp.signed_ark_msg.get()
    }

    suspend fun getProfileCard(uin: String): Result<Card> {
        return getProfileCardFromCache(uin).onFailure {
            return refreshAndGetProfileCard(uin)
        }
    }

    fun getProfileCardFromCache(uin: String): Result<Card> {
        val profileDataService = app
            .getRuntimeService(IProfileDataService::class.java, "all")
        val card = profileDataService.getProfileCard(uin, true)
        return if (card == null) {
            Result.failure(Exception("unable to fetch profile card"))
        } else {
            Result.success(card)
        }
    }

    suspend fun refreshAndGetProfileCard(uin: String): Result<Card> {
        val dataService = app
            .getRuntimeService(IProfileDataService::class.java, "all")
        val card = refreshCardLock.withLock {
            suspendCancellableCoroutine {
                app.addObserver(object: ProfileCardObserver() {
                    override fun onGetProfileCard(success: Boolean, obj: Any) {
                        app.removeObserver(this)
                        if (!success || obj !is Card) {
                            it.resume(null)
                        } else {
                            dataService.saveProfileCard(obj)
                            it.resume(obj)
                        }
                    }
                })
                app.getRuntimeService(IProfileProtocolService::class.java, "all")
                    .requestProfileCard(app.currentUin, uin, 12, 0L, 0.toByte(), 0L, 0L, null, "", 0L, 10004, null, 0.toByte())
            }
        }
        return if (card == null) {
            Result.failure(Exception("unable to fetch profile card"))
        } else {
            Result.success(card)
        }
    }

}