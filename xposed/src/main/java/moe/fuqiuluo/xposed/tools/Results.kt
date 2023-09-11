package moe.fuqiuluo.xposed.tools

fun Result<*>.errMsg(): String {
    return this.exceptionOrNull()?.message ?: exceptionOrNull().toString()
}