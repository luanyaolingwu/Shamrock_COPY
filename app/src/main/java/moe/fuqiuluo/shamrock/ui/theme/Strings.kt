@file:Suppress(
    "SpellCheckingInspection", "unused", "PropertyName",
    "ClassName", "NonAsciiCharacters"
)
package moe.fuqiuluo.shamrock.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import moe.fuqiuluo.shamrock.R

private val LocalStringDefault = Default()
private val LocalString2B = Chūnibyō()

val RANDOM_TITLE = arrayOf(
    "Clover", "CuteKitty", "Shamrock",
    "Threeleaf", "CuteCat", "FuckingCat",
    "XVideos", "Onlyfans", "Pornhub",
    "Xposed", "LittleFox", "Springboot",
    "Kotlin", "Rust & Android", "Dashabi",
    "YYDS", "Amd Yes", "Gayhub",
    "Yuzukkity", "HongKongDoll", "Xinrao",
    "404 Not Found","TX.SB","Minecraft"
)
val RANDOM_SUB_TITLE = arrayOf(
    "A Framework Base On Xposed",
    "今天吃什么好呢?",
    "遇事不决，量子力学!",
    "Just kkb?",
    "いいよ，こいよ",
    "伊已逝 吾亦逝",
    "忆久意久 把义领",
    "喵帕斯!",
    "Creeper?",
    "Make American Great Again!",
    "TXHookPro",
    "曾经有人失去了那个她",
    "欲买桂花同载酒，终不似，少年游。",
    "抚千窟为佑 看长安落花",
    "どこにもない",
    "春日和 かかってらしゃい",
    "java.lang.NullPointerException"
)

val LocalString: VarString
    @ReadOnlyComposable
    @Composable
    get() {
        val ctx = LocalContext.current
        val sharedPreferences = ctx.getSharedPreferences("config", 0)
        return if (!sharedPreferences.getBoolean("2B", false)) {
            LocalStringDefault
        } else {
            LocalString2B
        }
    }

private open class Chūnibyō: Default() {
    init {
        TitlesWithIcon = arrayOf(
            "喵窝" to R.drawable.round_home_24,
            "喵况" to R.drawable.round_dashboard_24,
            "喵咪" to R.drawable.round_monitor_heart_24,
            "喵嘻" to R.drawable.round_logo_dev_24
        )
        frameworkYes = "在玩毛线球"
        frameworkNo = "寻找毛线球"
        frameworkYesLite = "开心喵 ~"
        frameworkNoLite = "哭哭喵 ~"
        legalWarning = "只能作用在8.9.68或者更高的版本上哦，不然会出问题的。\n" +
        "还有，这个软件只是用来学习和交流的，不要用来做坏事哦，要是被发现了，依凌可不负责。\n" +
        "还有还有，这个软件是很多好心大哥哥大姐姐一起做的，他们都很厉害，也很好心，你要尊重他们哦。\n" +
        "如果你犯错了，要负全责的哦~ 所以，答应猫猫，不要用于违规用途哦~"
        labWarning = "实验室功能，有很多新奇的东西，不过也有很多危险的东西，你要小心点哦，不要乱动，不然会出大事的。"
        logTitle = "喵咪"
        testName = "未闻猫名"
        logCentralLoadSuccessfully = "喵咪 - 喵力已满，可以开始玩耍了 o(*￣▽￣*)ブ"
        logCentralLoadFailed = "喵咪 - 喵力不足，需要充电 ＞﹏＜"
        functionSetting = "喵喵功能"
        sslSetting = "喵力加密"
        warnTitle = "喵喵提醒"
        b2Mode = "喵喵模式"
        b2ModeDesc = "你已经召唤过依凌啦~ 肉球再点一下猫猫就要离开了哦~"
        restartToast = "重启之后才会生效喵！"
        showDebugLog = "喵咪Plus"
        showDebugLogDesc = "这个功能会让猫猫变得更健谈，可能会有点乱的喵。 qwq"
        antiTrace = "喵喵隐身"
        antiTraceDesc = "有坏人想伤害跟猫猫，猫猫会尽量保护自己的 (开启隐身之后记得重新打开QQ哦)"
        injectPacket = "喵喵拦截"
        injectPacketDesc = "防止部分过敏原接触Master，防止过敏"
        persistentText = "喵喵保护"
        persistentTextDesc = "喵喵会一直陪着Master，直到永远 永远~"
    }
}

private open class Default: VarString(
    TitlesWithIcon = arrayOf(
        "主页" to R.drawable.round_home_24,
        "状态" to R.drawable.round_dashboard_24,
        "日志" to R.drawable.round_monitor_heart_24,
        "Lab" to R.drawable.round_logo_dev_24
    ), "框架已激活", "框架未激活",
    "已激活", "未激活",
    legalWarning = "该模块仅适用于目标版本8.9.68及以上的版本。\n" +
            "同时声明本项目仅用于学习与交流，请于24小时内删除。\n" +
            "同时开源贡献者均享受免责条例。",
    labWarning = "实验室功能，可能会导致出乎意料的BUG!",
    logTitle = "日志",
    testName = "测试昵称",
    logCentralLoadSuccessfully = "日志框架激活成功，开放操作许可。",
    logCentralLoadFailed = "日志框架处于未激活状态，请检查。",
    functionSetting = "功能设置",
    sslSetting = "SSL配置",
    warnTitle = "温馨提示",
    b2Mode = "中二病模式",
    b2ModeDesc = "也许会导致奇怪的问题，大抵就是你看不懂罢了。",
    restartToast = "重启生效哦！",
    restartSysToast = "重启系统生效哦！",
    showDebugLog = "显示调试日志",
    showDebugLogDesc = "会导致日志刷屏。",
    antiTrace = "防止调用栈检测",
    antiTraceDesc = "防止QQ进行堆栈跟踪检测，需要重新启动QQ。",
    injectPacket = "拦截QQ无用收包",
    injectPacketDesc = "测试阶段，可能导致网络异常或掉线。",
    persistentText = "免死金牌",
    persistentTextDesc = "由天地之起也，须复动之。"
)

open class VarString(
    var TitlesWithIcon: Array<Pair<String, Int>>,
    var frameworkYes: String,
    var frameworkNo: String,

    var frameworkYesLite: String,
    var frameworkNoLite: String,

    var legalWarning: String,

    var labWarning: String,

//     var logTitle: String
// )
    var logTitle: String,

    var testName: String,

    var logCentralLoadSuccessfully: String,
    var logCentralLoadFailed: String,

    var functionSetting: String,
    var sslSetting: String,

    var warnTitle: String,

    var b2Mode: String,
    var b2ModeDesc: String,

    var restartToast: String,
    var restartSysToast: String,

    var showDebugLog: String,
    var showDebugLogDesc: String,

    var antiTrace: String,
    var antiTraceDesc: String,

    var injectPacket: String,
    var injectPacketDesc: String,

    var persistentText: String,
    var persistentTextDesc: String
) {
    private var inited = false

    @Composable
    fun init(): VarString {
        if (inited) return this

        inited = true
        return this
    }
}
