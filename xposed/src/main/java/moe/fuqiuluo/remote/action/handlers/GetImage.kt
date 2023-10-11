package moe.fuqiuluo.remote.action.handlers

import kotlinx.serialization.Serializable
import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler

internal object GetImage: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val echo = session.echo
        val file = session.getString("file")
        return invoke(file, echo)
    }

    operator fun invoke(file: String, echo: String = ""): String {
        return ""
    }

    override val requiredParams: Array<String> = arrayOf("file")

    override fun path(): String = "get_image"

    @Serializable
    data class GetImageResult(
        val size: Long,
        val filename: String,
        val url: String
    )
}