#include <jni.h>
#include <string>
#include <utility>
#include <sys/auxv.h>
/*
/* 通过移位Bit Mask来读取相应的标志位
#define BIT_SHIFTS(n)			(uint64_t(1) << (n))

uint64_t readFRQ()
{
    uint64_t value = 0;
    //asm volatile("mrs %0, cntfrq_el0 \n"
    //             "mrs x9, cntvct_el0": "=r"(value));

    return value;
}

uint64_t hwcapsByAsm() {
    uint64_t value = 0;
    //asm volatile("mrs %[result], ID_AA64PFR0_EL1 "
    //            : [result] "=r"(value));
    return value;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_moe_protocol_servlet_utils_PlatformUtils_outCpuInfo(JNIEnv *env, jobject thiz) {
    unsigned long hwcaps = getauxval(AT_HWCAP);
    int i;
    char *list[32] = {"fp", "asimd", "evtstrm", "aes", "pmull", "sha1",
                      "sha2", "crc32", "atomics", "fphp", "asimdhp",
                      "cpuid", "asimdrdm","jscvt", "fcma", "lrcpc",
                      "dcpop", "sha3", "sm3", "sm4", "asimddp" ,
                      "sha512", "sve", "asimdfhm", "dit", "uscat",
                      "ilrcpc", "flagm", "ssbs", "sb", "paca","pacg",};
    std::string out;
    out += "flags: ";
    for (i = 0; i< 32; i++){
        if (hwcaps & BIT_SHIFTS(i)) {
            out += list[i];
            out += ", ";
        }
    }

    out += "\ncntfrq_el0: ";
    out += std::to_string(readFRQ());

    hwcaps = hwcapsByAsm();
    out += "flags_asm: ";
    for (i = 0; i< 32; i++){
        if (hwcaps & BIT_SHIFTS(i)) {
            out += list[i];
            out += ", ";
        }
    }

    return env->NewStringUTF(out.c_str());
}
*/