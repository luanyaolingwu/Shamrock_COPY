package moe.fuqiuluo.qqinterface.servlet

import moe.fuqiuluo.proto.protobufOf

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


}