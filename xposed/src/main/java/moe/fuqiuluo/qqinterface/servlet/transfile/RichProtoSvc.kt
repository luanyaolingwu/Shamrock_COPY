package moe.fuqiuluo.qqinterface.servlet.transfile

import com.tencent.mobileqq.transfile.FileMsg
import com.tencent.mobileqq.transfile.api.IProtoReqManager
import com.tencent.mobileqq.transfile.protohandler.RichProto
import com.tencent.mobileqq.transfile.protohandler.RichProtoProc
import io.ktor.server.util.url
import kotlinx.coroutines.suspendCancellableCoroutine
import moe.fuqiuluo.proto.ProtoUtils
import moe.fuqiuluo.proto.asByteArray
import moe.fuqiuluo.proto.asInt
import moe.fuqiuluo.proto.asUtf8String
import moe.fuqiuluo.proto.protobufOf
import moe.fuqiuluo.qqinterface.servlet.BaseSvc
import moe.fuqiuluo.shamrock.tools.hex2ByteArray
import moe.fuqiuluo.shamrock.utils.PlatformUtils
import moe.fuqiuluo.shamrock.helper.Level
import moe.fuqiuluo.shamrock.helper.LogCenter
import moe.fuqiuluo.shamrock.tools.slice
import moe.fuqiuluo.shamrock.tools.toHexString
import mqq.app.MobileQQ
import tencent.im.oidb.oidb_sso
import kotlin.coroutines.resume

internal object RichProtoSvc: BaseSvc() {
    suspend fun getGroupFileDownUrl(
        peerId: String,
        fileId: String,
        bizId: Int = 102
    ): String {
        val buffer = sendOidbAW("OidbSvcTrpcTcp.0x6d6_2", 1750, 2, protobufOf(
            3 to mapOf(
                1 to peerId.toLong(),
                2 to 3,
                3 to bizId,
                4 to fileId,
            )
        ).toByteArray())
        if (buffer == null) {
            return ""
        } else {
            val body = oidb_sso.OIDBSSOPkg()
            body.mergeFrom(buffer.slice(4))
            val result = ProtoUtils.decodeFromByteArray(body.bytes_bodybuffer.get().toByteArray())

            if (body.uint32_result.get() != 0 || result[3, 1].asInt != 0) {
                return ""
            }

            val domain = if (result.has(3, 4)) result[3, 4].asUtf8String else result[3, 5].asUtf8String
            val downloadUrl = result[3, 6].asByteArray.toHexString()
            val appId = MobileQQ.getMobileQQ().appId
            val version = PlatformUtils.getQQVersion(MobileQQ.getContext())

            return "https://$domain/ftn_handler/$downloadUrl/?fname=$fileId&client_proto=qq&client_appid=$appId&client_type=android&client_ver=$version&client_down_type=auto&client_aio_type=unk"
        }
    }

    fun getGroupPicDownUrl(
        md5: String
    ): String {
        return "http://gchat.qpic.cn/gchatpic_new/0/0-0-${md5.uppercase()}/0?term=2"
    }

    fun getC2CPicDownUrl(
        md5: String
    ): String {
        return "https://c2cpicdw.qpic.cn/offpic_new/0/123-0-${md5.uppercase()}/0?term=2"
    }

    suspend fun getC2CVideoDownUrl(
        peerId: String,
        md5Hex: String,
        fileUUId: String
    ): String {
        return suspendCancellableCoroutine {
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val richProtoReq = RichProto.RichProtoReq()
            val downReq: RichProto.RichProtoReq.ShortVideoDownReq = RichProto.RichProtoReq.ShortVideoDownReq()
            downReq.selfUin = runtime.currentAccountUin
            downReq.peerUin = peerId
            downReq.secondUin = peerId
            downReq.uinType = FileMsg.UIN_BUDDY
            downReq.agentType = 0
            downReq.chatType = 1
            downReq.troopUin = peerId
            downReq.clientType = 2
            downReq.fileId = fileUUId
            downReq.md5 = md5Hex.hex2ByteArray()
            downReq.busiType = FileTransfer.BUSI_TYPE_SHORT_VIDEO
            downReq.subBusiType = 0
            downReq.fileType = FileTransfer.VIDEO_FORMAT_MP4
            downReq.downType = 1
            downReq.sceneType = 1
            richProtoReq.callback = RichProtoProc.RichProtoCallback { _, resp ->
                if (resp.resps.isEmpty() || resp.resps.first().errCode != 0) {
                    LogCenter.log("requestDownPrivateVideo: ${resp.resps.firstOrNull()?.errCode}", Level.WARN)
                    it.resume("")
                } else {
                    val videoDownResp = resp.resps.first() as RichProto.RichProtoResp.ShortVideoDownResp
                    val url = StringBuilder()
                    url.append(videoDownResp.mIpList.random().getServerUrl("http://"))
                    url.append(videoDownResp.mUrl)
                    it.resume(url.toString())
                }
            }
            richProtoReq.protoKey = RichProtoProc.SHORT_VIDEO_DW
            richProtoReq.reqs.add(downReq)
            richProtoReq.protoReqMgr = runtime.getRuntimeService(IProtoReqManager::class.java, "all")
            RichProtoProc.procRichProtoReq(richProtoReq)
        }
    }

    suspend fun getGroupVideoDownUrl(
        peerId: String,
        md5Hex: String,
        fileUUId: String
    ): String {
        return suspendCancellableCoroutine {
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val richProtoReq = RichProto.RichProtoReq()
            val downReq: RichProto.RichProtoReq.ShortVideoDownReq = RichProto.RichProtoReq.ShortVideoDownReq()
            downReq.selfUin = runtime.currentAccountUin
            downReq.peerUin = peerId
            downReq.secondUin = peerId
            downReq.uinType = FileMsg.UIN_TROOP
            downReq.agentType = 0
            downReq.chatType = 1
            downReq.troopUin = peerId
            downReq.clientType = 2
            downReq.fileId = fileUUId
            downReq.md5 = md5Hex.hex2ByteArray()
            downReq.busiType = FileTransfer.BUSI_TYPE_SHORT_VIDEO
            downReq.subBusiType = 0
            downReq.fileType = FileTransfer.VIDEO_FORMAT_MP4
            downReq.downType = 1
            downReq.sceneType = 1
            richProtoReq.callback = RichProtoProc.RichProtoCallback { _, resp ->
                if (resp.resps.isEmpty() || resp.resps.first().errCode != 0) {
                    LogCenter.log("requestDownGroupVideo: ${resp.resps.firstOrNull()?.errCode}", Level.WARN)
                    it.resume("")
                } else {
                    val videoDownResp = resp.resps.first() as RichProto.RichProtoResp.ShortVideoDownResp
                    val url = StringBuilder()
                    url.append(videoDownResp.mIpList.random().getServerUrl("http://"))
                    url.append(videoDownResp.mUrl)
                    it.resume(url.toString())
                }
            }
            richProtoReq.protoKey = RichProtoProc.SHORT_VIDEO_DW
            richProtoReq.reqs.add(downReq)
            richProtoReq.protoReqMgr = runtime.getRuntimeService(IProtoReqManager::class.java, "all")
            RichProtoProc.procRichProtoReq(richProtoReq)
        }
    }

    suspend fun getC2CPttDownUrl(
        peerId: String,
        fileUUId: String
    ): String {
        return suspendCancellableCoroutine {
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val richProtoReq = RichProto.RichProtoReq()
            val pttDownReq: RichProto.RichProtoReq.C2CPttDownReq = RichProto.RichProtoReq.C2CPttDownReq()
            pttDownReq.selfUin = runtime.currentAccountUin
            pttDownReq.peerUin = peerId
            pttDownReq.secondUin = peerId
            pttDownReq.uinType = FileMsg.UIN_BUDDY
            pttDownReq.busiType = 1002
            pttDownReq.uuid = fileUUId
            pttDownReq.storageSource = "pttcenter"
            pttDownReq.isSelfSend = false

            pttDownReq.voiceType = 1
            pttDownReq.downType = 1
            richProtoReq.callback = RichProtoProc.RichProtoCallback { _, resp ->
                if (resp.resps.isEmpty() || resp.resps.first().errCode != 0) {
                    LogCenter.log("requestDownPrivateVoice: ${resp.resps.firstOrNull()?.errCode}", Level.WARN)
                    it.resume("")
                } else {
                    val pttDownResp = resp.resps.first() as RichProto.RichProtoResp.C2CPttDownResp
                    val url = StringBuilder()
                    url.append(pttDownResp.downloadUrl)
                    url.append("&client_proto=qq&client_appid=${MobileQQ.getMobileQQ().appId}&client_type=android&client_ver=${PlatformUtils.getQQVersion(MobileQQ.getContext())}&client_down_type=auto&client_aio_type=unk")
                    it.resume(url.toString())
                }
            }
            richProtoReq.protoKey = RichProtoProc.C2C_PTT_DW
            richProtoReq.reqs.add(pttDownReq)
            richProtoReq.protoReqMgr = runtime.getRuntimeService(IProtoReqManager::class.java, "all")
            RichProtoProc.procRichProtoReq(richProtoReq)
        }
    }

    suspend fun getGroupPttDownUrl(
        peerId: String,
        md5Hex: String,
        fileUUId: String
    ): String {
        return suspendCancellableCoroutine {
            val runtime = MobileQQ.getMobileQQ().waitAppRuntime()
            val richProtoReq = RichProto.RichProtoReq()
            val groupPttDownReq: RichProto.RichProtoReq.GroupPttDownReq = RichProto.RichProtoReq.GroupPttDownReq()
            groupPttDownReq.selfUin = runtime.currentAccountUin
            groupPttDownReq.peerUin = peerId
            groupPttDownReq.secondUin = peerId
            groupPttDownReq.uinType = FileMsg.UIN_TROOP
            groupPttDownReq.groupFileID = 0
            groupPttDownReq.groupFileKey = fileUUId
            groupPttDownReq.md5 = md5Hex.hex2ByteArray()
            groupPttDownReq.voiceType = 1
            groupPttDownReq.downType = 1
            richProtoReq.callback = RichProtoProc.RichProtoCallback { _, resp ->
                if (resp.resps.isEmpty() || resp.resps.first().errCode != 0) {
                    LogCenter.log("requestDownGroupVoice: ${resp.resps.firstOrNull()?.errCode}", Level.WARN)
                    it.resume("")
                } else {
                    val pttDownResp = resp.resps.first() as RichProto.RichProtoResp.GroupPttDownResp
                    val url = StringBuilder()
                    url.append("http://")
                    url.append(pttDownResp.domainV4V6)
                    url.append(pttDownResp.urlPath)
                    url.append("&client_proto=qq&client_appid=${MobileQQ.getMobileQQ().appId}&client_type=android&client_ver=${
                        PlatformUtils.getQQVersion(
                            MobileQQ.getContext())}&client_down_type=auto&client_aio_type=unk")
                    it.resume(url.toString())
                }
            }
            richProtoReq.protoKey = RichProtoProc.GRP_PTT_DW
            richProtoReq.reqs.add(groupPttDownReq)
            richProtoReq.protoReqMgr = runtime.getRuntimeService(IProtoReqManager::class.java, "all")
            RichProtoProc.procRichProtoReq(richProtoReq)
        }
    }

}