// IQSigner.aidl
package moe.fuqiuluo.xposed.ipc.qsign;

import moe.fuqiuluo.xposed.ipc.qsign.IQSign;

interface IQSigner {
    IQSign sign(String cmd, int seq, String uin, in byte[] buffer);

    byte[] energy(String module, in byte[] salt);

    byte[] xwDebugId(String uin, String start, String end);
}