package moe.fuqiuluo.shamrock.remote.action.handlers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import moe.fuqiuluo.qqinterface.servlet.GroupSvc
import moe.fuqiuluo.qqinterface.servlet.structures.GuildInfo
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler
import moe.fuqiuluo.shamrock.tools.EmptyJsonString
import moe.fuqiuluo.symbols.OneBotHandler

@OneBotHandler("send_group_sign")
internal object SendGroupSign: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val groupId = session.getLong("group_id")
        return invoke(groupId, session.echo)
    }

    suspend operator fun invoke(groupId: Long, echo: JsonElement = EmptyJsonString): String {
        val ret = GroupSvc.groupSign(groupId)
        return if (ret.isSuccess) {
            ok(Message(message = ret.getOrNull() ?: ""), echo, "成功")
        } else {
            logic(ret.exceptionOrNull()?.message ?: "", echo)
        }
    }

    override val requiredParams: Array<String> = arrayOf("group_id")

    @Serializable
    data class Message(
        @SerialName("message") var message: String = ""
    )
}