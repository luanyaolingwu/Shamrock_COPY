package moe.fuqiuluo.shamrock.remote.action.handlers

import moe.fuqiuluo.qqinterface.servlet.GroupSvc
import moe.fuqiuluo.shamrock.remote.action.ActionSession
import moe.fuqiuluo.shamrock.remote.action.IActionHandler
import moe.fuqiuluo.shamrock.remote.service.data.SimpleTroopInfo

internal object GetTroopList: IActionHandler() {
    override suspend fun internalHandle(session: ActionSession): String {
        val refresh = session.getBooleanOrDefault("refresh", true)
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