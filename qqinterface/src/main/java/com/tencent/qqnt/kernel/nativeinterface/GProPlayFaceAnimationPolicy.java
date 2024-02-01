package com.tencent.qqnt.kernel.nativeinterface;

public  final class GProPlayFaceAnimationPolicy {
    int userIdleTime;

    public GProPlayFaceAnimationPolicy() {
    }

    public int getUserIdleTime() {
        return this.userIdleTime;
    }

    public String toString() {
        return "GProPlayFaceAnimationPolicy{userIdleTime=" + this.userIdleTime + ",}";
    }

    public GProPlayFaceAnimationPolicy(int i2) {
        this.userIdleTime = i2;
    }
}
