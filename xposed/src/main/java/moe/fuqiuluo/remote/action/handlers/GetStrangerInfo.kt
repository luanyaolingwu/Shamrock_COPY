package moe.fuqiuluo.remote.action.handlers

import com.tencent.mobileqq.data.Card
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.fuqiuluo.xposed.tools.toHexString
import moe.protocol.servlet.CardSvc

internal object GetStrangerInfo: IActionHandler() {
    override fun path(): String = "_get_stranger_info"

    override suspend fun internalHandle(session: ActionSession): String {
        val uid = session.getString("user_id")
        return invoke(uid, session.echo)
    }

    suspend operator fun invoke(userId: String, echo: String = ""): String {
        val info = CardSvc.getProfileCard(userId).onFailure {
            return logic("unable to fetch stranger info", echo)
        }.getOrThrow()

        return ok(StrangerInfo(
            userId,
            info.strNick,
            info.age,
            when(info.shGender) {
                Card.FEMALE -> "female"
                Card.MALE -> "male"
                else -> "unknown"
            },
            info.iQQLevel,
            info.lLoginDays,
            info.qid ?: "",
            info.lVoteCount,
            info.wzryHonorInfo?.toHexString()
        ), echo)
    }

    override val requiredParams: Array<String> = arrayOf("user_id")

    @Serializable
    data class StrangerInfo(
        @SerialName("user_id") val uid: String,
        @SerialName("nickname") val nickname: String,
        @SerialName("age") val age: Byte,
        @SerialName("sex") val sex: String,
        @SerialName("level") val level: Int,
        @SerialName("login_days") val loginDays: Long,
        @SerialName("qid") val qid: String?,
        @SerialName("vote") val vote: Long,
        @SerialName("wzry_honor") val wzryHonor: String?
    )
}