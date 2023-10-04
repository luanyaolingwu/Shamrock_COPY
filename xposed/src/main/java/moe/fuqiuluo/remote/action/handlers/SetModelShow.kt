package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.protocol.servlet.CardSvc

internal object SetModelShow: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val model = session.getString("model")
        val manu = session.getString("manu")
        val modelshow = session.getString("modelshow")
        val imei = session.getString("imei")
        val show = session.getBoolean("show")
        return invoke(model, manu, modelshow, imei, show, session.echo)
    }

    suspend operator fun invoke(model: String, manu: String, modelshow: String, imei: String, show: Boolean, echo: String = ""): String {
        CardSvc.setModelShow(model, manu, modelshow, imei, show)
        return ok("成功", echo = echo)
    }

    override val requiredParams: Array<String> = arrayOf("model", "manu", "modelshow", "imei", "show")

    override fun path(): String = "_set_model_show"
}