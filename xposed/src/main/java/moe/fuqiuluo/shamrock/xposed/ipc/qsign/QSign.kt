package moe.fuqiuluo.shamrock.xposed.ipc.qsign

import android.os.Parcel
import android.os.Parcelable
import com.tencent.mobileqq.fe.FEKit
import com.tencent.mobileqq.qsec.qsecdandelionsdk.Dandelion
import com.tencent.mobileqq.qsec.qsecurity.QSec
import moe.fuqiuluo.shamrock.xposed.ipc.qsign.IQSigner
import mqq.app.MobileQQ

data class IQSign(
    val token: ByteArray,
    val extra: ByteArray,
    val sign: ByteArray
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.createByteArray()!!,
        parcel.createByteArray()!!,
        parcel.createByteArray()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByteArray(token)
        parcel.writeByteArray(extra)
        parcel.writeByteArray(sign)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<IQSign> {
        override fun createFromParcel(parcel: Parcel): IQSign {
            return IQSign(parcel)
        }

        override fun newArray(size: Int): Array<IQSign?> {
            return arrayOfNulls(size)
        }
    }
}

internal object QSignGenerator: IQSigner.Stub() {
    override fun sign(cmd: String, seq: Int, uin: String, buffer: ByteArray): IQSign {
        val sign = FEKit.getInstance().getSign(cmd, buffer, seq, uin)
        return IQSign(sign.token, sign.extra, sign.sign,)
    }

    override fun energy(module: String, salt: ByteArray): ByteArray {
        return Dandelion.getInstance().fly(module, salt)
    }

    override fun xwDebugId(uin: String, start: String, end: String): ByteArray {
        return QSec.getInstance().getFeKitAttach(MobileQQ.getContext(), uin, start, end)
    }

    override fun getCmdWhiteList(): List<String> {
        return FEKit.getInstance().cmdWhiteList
    }
}