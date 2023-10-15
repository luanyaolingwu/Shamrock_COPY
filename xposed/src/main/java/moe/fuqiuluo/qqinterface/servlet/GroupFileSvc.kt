package moe.fuqiuluo.qqinterface.servlet

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.fuqiuluo.proto.protobufOf
import tencent.im.oidb.cmd0x6d8.oidb_0x6d8
import tencent.im.oidb.oidb_sso

internal object GroupFileSvc: BaseSvc() {

    fun createFileFolder(groupId: String, folderName: String) {
        sendOidb("OidbSvc.0x6d7_0", 1751, 0, protobufOf(
            1 to mapOf(
                1 to groupId.toLong(),
                2 to 3,
                3 to "/",
                4 to folderName
            )
        ).toByteArray())
    }

    fun deleteGroupFolder(groupId: String, folderUid: String) {
        sendOidb("OidbSvc.0x6d7_1", 1751, 1, protobufOf(
            2 to mapOf(
                1 to groupId.toLong(),
                2 to 3,
                3 to folderUid,
            )
        ).toByteArray())
    }

    fun deleteGroupFile(groupId: String, bizId: Int, fileUid: String) {
        sendOidb("OidbSvc.0x6d6_3", 1750, 3, protobufOf(
            4 to mapOf(
                1 to groupId.toLong(),
                2 to 3,
                3 to bizId,
                4 to "/",
                5 to fileUid
            )
        ).toByteArray())
    }

    suspend fun getGroupFileSystemInfo(groupId: Long): FileSystemInfo {
        val rspGetFileCntBuffer = sendOidbAW("OidbSvc.0x6d8_1", 1752, 2, oidb_0x6d8.ReqBody().also {
            it.group_file_cnt_req = oidb_0x6d8.GetFileCountReqBody().also {
                it.uint64_group_code.set(groupId)
                it.uint32_app_id.set(3)
                it.uint32_bus_id.set(0)
            }
        }.toByteArray())
        val fileCnt: Int
        val limitCnt: Int
        if (rspGetFileCntBuffer != null) {
            oidb_0x6d8.RspBody().mergeFrom(oidb_sso.OIDBSSOPkg()
                .mergeFrom(rspGetFileCntBuffer)
                .bytes_bodybuffer.get()
                .toByteArray()).group_file_cnt_rsp.also {
                fileCnt = it.uint32_all_file_count.get()
                limitCnt = it.uint32_limit_count.get()
            }
        } else {
            throw RuntimeException("获取群文件数量失败")
        }

        val rspGetFileSpaceBuffer = sendOidbAW("OidbSvc.0x6d8_1", 1752, 3, oidb_0x6d8.ReqBody().also {
            it.group_space_req = oidb_0x6d8.GetSpaceReqBody().also {
                it.uint64_group_code.set(groupId)
                it.uint32_app_id.set(3)
            }
        }.toByteArray())
        val totalSpace: Long
        val usedSpace: Long
        if (rspGetFileSpaceBuffer != null) {
            oidb_0x6d8.RspBody().mergeFrom(oidb_sso.OIDBSSOPkg()
                .mergeFrom(rspGetFileSpaceBuffer)
                .bytes_bodybuffer.get()
                .toByteArray()).group_space_rsp.also {
                totalSpace = it.uint64_total_space.get()
                usedSpace = it.uint64_used_space.get()
            }
        } else {
            throw RuntimeException("获取群文件空间失败")
        }

        return FileSystemInfo(
            fileCnt, limitCnt, usedSpace, totalSpace
        )
    }

    @Serializable
    data class FileSystemInfo(
        @SerialName("file_count") val fileCount: Int,
        @SerialName("limit_count") val fileLimitCount: Int,
        @SerialName("used_space") val usedSpace: Long,
        @SerialName("total_space") val totalSpace: Long,
    )
}