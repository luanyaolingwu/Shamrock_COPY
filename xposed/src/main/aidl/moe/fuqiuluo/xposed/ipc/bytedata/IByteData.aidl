// IByteData.aidl
package moe.fuqiuluo.xposed.ipc.bytedata;

import moe.fuqiuluo.xposed.ipc.bytedata.IByteDataSign;

interface IByteData {
    IByteDataSign sign(String uin, String data, in byte[] salt);
}