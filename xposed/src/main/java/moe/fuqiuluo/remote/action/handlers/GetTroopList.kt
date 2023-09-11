package moe.fuqiuluo.remote.action.handlers

import moe.fuqiuluo.remote.action.ActionSession
import moe.fuqiuluo.remote.action.IActionHandler
import moe.protocol.service.data.SimpleTroopInfo
import moe.protocol.servlet.GroupSvc

internal object GetTroopList: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val refresh = session.getBooleanOrDefault("refresh", false)
        return invoke(refresh, session.echo)
    }

    suspend operator fun invoke(refresh: Boolean, echo: String = ""): String {
        val troopList = arrayListOf<SimpleTroopInfo>()
        GroupSvc.getGroupList(refresh).onFailure {
            return error(it.message ?: "unknown error", echo)
        }.onSuccess { troops ->
            troops.forEach { groupInfo ->
                troopList.add(SimpleTroopInfo(
                    groupId = groupInfo.troopuin,
                    groupUin = groupInfo.troopcode,
                    groupName = groupInfo.troopname ?: groupInfo.newTroopName ?: groupInfo.oldTroopName,
                    groupRemark = groupInfo.troopRemark,
                    adminList = GroupSvc.getAdminList(groupInfo.troopuin, true),
                    classText = groupInfo.mGroupClassExtText,
                    isFrozen = groupInfo.mIsFreezed != 0,
                    maxMember = groupInfo.wMemberMax,
                    memNum = groupInfo.wMemberNum,
                    memCount = groupInfo.wMemberNum,
                    maxNum = groupInfo.wMemberMax
                ))
            }
        }
        return ok(troopList, echo)
    }

    override fun path(): String = "get_group_list"
}