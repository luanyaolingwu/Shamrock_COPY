package moe.fuqiuluo.remote.action

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import moe.fuqiuluo.remote.action.handlers.*
import moe.fuqiuluo.remote.entries.EmptyObject
import moe.fuqiuluo.remote.entries.Status
import moe.fuqiuluo.remote.entries.resultToString
import moe.fuqiuluo.xposed.tools.*

internal object ActionManager {
    val actionMap = mutableMapOf(
        "test" to TestHandler,
        "get_latest_events" to GetLatestEvents,
        "get_supported_actions" to GetSupportedActions,
        "get_status" to GetStatus,
        "get_version" to GetVersion,
        "get_self_info" to GetSelfInfo,
        "get_user_info" to GetProfileCard,
        "get_friend_list" to GetFriendList,
        "get_group_info" to GetTroopInfo,
        "get_group_list" to GetTroopList,
        "get_group_member_info" to GetTroopMemberInfo,
        "get_group_member_list" to GetTroopMemberList,
        "set_group_name" to ModifyTroopName,
        "leave_group" to LeaveTroop,
        "send_message" to SendMessage,
        "get_uid" to GetUid,
        "get_uin_by_uid" to GetUinByUid,
        "delete_message" to DeleteMessage,
        "sanc_qrcode" to ScanQRCode,
        "set_qq_profile" to SetProfileCard,
        "get_msg" to GetMsg,
        "get_forward_msg" to GetForwardMsg,
        "send_like" to SendLike,
        "set_group_kick" to KickTroopMember,
        "set_group_ban" to BanTroopMember
    )

    operator fun get(action: String): IActionHandler? {
        return actionMap[action]
    }
}

internal abstract class IActionHandler {
    abstract suspend fun handle(session: ActionSession): String

    abstract fun path(): String

    fun ok(msg: String = ""): String {
        return resultToString(true, Status.Ok, EmptyObject, msg)
    }

    inline fun <reified T> ok(data: T, msg: String = ""): String {
        return resultToString(true, Status.Ok, data!!, msg)
    }

    fun noParam(paramName: String): String {
        return failed(Status.BadParam, "lack of [$paramName]")
    }

    fun badParam(why: String): String {
        return failed(Status.BadParam, why)
    }

    fun error(why: String): String {
        return failed(Status.InternalHandlerError, why)
    }

    fun logic(why: String): String {
        return failed(Status.LogicError, why)
    }

    fun failed(status: Status, msg: String): String {
        return resultToString(false, status, EmptyObject, msg)
    }
}

internal class ActionSession {
    private val params: JsonObject

    constructor(values: Map<String, Any?>) {
        val map = hashMapOf<String, JsonElement>()
        values.forEach { (key, value) ->
            if (value != null) {
                when (value) {
                    is String -> map[key] = value.json
                    is Number -> map[key] = value.json
                    is Char -> map[key] = JsonPrimitive(value.code.toByte())
                    is Boolean -> map[key] = value.json
                    is JsonObject -> map[key] = value
                    is JsonArray -> map[key] = value
                    else -> error("unsupported type: ${value::class.java}")
                }
            }
        }
        this.params = JsonObject(map)
    }

    constructor(params: JsonObject) {
        this.params = params
    }

    fun getLong(key: String): Long {
        return params[key].asLong
    }

    fun getLongOrNull(key: String): Long? {
        return params[key].asLongOrNull
    }

    fun getInt(key: String): Int {
        return params[key].asInt
    }

    fun getIntOrNull(key: String): Int? {
        return params[key].asIntOrNull
    }

    fun isString(key: String): Boolean {
       val element = params[key]
        return element is JsonPrimitive && element.isString
    }

    fun isArray(key: String): Boolean {
        val element = params[key]
        return element is JsonArray
    }

    fun isObject(key: String): Boolean {
        val element = params[key]
        return element is JsonObject
    }

    fun getString(key: String): String {
        return params[key].asString
    }

    fun getStringOrNull(key: String): String? {
        return params[key].asStringOrNull
    }

    fun getBoolean(key: String): Boolean {
        return params[key].asBoolean
    }

    fun <T: Boolean?> getBooleanOrDefault(key: String, default: T? = null): T {
        return (params[key].asBooleanOrNull as? T) ?: default as T
    }

    fun getObject(key: String): JsonObject {
        return params[key].asJsonObject
    }

    fun getObjectOrNull(key: String): JsonObject? {
        return params[key].asJsonObjectOrNull
    }

    fun getArray(key: String): JsonArray {
        return params[key].asJsonArray
    }

    fun getArrayOrNull(key: String): JsonArray? {
        return params[key].asJsonArrayOrNull
    }

    fun has(key: String) = params.containsKey(key)
}